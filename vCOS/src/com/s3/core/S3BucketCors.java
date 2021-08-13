package com.s3.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;

public class S3BucketCors {

	@SuppressWarnings("unused")
	private final S3Setting s3Setting;
	private final AmazonS3Client s3Client;

	public S3BucketCors() {
		s3Setting = new S3Setting();
		s3Client = new AmazonS3Client(new BasicAWSCredentials(S3Setting.getAccessKey(), S3Setting.getSecretKey()));
		s3Client.setEndpoint(S3Setting.getEndPoint());
		S3ClientOptions options = new S3ClientOptions();
		options.setPathStyleAccess(true);
		s3Client.setS3ClientOptions(options);
	}

	/**
	 * Add a new rule with origin rules
	 */
	public void addCorsRules(String bucketName, String ruleName, List<String> allowedOrigins, 
			List<String> allowedMethods, List<String> exposedHeaders, int maxAgeSeconds) {
		CORSRule rule = new CORSRule().withId(ruleName);
		if(allowedOrigins == null) {
			System.err.println("AllowedOrigins field must be not null.");
			return;
		}
		rule.withAllowedOrigins(allowedOrigins);
		// Create CORS rules.
		List<CORSRule.AllowedMethods> methodRule = new ArrayList<CORSRule.AllowedMethods>();
		if (allowedMethods != null) {
			for (String method : allowedMethods) {
				try {
					methodRule.add(CORSRule.AllowedMethods.valueOf(method.toUpperCase()));
				} catch (Exception e) {
					continue;
				}
			}
			rule.withAllowedMethods(methodRule);
		}
		if(exposedHeaders != null)
			rule.withExposedHeaders(exposedHeaders);
		if(maxAgeSeconds != 0)
			rule.withMaxAgeSeconds(maxAgeSeconds);
		//Set rule
		BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();
		List<CORSRule> originRule = new ArrayList<CORSRule>();
		try {
			originRule = s3Client.getBucketCrossOriginConfiguration(bucketName).getRules();
			originRule.add(rule);
			configuration.setRules(originRule);
			// Add the configuration to the bucket.
			s3Client.setBucketCrossOriginConfiguration(bucketName, configuration);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Override the origin rules
	 */
	public void setCorsRules(String bucketName, String ruleName, List<String> allowedOrigins, 
			List<String> allowedMethods, List<String> exposedHeaders, int maxAgeSeconds) {
		CORSRule rule = new CORSRule().withId(ruleName);
		if(allowedOrigins == null) {
			System.err.println("AllowedOrigins field must be not null.");
			return;
		}
		rule.withAllowedOrigins(allowedOrigins);
		// Create CORS rules.
		List<CORSRule.AllowedMethods> methodRule = new ArrayList<CORSRule.AllowedMethods>();
		if (allowedMethods != null) {
			for (String method : allowedMethods) {
				try {
					methodRule.add(CORSRule.AllowedMethods.valueOf(method.toUpperCase()));
				} catch (Exception e) {
					continue;
				}
			}
			rule.withAllowedMethods(methodRule);
		}
		if(exposedHeaders != null)
			rule.withExposedHeaders(exposedHeaders);
		if(maxAgeSeconds != 0)
			rule.withMaxAgeSeconds(maxAgeSeconds);
		//Set rule
		BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();
		configuration.setRules(Arrays.asList(rule));
		// Add the configuration to the bucket.
		try {
			s3Client.setBucketCrossOriginConfiguration(bucketName, configuration);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Delete the origin rules
	 */
	public void deleteCorsRules(String bucketName) {
		try {
			s3Client.deleteBucketCrossOriginConfiguration(bucketName);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public BucketCrossOriginConfiguration getBucketCrossOrigin(String bucketName) {
		BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();
		// Retrieve
		try {
			configuration = s3Client.getBucketCrossOriginConfiguration(bucketName);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
		// print to test
		// printCORSConfiguration(configuration);

		return configuration;
	}
	
	public void printCORSConfiguration(BucketCrossOriginConfiguration configuration) {
        if (configuration == null) {
            System.out.println("Configuration is null.");
        } else {
            System.out.println("Configuration has " + configuration.getRules().size() + " rules\n");

            for (CORSRule rule : configuration.getRules()) {
                System.out.println("Rule ID: " + rule.getId());
                System.out.println("MaxAgeSeconds: " + rule.getMaxAgeSeconds());
                System.out.println("AllowedMethod: " + rule.getAllowedMethods());
                System.out.println("AllowedOrigins: " + rule.getAllowedOrigins());
                System.out.println("AllowedHeaders: " + rule.getAllowedHeaders());
                System.out.println("ExposeHeader: " + rule.getExposedHeaders());
            }
        }
    }
}
