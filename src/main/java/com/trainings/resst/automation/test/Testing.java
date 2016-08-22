package com.trainings.resst.automation.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;

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

	@Test
	public static void GetMessageTestCase()
	{
		try {
			given().
			contentType(ContentType.JSON)
			.pathParam("id", propData.getProperty("idGet"))
			.expect().body("size()", equalTo(1))
			.when()
			.get(url+"/"+"{id}")
			.then()			
			.body(matchesJsonSchema(new FileInputStream(".//data//schema-def.json")))
			.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK")))
			.body("content", equalTo(propData.getProperty("contentGet"))).log().all();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
//	@Test
//	public static void PostTestCase(){
//		String outputData;
//		String postData="{\"author\""+":"+"\""+propData.getProperty("newAuthor")+"\""+","+"\"content\""+":"+"\""+propData.getProperty("newContent")+"\""+"}";
//		outputData=given()
//		.contentType(ContentType.JSON)
//		.body(postData).
//		when()
//		.post(url).
//		then()
//		.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK")))
//		.extract().contentType();
//		System.out.println("Output Data:"+outputData);
//
//	}
//	@Test
//	public static void PutTestCase(){
//		String putData="{\"author\""+":"+"\""+propData.getProperty("updateAuthor")+"\""+","+"\"content\""+":"+"\""+propData.getProperty("updateContent")+"\""+"}";
//		given()
//		.contentType(ContentType.JSON)
//		.pathParam("id", propData.getProperty("idPut"))
//		.body(putData)	        
//		.when()
//		.put(url+"/"+"{id}")	        
//		.then()	        
//		.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK")));
//
//	}
//	@Test
//	public static void DeleteTestCase(){
//		given()
//		.contentType(ContentType.JSON)
//		.pathParam("id", propData.getProperty("idDelete"))
//		.when()
//		.delete((url+"/"+"{id}")).
//		then()
//		.assertThat().statusCode(Integer.parseInt(propData.getProperty("204_NoContent"))).log().ifError();
//
//	}


}
