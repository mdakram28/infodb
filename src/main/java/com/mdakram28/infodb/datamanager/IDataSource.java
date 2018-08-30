package com.mdakram28.infodb.datamanager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;

import com.mdakram28.infodb.Info;
import com.mdakram28.infodb.InfoList;

public interface IDataSource extends IDataEndpoint {
	
	public List<Info> fetchInfo(String key) throws Exception;

	String getName();
}
