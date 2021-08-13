package com.s3.core;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Rule;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Transition;

import java.util.Date;
import java.util.List;

public class S3LifecycleConfiguration {

	private final S3Setting s3Setting;
	private final AmazonS3Client s3Client;

	public S3LifecycleConfiguration() {
		s3Setting = new S3Setting();
		s3Client = new AmazonS3Client(new BasicAWSCredentials(S3Setting.getAccessKey(), S3Setting.getSecretKey()));
		s3Client.setEndpoint(S3Setting.getEndPoint());
		S3ClientOptions options = new S3ClientOptions();
		options.setPathStyleAccess(true);
		s3Client.setS3ClientOptions(options);
	}

	public Rule makeLifecycleRule(String ruleName, String prefix, List<Transition> transition, Object expirationDate, String status) {
		// Create a rule to archive objects with the prefix to Glacier immediately.
		BucketLifecycleConfiguration.Rule rule = new BucketLifecycleConfiguration.Rule().withPrefix(prefix)
				.withId(ruleName).withStatus(status);

		if (!transition.isEmpty()) {
			for (Transition t : transition) {
				rule.withTransition(t);
			}
		}
		
		if (expirationDate.getClass() == Integer.class) {
			rule.setExpirationInDays((Integer) expirationDate);
		} else if (expirationDate.getClass() == Date.class) {
			rule.setExpirationDate((Date) expirationDate);
		}
		rule.setStatus(status);
		return rule;
	}

	public void deleteLifecyctleConfiguration(String bucketName) {
		try {
			BucketLifecycleConfiguration configuration = getLifecyctleConfiguration(bucketName);
			if(configuration == null) {
				System.out.println("Current Lifecycle Configuration is null.");
				return;
			}
			for(Rule rule : configuration.getRules()) {
				System.out.println("DELETE: " + rule.getId()+" "+rule.getPrefix()+" "+rule.getExpirationInDays());
			}
			s3Client.deleteBucketLifecycleConfiguration(bucketName);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * Remind that this method is add new with old configuration, please set name/folder is not same with old configuration.
	 * @param bucketName
	 * @param rules
	 */
	public void addLifecycleConfiguration(String bucketName, List<Rule> rules) {
		// Retrieve the configuration.
		try {
			BucketLifecycleConfiguration configuration = s3Client.getBucketLifecycleConfiguration(bucketName);
			configuration.getRules().addAll(rules);
			// Save the configuration.
			s3Client.setBucketLifecycleConfiguration(bucketName, configuration);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public BucketLifecycleConfiguration getLifecyctleConfiguration(String bucketName) {
		// Retrieve the configuration.
		try {
			return s3Client.getBucketLifecycleConfiguration(bucketName);
		} catch (Exception e) {
			System.err.println(e.toString());
			return null;
		}
	}
	
	/**
	 * Remide that: This method is override the Lifecycle configuration
	 * @param bucketName
	 * @param rules
	 */
	public void setLifecycleConfiguration(String bucketName, List<Rule> rules) {
		try {
			//delete old configuration
			deleteLifecyctleConfiguration(bucketName);
			// Add the rules to a new BucketLifecycleConfiguration.
			BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration().withRules(rules);
			// Save the configuration.
			s3Client.setBucketLifecycleConfiguration(bucketName, configuration);
		} catch (Exception e) {
			System.err.println(e.toString());
		}

	}
}