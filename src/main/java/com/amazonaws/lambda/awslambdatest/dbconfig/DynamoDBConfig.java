package com.amazonaws.lambda.awslambdatest.dbconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class DynamoDBConfig {
	private final static Logger _log = Logger.getLogger(DynamoDBConfig.class.getName());

	public static AmazonDynamoDB amazonDynamoDB() {

		_log.info(" dynamodb amazonDynamoDB start ...");
		AmazonDynamoDB dynamodb = null;
		try {

			dynamodb = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.fromName(System.getenv("AWS_DEFAULT_REGION")))
					.withCredentials(new AWSStaticCredentialsProvider(amazonAWSCredentials())).build();
			_log.info(" dynamodb connected...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dynamodb;
	}
	
	public static AmazonS3 amazonS3() {

		_log.info(" S3 start ...");
		AmazonS3 amazonS3 = null;
		try {

			amazonS3 = AmazonS3ClientBuilder.standard()
					.withRegion(Regions.fromName(System.getenv("AWS_DEFAULT_REGION")))
					.withCredentials(new AWSStaticCredentialsProvider(amazonAWSCredentials())).build();
			_log.info(" S3 connected...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amazonS3;
	}

	public static AWSCredentials amazonAWSCredentials() {
		EnvironmentVariableCredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();
		return credentialsProvider.getCredentials();
		// return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
	}
	
	public void createTable() {
		 	DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB());

	        String tableName = "ExampleTable";

	        try {
	        	List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
	            attributeDefinitions.add(new AttributeDefinition().withAttributeName("ID").withAttributeType("S"));

	            List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
	            keySchema.add(new KeySchemaElement().withAttributeName("ID").withKeyType(KeyType.HASH)); // Partition
	                                                                                                     // key

	            CreateTableRequest request = new CreateTableRequest().withTableName(tableName).withKeySchema(keySchema)
	                .withAttributeDefinitions(attributeDefinitions).withProvisionedThroughput(
	                    new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(6L));

	            System.out.println("Issuing CreateTable request for " + tableName);
	            //Table table = dynamoDB.createTable(request);
	            TableUtils.createTableIfNotExists(amazonDynamoDB(), request);
	            System.out.println("Waiting for " + tableName + " to be created...this may take a while...");
	            //table.waitForActive();
	            TableUtils.waitUntilActive(amazonDynamoDB(), tableName);
	            getTableInformation(dynamoDB,tableName);

	        }
	        catch (Exception e) {
	        	_log.info("Unable to create table: ");
	        	_log.info(e.getMessage());
	        	e.printStackTrace();
	        }
	}
	private static void getTableInformation(DynamoDB dynamoDB, String tableName) {

		_log.info("Describing " + tableName);

        TableDescription tableDescription = dynamoDB.getTable(tableName).describe();
        _log.info(" ############## "+tableDescription.getTableName()+" --- "+tableDescription.getTableStatus());
        System.out.format(
            "Name: %s:\n" + "Status: %s \n" + "Provisioned Throughput (read capacity units/sec): %d \n"
                + "Provisioned Throughput (write capacity units/sec): %d \n",
            tableDescription.getTableName(), tableDescription.getTableStatus(),
            tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
            tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
    }

}