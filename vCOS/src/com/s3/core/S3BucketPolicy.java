package com.s3.core;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;

public class S3BucketPolicy {

	private final S3Setting s3Setting;
	private final AmazonS3Client s3Client;
	private final FileReader fileReader;
	private final String filePath;

	public S3BucketPolicy(String filePath) {
		s3Setting = new S3Setting();
		s3Client = new AmazonS3Client(new BasicAWSCredentials(S3Setting.getAccessKey(), S3Setting.getSecretKey()));
		s3Client.setEndpoint(S3Setting.getEndPoint());
		S3ClientOptions options = new S3ClientOptions();
		options.setPathStyleAccess(true);
		s3Client.setS3ClientOptions(options);
		fileReader = new FileReader();
		this.filePath = filePath;
	}

	public String getFileReadPolicy() {
		String policyContend = fileReader.readFileAsString(filePath);
		Policy bucketPolicy = null;
		try {
			bucketPolicy = Policy.fromJson(policyContend);
		} catch (IllegalArgumentException e) {
			System.out.format("Invalid policy text in file: \"%s\"", bucketPolicy);
			System.out.println(e.getMessage());
		}
		return bucketPolicy.toJson();
	}

	public String getPublicReadPolicy(String bucketName) {
		Policy bucket_policy = new Policy().withStatements(new Statement(Statement.Effect.Allow)
				.withPrincipals(Principal.AllUsers).withActions(S3Actions.GetObject)
				.withResources(new Resource("arn:aws:s3:::" + bucketName + "/*")));
		return bucket_policy.toJson();
	}

	public void setBucketPolicy(String bucketName) {
		try {
			String policyText = getFileReadPolicy();
			if (policyText == null || policyText.isEmpty())
				policyText = getPublicReadPolicy(bucketName);

			s3Client.setBucketPolicy(bucketName, policyText);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void deleteBucketPolicy(String bucketName) {
		try {
			s3Client.deleteBucketPolicy(bucketName);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
