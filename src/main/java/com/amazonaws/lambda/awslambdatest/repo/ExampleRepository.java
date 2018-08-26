package com.amazonaws.lambda.awslambdatest.repo;

import java.util.logging.Logger;

import com.amazonaws.lambda.awslambdatest.dbconfig.DynamoDBConfig;
import com.amazonaws.lambda.awslambdatest.model.Learning;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class ExampleRepository {
	private final static Logger _log = Logger.getLogger(ExampleRepository.class.getName());
	private DynamoDBMapper mapper=null;
	public ExampleRepository() {
		mapper=new DynamoDBMapper(DynamoDBConfig.amazonDynamoDB());
	}
	
	public Learning add(Learning learning) {
		Learning learningRC=getRowCounter();
		Long id_part=0L;
		if(null!=learningRC) {
			id_part=learningRC.getRowCounter();
			_log.info(" id_part >>> "+id_part);
			id_part++;
			_log.info(" id_part NEW >>> "+id_part);
			learningRC.setRowCounter(id_part);
		}
		String currentId="EXM"+id_part;
		learning.setId(currentId);
		mapper.save(learning);
		updateRowCounter(learningRC);
		return learning;
	}
	
	private Learning getRowCounter() {
		return mapper.load(Learning.class, "ROWCOUNTERID");
	}
	
	private Learning updateRowCounter(Learning learning) {
		 mapper.save(learning);
		 return learning;
	}

}
