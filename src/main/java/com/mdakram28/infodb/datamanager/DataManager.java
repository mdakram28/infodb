package com.mdakram28.infodb.datamanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.mdakram28.infodb.Info;
import com.mdakram28.infodb.InfoList;
import com.mdakram28.infodb.tools.scraper.ScrapingException;

@Service
public class DataManager {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired(required = false)
	List<IDataEndpoint> dataEndpoints;

	@Autowired(required = false)
	List<IKnowledgeEngine> knowledgeEngines;

	@Autowired
	private Environment env;

	@Value("${titlediff_threshold:5}")
	private int titleDiffThreshold;

	public boolean needsElevation(IDataEndpoint ds1, IDataEndpoint ds2) {
		int v1 = ds1.getClass().getAnnotation(Order.class).value();
		int v2 = ds2.getClass().getAnnotation(Order.class).value();
		return v2 > v1;
	}

	public boolean isEnabled(Object dataEndpoint) {
		String prop = env.getProperty(dataEndpoint.getClass().getSimpleName() + ".enabled");
		return prop == null || Boolean.parseBoolean(prop);
	}
	

	public List<List<IDataEndpoint>> clusterByOrder() {
		Map<Integer, List<IDataEndpoint>> map = dataEndpoints.stream().filter(this::isEnabled)
				.collect(Collectors.groupingBy(
						dataEndpoint -> dataEndpoint.getClass().getAnnotation(Order.class).value(), TreeMap::new,
						Collectors.toCollection(ArrayList::new)));

		return map.keySet().stream().sorted().map(order -> map.get(order)).collect(Collectors.toList());
	}

	@Async
	public Future<InfoList> getPersonDetailsByName(String key, boolean force) {
		logger.info("----------------" + " Fetching data for " + key + " ----------------");

		InfoList infoList = new InfoList(key);

		List<List<IDataEndpoint>> clusters = clusterByOrder();

		int layerIndex;

		for (layerIndex = 0; layerIndex < clusters.size(); layerIndex++) {
			List<IDataEndpoint> layer = clusters.get(layerIndex);
			boolean foundInfo = layer.parallelStream().filter(dataEndpoint -> dataEndpoint instanceof IDataSource)
					.map(dataEndpoint -> {
						logger.info(String.format("Fetching info from data source %s", dataEndpoint.getName()));
						try {
							List<Info> info = ((IDataSource) dataEndpoint).fetchInfo(key);
							if (info == null || info.size() == 0 || info.get(0) == null)
								throw new ScrapingException("Null value returned");
							else
								logger.info("Got info from " + dataEndpoint.getName());
							return info;
						} catch (Exception e) {
							logger.error("Fetching data failed at " + dataEndpoint.getName() + " : " + e.getMessage());
						}
						return null;
					}).filter(Objects::nonNull).flatMap(ll -> ll.stream()).filter(Objects::nonNull)
					.filter(info -> isSimilar(info.getTitle(), key)).map(infoList::addInfo).collect(Collectors.toList())
					.size() > 0;

			if (!force && foundInfo)
				break;
		}

		knowledgeEngines.forEach(ke -> {
			if(!isEnabled(ke)) return;
			try {
				logger.info(String.format("Running Knowledge Engine %s", ke.getClass().getSimpleName()));
				Object knowledge = ke.process(infoList);
				if (knowledge != null)
					infoList.addKnowledge(knowledge);
			} catch (Exception e) {
				logger.info(String.format("Knowledge Engine %s crashed : %s", ke.getClass().getSimpleName(),
						e.getMessage()));
			}
			
		});

		if (!infoList.getData().isEmpty()) {
			layerIndex--;
			for (; layerIndex >= 0; layerIndex--) {
				List<IDataEndpoint> layer = clusters.get(layerIndex);
				layer.parallelStream().filter(dataEndpoint -> dataEndpoint instanceof IDataSink)
						.forEach(dataEndpoint -> {
							logger.info(String.format("Storing info to data sink %s", dataEndpoint.getName()));
							try {
								((IDataSink) dataEndpoint).storeInfo(infoList);
							} catch (Exception e) {
								logger.error(
										"Fetching data failed at " + dataEndpoint.getName() + " : " + e.getMessage());
							}
						});
			}
		}

		logger.info("Info fetch complete found " + infoList.size() + " entries");
		return new AsyncResult<InfoList>(infoList);
	}

	private void combineInfo(String key, InfoList infoList, Map<IDataEndpoint, Future<Info>> waitingForInfo) {
		waitingForInfo.forEach((dataEndpoint, futureInfo) -> {
			try {
				Info info = futureInfo.get();
				if (info == null) {
					logger.info("Data not found in fetcher " + dataEndpoint.getName());
				} else if (!isSimilar(info.title, key)) {
					logger.info("Found title from " + dataEndpoint.getName() + " differs : " + info.title);
				} else {
					logger.info("Found info from " + dataEndpoint.getName());
					infoList.addInfo(info);
				}
			} catch (Exception e) {
				logger.error("Fetching async response failed at " + dataEndpoint.getName() + " : " + e.getMessage());
			}
		});
	}

	boolean isSimilar(String bigString, String smallString) {
		logger.info(String.format("Calculating distance between : %s and %s", bigString, smallString));
		int distance = StringUtils.getLevenshteinDistance(bigString.toLowerCase(), smallString.toLowerCase());
		System.out.println("Distance : " + distance);
		int ed = Math.abs(bigString.length() - smallString.length());
		int ad = distance - ed;

		return ad <= titleDiffThreshold;
	}

}
