package com.trainings.resst.automation.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.FileInputStream;
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

	@BeforeSuite
	public static void setConfig(){
		try {
			inputData = new FileInputStream("D://Trainings//RestAutomation//RestAssuredProject//src//main//java//com//tranings//rest//automation//config//config.properties");//getClass().getClassLoader().getResourceAsStream("D://Trainings//RestAutomation//RestAssuredProject//src//main//java//com//tranings//rest//automation//data//data.properties");
			propData.load(inputData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		url = "http://"+propData.getProperty("ServerIP")+":"+propData.getProperty("port")+propData.getProperty("restAPI");
	}

	@BeforeClass
	public void getData(){
		try {
			inputData = new FileInputStream("D://Trainings//RestAutomation//RestAssuredProject//src//main//java//com//tranings//rest//automation//data//data.properties");//getClass().getClassLoader().getResourceAsStream("D://Trainings//RestAutomation//RestAssuredProject//src//main//java//com//tranings//rest//automation//data//data.properties");
			propData.load(inputData);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

	@Test
	public static void GetMessageTestCase()
	{
		given().
		contentType(ContentType.JSON).
		pathParam("id", propData.getProperty("idGet")).
		when().
		get(url+"/"+"{id}").
		then().
		assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK"))).
		body("author", equalTo(propData.getProperty("author")));
	}
	@Test
	public static void PostTestCase(){
		String postData="{\"author\""+":"+"\""+propData.getProperty("newAuthor")+"\""+","+"\"content\""+":"+"\""+propData.getProperty("newContent")+"\""+"}";
		given()
		.contentType(ContentType.JSON)
		.body(postData).
		when()
		.post(url).
		then()
		.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK")));

	}
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
		.assertThat().statusCode(Integer.parseInt(propData.getProperty("200_OK")));

	}
	@Test
	public static void DeleteTestCase(){
		given()
		.contentType(ContentType.JSON)
		.pathParam("id", propData.getProperty("idDelete"))
		.when()
		.delete((url+"/"+"{id}")).
		then()
		.assertThat().statusCode(Integer.parseInt(propData.getProperty("204_NoContent"))).log().ifError();

	}


}
