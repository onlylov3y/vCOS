package com.s3.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

public class S3MultipartManager {
	
	@SuppressWarnings("unused")
	private final S3Setting s3Setting;
	private final AmazonS3Client s3Client;

	public S3MultipartManager() {
		s3Setting = new S3Setting();
		// Create S3 Client object using AWS KEY & SECRET
		s3Client = new AmazonS3Client(new BasicAWSCredentials(S3Setting.getAccessKey(), S3Setting.getSecretKey()));
		s3Client.setEndpoint(S3Setting.getEndPoint());
		S3ClientOptions options = new S3ClientOptions();
		options.setPathStyleAccess(true);
		s3Client.setS3ClientOptions(options);
	}
	
	public void multipartUpload(String bucketName, String keyName, String filePath) {
		multipartUpload(bucketName, keyName, filePath, -1);
	}

	public void multipartUpload(String bucketName, String keyName, String filePath, long partSizeMb) {
		if (partSizeMb <= 5) {
			System.out.println("partSize is been setted default = 5Mb");
			partSizeMb = 5;
		}

		File file = new File(filePath);
		long contentLength = file.length();
		long partSize = partSizeMb * 1024 * 1024; // Set part size to 5 MB.
		List<PartETag> partETags = new ArrayList<PartETag>();
		try {
			// Initiate the multipart upload.
			InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);
			InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);
			
			// Upload the file parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Because the last part could be less than 5 MB, adjust the part size as needed.
                partSize = Math.min(partSize, (contentLength - filePosition));

                // Create the request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(keyName)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);

                // Upload the part and add the response's ETag to our list.
                UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);
                partETags.add(uploadResult.getPartETag());

                filePosition += partSize;
            }

            // Complete the multipart upload.
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
            		bucketName, keyName, initResponse.getUploadId(), partETags);
            s3Client.completeMultipartUpload(compRequest);
            
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	public List<MultipartUpload> listMultipartUploads(String bucketName) {
		try {
			ListMultipartUploadsRequest allMultipartUploadsRequest = new ListMultipartUploadsRequest(bucketName);
			MultipartUploadListing multipartUploadListing = s3Client.listMultipartUploads(allMultipartUploadsRequest);
			return multipartUploadListing.getMultipartUploads();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	public void abortMultipart(String bucketName) {
		List<MultipartUpload> uploads = listMultipartUploads(bucketName);
		// Abort each upload.
		for (MultipartUpload u : uploads) {
			System.out.println("Upload in progress: Key = \"" + u.getKey() + "\", id = " + u.getUploadId());
			s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, u.getKey(), u.getUploadId()));
			System.out.println("Upload deleted: Key = \"" + u.getKey() + "\", id = " + u.getUploadId());
		}
	}

	public void abortMultipart(String bucketName, String key, String id) {
		System.out.println("Upload in progress: Key = \"" + key + "\", id = " + id);
		s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, key, id));
		System.out.println("Upload deleted: Key = \"" + key + "\", id = " + id);
	}

}
