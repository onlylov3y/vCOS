package com.s3.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3BucketManagerment {

	@SuppressWarnings("unused")
	private final S3Setting s3Setting;
	private final AmazonS3Client s3Client;
	private final S3ObjectManagerment s3ObjectManagerment;

	public S3BucketManagerment() {
		s3Setting = new S3Setting();
		s3Client = new AmazonS3Client(new BasicAWSCredentials(S3Setting.getAccessKey(), S3Setting.getSecretKey()));
		s3Client.setEndpoint(S3Setting.getEndPoint());
		S3ClientOptions options = new S3ClientOptions();
		options.setPathStyleAccess(true);
		s3Client.setS3ClientOptions(options);

		s3ObjectManagerment = new S3ObjectManagerment();
	}

	public List<Bucket> getListBucket() {
		return s3Client.listBuckets();
	}

	public void getListBucketRecursion() {
		List<Bucket> buckets = getListBucket();
		for (Bucket b : buckets) {
			getListBucketRecursion(b.getName());
		}
	}

	public Set<String> getListBucketRecursion(String bucketName) {
		Set<String> listBucketRecursion = new HashSet<String>();
		ObjectListing objectListing = null;
		try {
			objectListing = s3Client.listObjects(bucketName);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
		Iterator<?> iterator = objectListing.getObjectSummaries().iterator();
		while (iterator.hasNext()) {
			S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
			listBucketRecursion.add(bucketName + "/" + summary.getKey());
		}
		return listBucketRecursion;
	}

	public Bucket getBucket(String bucketName) {
		List<Bucket> buckets = getListBucket();
		for (Bucket b : buckets) {
			if (b.getName().equals(bucketName))
				return b;
		}
		return null;
	}

	public Bucket putBucket(String bucketName) {
		Bucket b = null;
		if (s3Client.doesBucketExist(bucketName)) {
			System.out.format("Bucket %s already exists.\n", bucketName);
			b = getBucket(bucketName);
		} else {
			try {
				b = s3Client.createBucket(bucketName);
			} catch (AmazonS3Exception e) {
				System.err.println(e.getErrorMessage());
				return null;
			}
		}
		return b;
	}

	public void deleteBucket(String bucketName) {
		s3ObjectManagerment.deleteAllObjectsFromBucket(bucketName);
		s3ObjectManagerment.deleteAllVersionsFromBucket(bucketName);
		s3Client.deleteBucket(bucketName);
		System.out.println("Deleted " + bucketName);
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
		List<CORSRule> originRule = s3Client.getBucketCrossOriginConfiguration(bucketName).getRules();
		originRule.add(rule);
		configuration.setRules(originRule);
		// Add the configuration to the bucket.
		s3Client.setBucketCrossOriginConfiguration(bucketName, configuration);
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
		s3Client.setBucketCrossOriginConfiguration(bucketName, configuration);
	}
	
	/**
	 * Delete the origin rules
	 */
	public void deleteCorsRules(String bucketName) {
		s3Client.deleteBucketCrossOriginConfiguration(bucketName);
	}
	
	public void getBucketCrossOrigin(String bucketName) {
		BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();
		// Retrieve
		configuration = s3Client.getBucketCrossOriginConfiguration(bucketName);
		printCORSConfiguration(configuration);
	}
	
	private static void printCORSConfiguration(BucketCrossOriginConfiguration configuration) {
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
