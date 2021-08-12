package com.s3.core;

import java.util.Iterator;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
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

	public void deleteObject(String bucketName, String objectKey) {
		try {
			s3Client.deleteObject(bucketName, objectKey);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
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
	
}
