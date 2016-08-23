package com.trainings.resst.automation.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;




public class Testing {
	public static String url = null;
	public static Properties propData = new Properties();
	public static InputStream inputData = null;
	public int a;

	@BeforeSuite
	public static void setConfig(){
		try {
			inputData = new FileInputStream(".//config//config.properties");//getClass().getClassLoader().getResourceAsStream("D://Trainings//RestAutomation//RestAssuredProject//src//main//java//com//tranings//rest//automation//data//data.properties");
			propData.load(inputData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		url = "http://"+propData.getProperty("ServerIP")+":"+propData.getProperty("port")+propData.getProperty("restAPI");
	}

	@BeforeClass
	public void getData(){
		try {
			inputData = new FileInputStream(".//data//data.properties");//getClass().getClassLoader().getResourceAsStream("D://Trainings//RestAutomation//RestAssuredProject//src//main//java//com//tranings//rest//automation//data//data.properties");
			propData.load(inputData);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

	/*******Test Case to verify GET and validating schema ,status code and data*********/
	@Test
	public static void GetMessageTestCase()
	{
		try {
			given().
			contentType(ContentType.JSON)
			.pathParam("id", propData.getProperty("idGet"))
			//.expect().body("size()", equalTo(3))
			.when()
			.get(url+"/"+"{id}")
			.then()			
			.body(matchesJsonSchema(new FileInputStream(".//data//schema-def.json")))
			.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK")))
			.body("content", equalTo(propData.getProperty("contentGet"))).log().all();
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/*******Test Case to verify POST and validating length of object at
	 server before and after POST*********/
	
	@Test
	public static void PostTestCase(){
		String postData="{\"author\""+":"+"\""+propData.getProperty("newAuthor")+"\""+","+"\"content\""+":"+"\""+propData.getProperty("newContent")+"\""+"}";
		int lengthOfJsonbeforePost=given().when().get(url).path("size()");
		System.out.println("Length of json before POST: "+lengthOfJsonbeforePost);
		
		 given()
		.contentType(ContentType.JSON)
		.body(postData)
		.when()
		.post(url)
		.then()
		.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK"))).log().all()
		.body("author",equalTo(propData.getProperty("newAuthor")))
		.extract().body().equals(postData);
		
		 int lengthOfJsonafterPost=given().when().get(url).path("size()");
		System.out.println("Length of json before POST: "+lengthOfJsonafterPost);
	}
	
	/*******Test Case to verify PUT and validating updating data*********/
	
	@Test
	public static void PutTestCase(){
		String putData="{\"author\""+":"+"\""+propData.getProperty("updateAuthor")+"\""+","+"\"content\""+":"+"\""+propData.getProperty("updateContent")+"\""+"}";
		given()
		.contentType(ContentType.JSON)
		.pathParam("id", propData.getProperty("idPut"))
		.body(putData)	        
		.when()
		.put(url+"/"+"{id}")	        
		.then()	        
		.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK"))).log().all()
		.extract().response().getBody().equals(putData);
	
	}
	
	/*******Test Case for delete functionality with lenght validation*********/
	
	@Test
	public static void DeleteTestCase(){
		int lengthOfJsonbeforeDelete=given().when().get(url).path("size()");
		System.out.println("Length of json before POST: "+lengthOfJsonbeforeDelete);
		given()
		.contentType(ContentType.JSON)
		.pathParam("id", propData.getProperty("idDelete"))
		.when()
		.delete((url+"/"+"{id}")).
		then()
		.assertThat().statusCode(Integer.parseInt(propData.getProperty("204_NoContent"))).log().ifError();

		int lengthOfJsonafterdelete=given().when().get(url).path("size()");
		System.out.println("Length of json before POST: "+lengthOfJsonafterdelete);
	}
	
	/*******Test Case to verify POST using data from HashMap*********/
	@Test
	public static void PostJsonAsMapTestCase(){
		Map<String, String>  jsonAsMap = new HashMap<String, String>();
		jsonAsMap.put("author", "admin");
		jsonAsMap.put("content", "Hello super admin");
		int lengthOfJsonbeforePost=given().when().get(url).path("size()");
		System.out.println("Length of json before POST: "+lengthOfJsonbeforePost);
		
		 given()
		.contentType(ContentType.JSON)
		.body(jsonAsMap)
		.when()
		.post(url)
		.then()
		.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK"))).log().all()
		.extract().body().equals(jsonAsMap);
		
		 int lengthOfJsonafterPost=given().when().get(url).path("size()");
		System.out.println("Length of json before POST: "+lengthOfJsonafterPost);
	}


}
