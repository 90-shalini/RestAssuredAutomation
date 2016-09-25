package com.trainings.resst.automation.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.jayway.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import com.jayway.restassured.module.jsv.JsonSchemaValidator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;


public class Testing{
	public static String url = null;
	public static String username = null,password=null;
	public static Properties propData = new Properties();
	public static InputStream inputData = null;
	public static Properties propConfig = new Properties();
	public static InputStream inputConfig = null;
	public int a;
	
	public static final Logger log = Logger.getLogger(Testing.class);
	public static String xsd = null;
	@BeforeSuite
	public static void setConfig(){
		try {
			xsd = new FileInputStream(".//data//schema-def.xsd").toString();
			inputConfig = new FileInputStream(".//config//config.properties");
			propConfig.load(inputConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
		url = "http://"+propConfig.getProperty("ServerIP")+":"+propConfig.getProperty("port")+propConfig.getProperty("restAPI");
		username=propConfig.getProperty("username");
		password=propConfig.getProperty("password");
	}

	@BeforeTest
	public void getData(){
		try {
			inputData = new FileInputStream(".//data//data.properties");
			propData.load(inputData);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

	/*******Test Case to verify GET and validating schema ,status code and data with XML data*********/
	@Test
	public static void GetMessageTestCaseUsingXML()
	{
		
		log.info("GetMessageTestCase started:");
		try {
			given()
			.contentType(ContentType.JSON)
			.pathParam("id", propData.getProperty("idGet"))
			//.expect().body("size()", equalTo(3))				//check size of msg
			.when()
			.get(url+"/"+"{id}")
			.then()			
			.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK"))).log().all()
			.body("message.content", equalTo(propData.getProperty("contentGet")))
			.body(hasXPath("/message/author[text()='shalini']"));
			//.body(matchesXsd(xsd));
			//.body(matchesXsdInClasspath(".//data//schema-def.xsd")).log().all();
		
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Schema Exception "+e.getMessage());
		}
		log.info("GetMessageTestCase Completed.");
		
	}
	
	
	
	
	
	
/*******Test Case to verify GET and validating schema ,status code and data*********/
	@Test
	public static void GetMessageTestCase()
	{
		log.info("GetMessageTestCase started:");
		try {
			given()
			.auth().preemptive().basic(username, password)     //Handling authorization
			.contentType(ContentType.JSON)
			.pathParam("id", propData.getProperty("idGet"))
			.expect().body("size()", equalTo(3))
			.when()
			.get(url+"/"+"{id}")
			.then()			
			.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK")))
			.body("content", equalTo(propData.getProperty("contentGet")))
			.body(matchesJsonSchema(new FileInputStream(".//data//schema-def.json")).using(settings().with().checkedValidation(false)));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Schema Exception "+e.getMessage());
		}
		log.info("GetMessageTestCase Completed.");
		
	}
	
	
	
	
/*******Test Case to verify POST and validating length of object at server before and after POST*********/
	
	@Test
	public static void PostTestCase(){
		log.info("PostTestCase started:");
		String postData="{\"author\""+":"+"\""+propData.getProperty("newAuthor")+"\""+","+"\"content\""+":"+"\""+propData.getProperty("newContent")+"\""+"}";
		int lengthOfJsonbeforePost=getCountOfMessages();
		log.info("Length of json before POST: "+lengthOfJsonbeforePost);
		 
		given()
		.auth().preemptive().basic(username, password)    		 //Handling authorization
		.contentType(ContentType.JSON)
		.body(postData)
		.when()
		.post(url)
		.then()
		.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK")))
		.body("author",equalTo(propData.getProperty("newAuthor")))
		.extract().body().equals(postData);
		
		 int lengthOfJsonafterPost=getCountOfMessages();
		log.info("Length of json before POST: "+lengthOfJsonafterPost);
		Assert.assertEquals(lengthOfJsonbeforePost+1, lengthOfJsonafterPost);
		log.info("PostTestCase completed:");
	}
	
	
	
	
	
/*******Test Case to verify PUT and validating updating data*********/
	
	@Test
	public static void PutTestCase(){
		log.info("PutTestCase started:");
		String putData="{\"author\""+":"+"\""+propData.getProperty("updateAuthor")+"\""+","+"\"content\""+":"+"\""+propData.getProperty("updateContent")+"\""+"}";
		given()
		.auth().preemptive().basic(username, password)					 //Handling authorization
		.contentType(ContentType.JSON)
		.pathParam("id", propData.getProperty("idPut"))
		.body(putData)	        
		.when()
		.put(url+"/"+"{id}")	        
		.then()	        
		.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK")))
		.extract().response().getBody().equals(putData);
		log.info("PutTestCase completed.");
	}
	
	
	
	
	
	
/*******Test Case for delete functionality with length validation*********/
		@Test(dependsOnMethods = { "PostTestCase" })
	public static void DeleteTestCase(){
		log.info("DeleteTestCase started:");
		int lengthOfJsonbeforeDelete=getCountOfMessages();
		log.info("Length of json before Delete: "+lengthOfJsonbeforeDelete);
		given()
		.auth().preemptive().basic(username, password)					 //Handling authorization(preemptive), when we expect login from application(not challenged)
		.contentType(ContentType.JSON)
		.pathParam("id", propData.getProperty("idDelete"))
		.when()
		.delete((url+"/"+"{id}")).
		then()
		.assertThat().statusCode(Integer.parseInt(propData.getProperty("204_NOCONTENT")));

		int lengthOfJsonafterdelete=getCountOfMessages();
		log.info("Length of json after Delete: "+lengthOfJsonafterdelete);
		Assert.assertEquals(lengthOfJsonbeforeDelete-1, lengthOfJsonafterdelete);
		log.info("DeleteTestCase completed.");		
		}
	
	
	
	
	
/*******Test Case to verify POST using data from HashMap*********/
	//@SuppressWarnings("deprecation")
	@Test
	public static void PostJsonAsMapTestCase(){
		log.info("PostJsonAsMapTestCase started:");
		Map<String, String>  jsonAsMap = new HashMap<String, String>();
		jsonAsMap.put("author", "newJSONMapAdmin");
		jsonAsMap.put("content", "Hello JsonMap admin");
		
		int lengthOfJsonbeforePost =getCountOfMessages();
		log.info("Length of json before JSONMAP POST: "+lengthOfJsonbeforePost);
		
		 given()
		.auth().preemptive().basic(username, password)
		.contentType(ContentType.JSON)
		.body(jsonAsMap)
		.when()
		.post(url)
		.then()
		.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK")))
		.extract().body().equals(jsonAsMap);
		
		 int lengthOfJsonafterPost=getCountOfMessages();
		log.info("Length of json after JSONMAP POST: "+lengthOfJsonafterPost);
		Assert.assertEquals(lengthOfJsonbeforePost+1, lengthOfJsonafterPost);
		log.info("PostJsonAsMapTestCase completed.");
		
	}

	
	
	
	
/*******Test Case to verify GET AllMessages using basic authentication*********/
	@Test
	public static void GetAllMessagesTestCase()
	{
		log.info("GetAllMessagesTestCase started:");
		try {
			given()
			.auth().preemptive().basic(username, password).
			contentType(ContentType.JSON)
			.when()
			.get(url)
			.then()			
			.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK")))
			.extract().response().getBody();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error "+e.getMessage());
		}
		log.info("GetAllMessagesTestCase completed.");		
	}
	
	
	

/*******Test Case to verify GET AllMessages using basic authentication*********/
	public static int getCountOfMessages(){
		int lengthOfJson=given()
				.auth().preemptive().basic(username, password)
				.when().get(url).path("size()");
		return lengthOfJson;
	}

	
	
	
	
/*******Test Case to verify GET AllMessages using wrong credentials*********/
	@Test
	public static void WrongCredentialValidationTestCase(){
		log.info("WrongCredentialValidationTestCase started:");	
		try {
			given()
			.auth().preemptive().basic(username+"123", password)
			.contentType(ContentType.JSON)
			.when()
			.get(url)
			.then()			
			.assertThat().statusCode(Integer.parseInt(propData.getProperty("401_UNAUTHORIZED")))
			.extract().response().getBody().print();
			} catch (Exception e) {
			e.printStackTrace();
			
		}
		log.info("WrongCredentialValidationTestCase completed.");	
	}
	
	

}
