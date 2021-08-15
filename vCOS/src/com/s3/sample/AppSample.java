package com.s3.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Rule;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Transition;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.StorageClass;
import com.s3.core.S3Acl;
import com.s3.core.S3BucketCors;
import com.s3.core.S3BucketManagerment;
import com.s3.core.S3BucketPolicy;
import com.s3.core.S3BucketVersioning;
import com.s3.core.S3LifecycleConfiguration;
import com.s3.core.S3MultipartManager;
import com.s3.core.S3ObjectManagerment;

public class AppSample {

	public static void main(String[] args) {
		System.out.println("testing with bucket....");
		S3BucketManagerment s3BucketManagerment = new S3BucketManagerment();
		String bucketName = "thangdv123";

		System.out.println("Putting bucket " + bucketName);
		s3BucketManagerment.putBucket(bucketName);
//		//list Multipart Uploads
//		s3BucketManagerment.listMultipartUploads(bucketName);
//
//		//List Bucket
//		for (Bucket b : s3BucketManagerment.getListBucket()) {
//			System.out.println(b.getName());
//		}
//		System.out.println("Deleting bucket " + bucketName);
//		s3BucketManagerment.deleteBucket(bucketName);
		
		
//		//Test Cors
//		S3BucketCors s3BucketCors = new S3BucketCors();
//		// addCorsRules
//		s3BucketCors.addCorsRules(bucketName, "CorsRuleAdmin", Arrays.asList("http://www.example.com"),
//				Arrays.asList("POST", "GET"), null, 0);
//		s3BucketCors.addCorsRules(bucketName, "CorsRuleUser", Arrays.asList("http://www.example1234.com"),
//				Arrays.asList("POST", "GET", "DELETE"), null, 0);
//		// getBucketCoraOrigin
//		s3BucketCors.getBucketCrossOrigin(bucketName);
//		// setCorsRules
//		s3BucketCors.setCorsRules(bucketName, "CorsOverride", Arrays.asList("http://www.example4321.com"),
//				Arrays.asList("POST", "GET", "DELETE"), null, 0);
//		// getBucketCoraOrigin
//		s3BucketCors.getBucketCrossOrigin(bucketName);
//		// deleteBucketCoraOrigin
//		s3BucketCors.deleteCorsRules(bucketName);
//		// getBucketCoraOrigin
//		s3BucketCors.printCORSConfiguration(s3BucketCors.getBucketCrossOrigin(bucketName));

		
//		//Test Acl
//		S3Acl s3BucketAcl = new S3Acl();
//		//set permisson
//		s3BucketAcl.setBucketAcl(bucketName, "15,0,1");
//		//set permission for an account by user. Error due to only supporting for pro.
//		//s3BucketAcl.setBucketAcl(bucketName, "thangdc@viettelidc.com.vn", 1);
//		//show permission
//		s3BucketAcl.showBucketAcl(s3BucketAcl.getBucketAcl(bucketName));
	
		
//		//Test Lifecycle
//		S3LifecycleConfiguration s3Lifecycle = new S3LifecycleConfiguration();
//		List<Rule> rules = new ArrayList<Rule>();
//		
//		List<Transition> transition = new ArrayList<Transition>();
//		transition.add(new Transition().withDays(30).withStorageClass(StorageClass.Standard));
//		transition.add(new Transition().withDays(365).withStorageClass(StorageClass.Glacier));
//		rules.add(s3Lifecycle.makeLifecycleRule("RuleAdmin2", "New Folder2/", transition, 6, BucketLifecycleConfiguration.ENABLED));
//		
//		List<Transition> transition2 = new ArrayList<Transition>();
//		transition2.add(new Transition().withDays(5).withStorageClass(StorageClass.ReducedRedundancy));
//		rules.add(s3Lifecycle.makeLifecycleRule("RuleLog", "New Folder/", transition2, 360, BucketLifecycleConfiguration.DISABLED));
//		for(Rule rule : rules) {
//			System.out.println(rule.getId()+" "+rule.getPrefix()+" "+rule.getExpirationInDays());
//			if(rule.getTransition() != null) {
//				System.out.println(rule.getTransition().getStorageClass().toString());
//			}
//		}
//		//Please know add and set clealy. add to add new, set is ovveride
//		//s3Lifecycle.setLifecycleConfiguration(bucketName, rules);
//		//s3Lifecycle.addLifecycleConfiguration(bucketName, rules);
//		
//		//get Lifecycle
//		BucketLifecycleConfiguration config = s3Lifecycle.getLifecyctleConfiguration(bucketName);
//		if(config != null)
//		for(Rule rule : config.getRules()) {
//			System.out.println("GET: " + rule.getId()+" "+rule.getPrefix()+" "+rule.getExpirationInDays());
//			if(rule.getTransition() != null) {
//				System.out.println(rule.getTransition().getStorageClass().toString());
//			}
//		}
//		// delete lifecycleConfiguration
//		s3Lifecycle.deleteLifecyctleConfiguration(bucketName);
		
//		//Test bucket policy
//		String filePath = "E:\\vCOS\\vCOS\\data\\bucketpolicy.txt";
//		S3BucketPolicy s3BucketPolicy = new S3BucketPolicy(filePath);
//		//set policy
//		s3BucketPolicy.setBucketPolicy(bucketName);
//		//delete policy
//		s3BucketPolicy.deleteBucketPolicy(bucketName);
		
//		//test BucketVersioning
//		S3BucketVersioning s3BucketVersioning = new S3BucketVersioning();
//		s3BucketVersioning.getBucketVersioning(bucketName);
//		//String prefixPath = "New Folder/DPI team.jpg";
//		//s3BucketVersioning.getBucketVersioning(bucketName, prefixPath);
		
		
		System.out.println("Testing with object....");
		S3ObjectManagerment s3ObjectManagerment = new S3ObjectManagerment();
		String key = "New Folder2/TRUNG.jpg";
		// listObjectRecursions
		Set<String> listObjectRecursions = s3ObjectManagerment.getListObjectRecursion(bucketName);
		// listObjectRecursions from key
 		//Set<String> listObjectRecursions = s3ObjectManagerment.getListObjectRecursion(bucketName, "New Folder2/");
		for (String oName : listObjectRecursions) {
			System.out.println(oName);
		}
		
//		//test get object
//		String outputPath = "E:\\vCOS\\vCOS\\data\\TRUNG.jpg";
//		s3ObjectManagerment.getObject(bucketName, key, outputPath);
		
//		//test put object
//		String inputPath = "E:\\vCOS\\vCOS\\data\\avatar.jpg";
//		key = "New Folder2/avatar.jpg";
//		s3ObjectManagerment.putObject(bucketName, key, inputPath);

//		//Delete an object
//		s3ObjectManagerment.deleteObject("thangdv123", "New Folder/folder2/TRUNG.jpg");
		
//		//Delete all objects
//		//s3ObjectManagerment.deleteAllObjectsFromBucket(bucketName);
		
//		//Delete objects by  versions
//		List<KeyVersion> objectKeys = new ArrayList<KeyVersion>();  
//		objectKeys.add(new KeyVersion(key));
//		objectKeys.add(new KeyVersion("New Folder2/avatar.jpg"));
//		s3ObjectManagerment.deleteObjects(bucketName, objectKeys);
		
//		//copy object
//		String key1 = "New Folder2/TRUNG.jpg";
//		String newKey = "folder3/TRUNG1234.jpg";
//		s3ObjectManagerment.copyObjectSameBucket(key, bucketName, newKey);
//		s3ObjectManagerment.copyObject(key1, "thangdv123", "thangdv", newKey);	
			
//		//Test object Acl
//		S3Acl s3ObjectAcl = new S3Acl();
//		//set permisson
//		s3ObjectAcl.setObjectAcl(bucketName, key, "15,15,15");
//		//set permission for an account by user. Error due to only supporting for pro.
//		//s3BucketAcl.setBucketAcl(bucketName, "thangdc@viettelidc.com.vn", 1);
//		//show permission 
//		s3ObjectAcl.showObjectAcl(s3ObjectAcl.getObjectAcl(bucketName, key));
		
		//Test multipart jobs
		S3MultipartManager s3Multipart = new S3MultipartManager();
		List<MultipartUpload> uploads = s3Multipart.listMultipartUploads(bucketName);
		System.out.println(uploads.size() + " multipart upload(s) in progress.");
        for (MultipartUpload u : uploads) {
            System.out.println("Upload in progress: Key = \"" + u.getKey() + "\", id = " + u.getUploadId());
            if(u.getKey().contains(".exe"))
            	s3Multipart.abortMultipart(bucketName, u.getKey(), u.getUploadId());
        }
        //test upload multipart
        String keyName = "New Folder2/hyaha.jpg";
        String filePath = "E:\\vCOS\\vCOS\\data\\avatar.jpg";
        s3Multipart.multipartUpload(bucketName, keyName, filePath);
        
	}
}
