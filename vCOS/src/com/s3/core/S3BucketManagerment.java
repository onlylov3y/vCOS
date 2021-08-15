package com.s3.core;

import java.util.List;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.MultipartUploadListing;
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
	
	public void listMultipartUploads(String bucketName) {
		try {
			// Retrieve a list of all in-progress multipart uploads.
			ListMultipartUploadsRequest allMultipartUploadsRequest = new ListMultipartUploadsRequest(bucketName);
			MultipartUploadListing multipartUploadListing = s3Client.listMultipartUploads(allMultipartUploadsRequest);
			List<MultipartUpload> uploads = multipartUploadListing.getMultipartUploads();

			// Display information about all in-progress multipart uploads.
			System.out.println(uploads.size() + " multipart upload(s) in progress.");
			for (MultipartUpload u : uploads) {
				System.out.println("Upload in progress: Key = \"" + u.getKey() + "\", id = " + u.getUploadId());
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}
	
}

