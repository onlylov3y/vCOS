package com.s3.core;

public class S3Setting {

	/*
	 * This class to read all settings (authen info, end point,...
	 * This info from file, user input,...
	 */
	
	private static String accessKey = "";
	private static String secretKey = "";
	private static String endPoint = "http://s1.cloudstorage.com.vn:9020";

	public static String getAccessKey() {
		return accessKey;
	}

	public static void setAccessKey(String accessKey) {
		S3Setting.accessKey = accessKey;
	}

	public static String getSecretKey() {
		return secretKey;
	}

	public static void setSecretKey(String secretKey) {
		S3Setting.secretKey = secretKey;
	}

	public static String getEndPoint() {
		return endPoint;
	}

	public static void setEndPoint(String endPoint) {
		S3Setting.endPoint = endPoint;
	}

}
