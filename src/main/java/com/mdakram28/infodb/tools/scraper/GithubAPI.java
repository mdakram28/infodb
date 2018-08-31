package com.mdakram28.infodb.tools.scraper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdakram28.infodb.Info;
import com.mdakram28.infodb.InfoList;
import com.mdakram28.infodb.datamanager.IDataSource;

@Component
@Order(value = 2)
public class GithubAPI implements IDataSource{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public String getType() {
		return TYPE.API;
	}

	@Override
	public List<Info> fetchInfo(String key) throws Exception {
		Info info = new Info(this);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Object> response = restTemplate.getForEntity("https://api.github.com/search/users?q="+key, Object.class);
		info.setSource("https://api.github.com/search/users?q="+key);
		
		ObjectMapper m = new ObjectMapper();
		Map<String,Object> props = m.convertValue(response.getBody(), Map.class);
		List<Object> props2 = m.convertValue(props.get("items"), List.class);
		
		if(props2.size() == 0) throw new ScrapingException("No search result");
		
		Map<String,String> props3 = m.convertValue(props2.get(0), Map.class);
		String url = props3.get("url");
		
		response = restTemplate.getForEntity(url, Object.class);
		info.setRaw(m.convertValue(response.getBody(), Map.class));
		List<String> titles = new ArrayList<>();
		Object o1 = m.convertValue(response.getBody(), Map.class).get("name");
		if(o1 != null) titles.add(o1.toString());
		Object o2 = m.convertValue(response.getBody(), Map.class).get("login");
		if(o2 != null) titles.add(o2.toString());
		
		info.setTitle(String.join(", ", titles));
		
		return Arrays.asList(new Info[]{info});
	}
	
	@Override
	public String getName() {
		return "Github API";
	}

}
