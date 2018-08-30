package com.mdakram28.infodb.tools.nosqldb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.mdakram28.infodb.Info;
import com.mdakram28.infodb.InfoList;
import com.mdakram28.infodb.datamanager.IDataSink;
import com.mdakram28.infodb.datamanager.IDataSource;

@Component
@Order(value = 2)
public class NosqlDbStore implements IDataSource, IDataSink {

	@Autowired
	InfoDAO infoDAO;

	@Override
	public String getType() {
		return TYPE.DATABASE;
	}

	@Override
	public String getName() {
		return "MongoDB";
	}

	@Override
	public List<Info> fetchInfo(String key) throws Exception {
		return infoDAO.findDocuments(key);
	}

	@Override
	public void storeInfo(InfoList obj) throws Exception {
		for (Info info : obj.getData()) {
			infoDAO.insertDocument(info);
		}
	}
}
