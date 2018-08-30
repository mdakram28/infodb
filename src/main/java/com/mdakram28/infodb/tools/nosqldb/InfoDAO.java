package com.mdakram28.infodb.tools.nosqldb;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mdakram28.infodb.Info;

public interface InfoDAO {
	public void insertDocument(Info info);

	public void deleteDocument(Info info);

	public Info findDocument(String key);

	public List<Info> findDocuments(String key);
}
