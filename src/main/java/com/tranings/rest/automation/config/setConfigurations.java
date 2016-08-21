package com.tranings.rest.automation.config;

public class setConfigurations {
	private static String ServerIP = "192.168.1.6";
	private static String port = ":8080";
	private static String restAPI = "/messenger/webapi/messages";
	private static String url = "http://"+ServerIP+port+restAPI;
		
	public static String getURL(){
	return url;
}
	
	
}
