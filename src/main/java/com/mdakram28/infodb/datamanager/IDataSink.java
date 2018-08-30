package com.mdakram28.infodb.datamanager;

import com.mdakram28.infodb.InfoList;

public interface IDataSink extends IDataEndpoint {
	public void storeInfo(InfoList obj) throws Exception;
}
