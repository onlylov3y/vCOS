package com.s3.core;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;

public class S3BucketVersioning {

	private final S3Setting s3Setting;
	private final AmazonS3Client s3Client;

	public S3BucketVersioning() {
		s3Setting = new S3Setting();
		s3Client = new AmazonS3Client(new BasicAWSCredentials(S3Setting.getAccessKey(), S3Setting.getSecretKey()));
		s3Client.setEndpoint(S3Setting.getEndPoint());
		S3ClientOptions options = new S3ClientOptions();
		options.setPathStyleAccess(true);
		s3Client.setS3ClientOptions(options);
	}
	
	public void getBucketVersioning(String bucketName) {
		getBucketVersioning(bucketName, null);
	}
	
	public void getBucketVersioning(String bucketName, String prefixPath) {
		ListVersionsRequest request = new ListVersionsRequest()
				.withBucketName(bucketName)
				.withMaxResults(10);
		VersionListing versionListing;
		if(prefixPath == null)
			versionListing = s3Client.listVersions(request);
		else 
			versionListing = s3Client.listVersions(bucketName, prefixPath);
		
		int numVersions = 0, numPages = 0;
        while (true) {
            numPages++;
            for (S3VersionSummary objectSummary : versionListing.getVersionSummaries()) {
                System.out.printf("Retrieved object %s, version %s\n",
                        objectSummary.getKey(),
                        objectSummary.getVersionId());
                numVersions++;
            }
            if (versionListing.isTruncated()) {
                versionListing = s3Client.listNextBatchOfVersions(versionListing);
            } else {
                break;
            }
        }
        System.out.println(numVersions + " object versions retrieved in " + numPages + " pages");
	}

}
