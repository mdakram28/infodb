package com.mdakram28.infodb.tools.scraper;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.mdakram28.infodb.Info;
import com.mdakram28.infodb.InfoList;
import com.mdakram28.infodb.datamanager.IDataSource;

import edu.emory.mathcs.backport.java.util.Arrays;

@Component
@Order(value = 3)
public class WikipediaScraper implements IDataSource {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

	@Override
	public String getType() {
		return TYPE.SCRAPER;
	}

	@Override
	public List<Info> fetchInfo(String name) throws IOException, ScrapingException {
		Info info = new Info(this);

		String request = "https://en.wikipedia.org/w/index.php?search=" + name
				+ "&title=Special%3ASearch&profile=default&fulltext=1";
		logger.info("Sending request..." + request);

		Document doc = Jsoup.connect(request).userAgent(USER_AGENT).timeout(10000).get();

		info.setTitle(doc.selectFirst(".mw-search-result-heading a").text());
		String page = "https://en.wikipedia.org" + doc.selectFirst(".mw-search-result-heading a").attr("href");
		info.setSource(page);
		Map<String, Object> raw = new HashMap<>();
		scrapePage(page, raw);
		if (raw.size() == 0)
			throw new ScrapingException("Empty raw data");
		info.setRaw(raw);
		return Arrays.asList(new Info[]{info});
	}

	public void scrapePage(String url, Map<String, Object> info) throws IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(10000).get();
		Elements table = doc.select(".infobox tr");

		String heading = "";
		Map<String, String> underHeading = new HashMap<>();
		int i = 0;
		for (Element row : table) {
			try {
				boolean hasth = row.selectFirst("th") != null;
				boolean hastd = row.selectFirst("td") != null;
				String th = hasth ? row.selectFirst("th").text() : "";
				String td = hastd ? row.select("td").text() : "";

				th = th.trim().replaceAll("\\.", "");
				td = td.trim();

				hasth = !th.equals("");
				hastd = !td.equals("");

				if (hasth && hastd) {
					td = String.join("\n", row.select("td > *").stream().map(Element::text)
							.filter(StringUtils::isNotBlank).collect(Collectors.toList()));
					
					// Found detail
					underHeading.put(th, td);
				} else if (hasth && !hastd) {
					// Found heading
					if (i == 0) {
						info.put("name", th);
					} else {
						if (heading.equals("")) {
							info.putAll(underHeading);
						} else if (underHeading.size() != 0) {
							info.put(heading, underHeading);
						}
						heading = th;
						underHeading = new HashMap<>();
					}
				}
			} catch (Exception e) {
			}
			i++;
		}

		try {
			if (heading.equals("")) {
				info.putAll(underHeading);
			} else if (underHeading.size() != 0) {
				info.put(heading, underHeading);
			}
		} catch (Exception e) {
		}

		try {
			if (!info.containsKey("name") || info.get("name").equals("")) {
				info.put("name", doc.selectFirst(".infobox caption").text());
			}
		} catch (Exception e) {

		}

		try {
			info.put("image", "https:" + doc.selectFirst(".infobox tr:nth-child(2)").selectFirst("img").attr("src"));
		} catch (Exception e) {
		}
	}

	@Override
	public String getName() {
		return "Wikipedia Scraper";
	}

}
