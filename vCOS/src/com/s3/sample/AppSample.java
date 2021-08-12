package com.s3.sample;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.s3.core.S3BucketManagerment;
import com.s3.core.S3ObjectManagerment;

public class AppSample {

	public static void main(String[] args) {
		System.out.println("testing with bucket....");
		S3BucketManagerment s3BucketManagerment = new S3BucketManagerment();
		String bucketName = "thangdv123";

		System.out.println("Putting bucket " + bucketName);
		s3BucketManagerment.putBucket(bucketName);

		for (Bucket b : s3BucketManagerment.getListBucket()) {
			System.out.println(b.getName());
		}
		// System.out.println("Deleting bucket " + bucketName);
		// s3BucketManagerment.deleteBucket(bucketName);

		// addCorsRules
		s3BucketManagerment.addCorsRules(bucketName, "CorsRuleAdmin", Arrays.asList("http://www.example.com"),
				Arrays.asList("POST", "GET"), null, 0);
		s3BucketManagerment.addCorsRules(bucketName, "CorsRuleUser", Arrays.asList("http://www.example1234.com"),
				Arrays.asList("POST", "GET", "DELETE"), null, 0);
		// getBucketCoraOrigin
		s3BucketManagerment.getBucketCrossOrigin(bucketName);
		// setCorsRules
		s3BucketManagerment.setCorsRules(bucketName, "CorsOverride", Arrays.asList("http://www.example4321.com"),
				Arrays.asList("POST", "GET", "DELETE"), null, 0);
		// getBucketCoraOrigin
		s3BucketManagerment.getBucketCrossOrigin(bucketName);
		// deleteBucketCoraOrigin
		s3BucketManagerment.deleteCorsRules(bucketName);
		// getBucketCoraOrigin
		s3BucketManagerment.getBucketCrossOrigin(bucketName);

		// listBucketRecursions
		Set<String> listBucketRecursions = s3BucketManagerment.getListBucketRecursion(bucketName);
		for (String bName : listBucketRecursions) {
			System.out.println(bName);
		}

//		System.out.println("Testing with object....");
//		S3ObjectManagerment s3ObjectManagerment = new S3ObjectManagerment();
//		s3ObjectManagerment.deleteObject("thangdv123", "New Folder/folder2/TRUNG.jpg");
//		//s3ObjectManagerment.deleteAllObjectsFromBucket(bucketName);

	}
}
