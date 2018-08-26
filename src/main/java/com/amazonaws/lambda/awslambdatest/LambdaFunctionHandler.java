package com.amazonaws.lambda.awslambdatest;

import com.amazonaws.lambda.awslambdatest.dbconfig.DynamoDBConfig;
import com.amazonaws.lambda.awslambdatest.model.Learning;
import com.amazonaws.lambda.awslambdatest.repo.ExampleRepository;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<Learning, String> {

    @Override
    public String handleRequest(Learning input, Context context) {
        context.getLogger().log("Input: " + input);
        
        DynamoDBConfig config=new DynamoDBConfig();
        config.createTable();
        
        ExampleRepository exampleRepository=new ExampleRepository();
        
        exampleRepository.add((Learning) input);

        

        return "Hello from Lambda!";
    }

}
