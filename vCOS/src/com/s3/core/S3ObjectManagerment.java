package com.s3.core;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.autoscaling.model.Tag;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;

public class S3ObjectManagerment {

	@SuppressWarnings("unused")
	private final S3Setting s3Setting;
	private final AmazonS3Client s3Client;

	public S3ObjectManagerment() {
		s3Setting = new S3Setting();
		// Create S3 Client object using AWS KEY & SECRET
		s3Client = new AmazonS3Client(new BasicAWSCredentials(S3Setting.getAccessKey(), S3Setting.getSecretKey()));
		s3Client.setEndpoint(S3Setting.getEndPoint());
		S3ClientOptions options = new S3ClientOptions();
		options.setPathStyleAccess(true);
		s3Client.setS3ClientOptions(options);
	}

	public void getObject(String bucketName, String key, String outputPath) {
		try {
			S3Object s3object = s3Client.getObject(bucketName, key);
			S3ObjectInputStream inputStream = s3object.getObjectContent();
			FileUtils.copyInputStreamToFile(inputStream, new File(outputPath));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		System.out.println("Get object done.");
	}
	
	public void putObject(String bucketName, String key, String inputPath) {
		try {
			s3Client.putObject(bucketName, key, new File(inputPath));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		System.out.println("Put object done.");
	}
	
	public void copyObjectSameBucket(String key, String bucketNme, String newKey) {
		copyObject(key, bucketNme, bucketNme, newKey);
	}
	
	public void copyObjectSameName(String key, String sourceBucket, String tagetBucket) {
		copyObject(key, sourceBucket, tagetBucket, key);
	}
	
	public void copyObject(String key, String sourceBucket, String tagetBucket, String newKey) {
		System.out.format("Copying object %s from bucket %s to %s\n", key, sourceBucket, tagetBucket);
        try {
            s3Client.copyObject(sourceBucket, key, tagetBucket, newKey);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Done!");
	}
	
	public void deleteObject(String bucketName, String objectKey) {
		try {
			s3Client.deleteObject(bucketName, objectKey);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void deleteObjects(String bucketName, List<KeyVersion> objectKeys) {
		try {
			DeleteObjectsRequest dor = new DeleteObjectsRequest(bucketName).withKeys(objectKeys);
			s3Client.deleteObjects(dor);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Delete done!");
	}
	
	public void deleteAllObjectsFromBucket(String bucketName) {
		System.out.println("Removing all objects from bucket " + bucketName);
		ObjectListing objectListing = null;
		try {
			objectListing = s3Client.listObjects(bucketName);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}
		while (true) {
			for (Iterator<?> iterator = objectListing.getObjectSummaries().iterator(); iterator.hasNext();) {
				S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
				System.out.println("Deleting " + summary.getKey());
				s3Client.deleteObject(bucketName, summary.getKey());
			}
			// more object_listing to retrieve?
			if (objectListing.isTruncated()) {
				objectListing = s3Client.listNextBatchOfObjects(objectListing);
			} else {
				break;
			}
		}
	}

	public void deleteAllVersionsFromBucket(String bucketName) {
		System.out.println("Removing all versions from bucket " + bucketName);
		VersionListing version_listing = null;
		try {
			version_listing = s3Client.listVersions(new ListVersionsRequest().withBucketName(bucketName));
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}
		while (true) {
			for (Iterator<?> iterator = version_listing.getVersionSummaries().iterator(); iterator.hasNext();) {
				S3VersionSummary vs = (S3VersionSummary) iterator.next();
				System.out.println("Deleting " + vs.getKey() + " " + vs.getVersionId());
				s3Client.deleteVersion(bucketName, vs.getKey(), vs.getVersionId());
			}

			if (version_listing.isTruncated()) {
				version_listing = s3Client.listNextBatchOfVersions(version_listing);
			} else {
				break;
			}
		}
	}
	
	public Set<String> getListObjectRecursion(String bucketName) {
		Set<String> listObjectRecursion = new HashSet<String>();
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
			listObjectRecursion.add(bucketName + "/" + summary.getKey());
		}
		return listObjectRecursion;
	}
	
	public Set<String> getListObjectRecursion(String bucketName, String key) {
		Set<String> listObjectRecursion = new HashSet<String>();
		ObjectListing objectListing = null;
		try {
			objectListing = s3Client.listObjects(bucketName, key);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
		Iterator<?> iterator = objectListing.getObjectSummaries().iterator();
		while (iterator.hasNext()) {
			S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
			listObjectRecursion.add(bucketName + "/" + summary.getKey());
		}
		return listObjectRecursion;
	}
}
