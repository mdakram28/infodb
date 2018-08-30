package com.mdakram28.infodb.tools.nosqldb;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mdakram28.infodb.Info;

public interface MongoInfoRepository extends MongoRepository<Info, Long> {

//	@Query(
//		value = "{ $text: { $search: '?0' } }",
//		fields = "{ score: { $meta: 'textScore'} }"
//	)
	Info findFirstBy(TextCriteria criteria, Sort sort);
	
	List<Info> findBy(TextCriteria criteria, Sort sort);

}
