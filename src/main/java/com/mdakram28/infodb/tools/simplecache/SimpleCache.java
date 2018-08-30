package com.mdakram28.infodb.tools.simplecache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.mdakram28.infodb.Info;
import com.mdakram28.infodb.InfoList;
import com.mdakram28.infodb.datamanager.IDataSink;
import com.mdakram28.infodb.datamanager.IDataSource;

//@Component
//@Order(value=1)
public class SimpleCache implements IDataSource, IDataSink{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	Map<String, Info> cache = new HashMap<>();

	@Override
	public String getType() {
		return TYPE.CACHE;
	}

	@Override
	public List<Info> fetchInfo(String key) throws Exception {
		key = key.toLowerCase().replaceAll("\\s", "");
		List<Info> ret = new ArrayList<>();
		for(String title : cache.keySet()) {
			if(title.indexOf(key) >= 0) {
				ret.add(cache.get(title));
			}
		}
		return ret;
	}

	@Override
	public void storeInfo(InfoList obj) throws Exception {
		for(Info info : obj.getData()) {
			cache.put(info.title.toLowerCase().replaceAll("\\s", "") + " " + info.getToolName(), info);
		}
	}

	@Override
	public String getName() {
		return "Simple Cache";
	}
	
}
