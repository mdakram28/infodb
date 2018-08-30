package com.mdakram28.infodb.tools.nosqldb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import com.mdakram28.infodb.Info;

@Service
public class MongoInfoDAO implements InfoDAO {

	@Autowired
	MongoInfoRepository infoRepository;

	@Override
	public void insertDocument(Info info) {
		try{
			infoRepository.insert(info);
		}catch (Exception e) {
//			e.printStackTrace();
		}
	}

	@Override
	public void deleteDocument(Info info) {
		infoRepository.delete(info);
	}

	@Override
	public Info findDocument(String key) {
		Sort sort = Sort.by("score");
		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(key);
		return infoRepository.findFirstBy(criteria, sort);
	}
	
	@Override
	public List<Info> findDocuments(String key) {
		Sort sort = Sort.by("score");
		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(key);
		return infoRepository.findBy(criteria, sort);
	}

}
