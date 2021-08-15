package com.s3.core;

import java.util.ArrayList;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;

public class S3BucketAcl {
	
	@SuppressWarnings("unused")
	private final S3Setting s3Setting;
	private final AmazonS3Client s3Client;

	public S3BucketAcl() {
		s3Setting = new S3Setting();
		s3Client = new AmazonS3Client(new BasicAWSCredentials(S3Setting.getAccessKey(), S3Setting.getSecretKey()));
		s3Client.setEndpoint(S3Setting.getEndPoint());
		S3ClientOptions options = new S3ClientOptions();
		options.setPathStyleAccess(true);
		s3Client.setS3ClientOptions(options);
	}
	
	public AccessControlList getBucketAcl(String bucketName) {
		try {
			return s3Client.getBucketAcl(bucketName);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	public void showBucketAcl(AccessControlList accessControlList) {
		if(accessControlList == null) {
			System.out.println("AccessControlList is null.");
			return;
		}
		System.out.println(accessControlList.toString());
		for(Grant g : accessControlList.getGrants()) {
			System.out.println(g.getGrantee() + " : " + g.getPermission());
		}
		System.out.println("============end list=============");
	}
	
	/**
	 * This class for authenticazed for bucket 
	 * 0 none
	 * 1 read
	 * 2 read Acp
	 * 4 write
	 * 8 write Acp
	 * 15 Full
	 * The first number be for user owner
	 * The second number be for ASW groups (access log)
	 * The latest number be for other user
	 */
	
	public void setBucketAcl(String bucketName, String permission) {
		// Create a collection of grants to add to the bucket.
        ArrayList<Grant> grantCollection = new ArrayList<Grant>();
        String [] permissionAr = null;
        if(permission.contains(","))
        	permissionAr = permission.split(",");
        else if(permission.contains("-"))
        	permissionAr = permission.split("-");
        else if(permission.contains("/"))
        	permissionAr = permission.split("/");
        if(permissionAr == null || permissionAr.length < 3) {
        	System.err.println("The permission format is error. Please add ',' or '-' or '/' for each owner/AWSgroup/other user."
							+ "\nExample: 15-0-1 mean set full permisson for owner, non-permission for AWS group, read for other user. ");
			return;
		}
		// Grant the account owner full control.
		int ownerP = Integer.parseInt(permissionAr[0]);
		if (ownerP != 0)
			grantCollection.addAll(parsePermission(new CanonicalGrantee(s3Client.getS3AccountOwner().getId()), ownerP));
		// Grant the LogDelivery group permission to write to the bucket.
		int LogDeliveryP = Integer.parseInt(permissionAr[1]);
		if (LogDeliveryP != 0)
			grantCollection.addAll(parsePermission(GroupGrantee.AuthenticatedUsers, LogDeliveryP));
		// Grant the other(ALL) user
		int allP = Integer.parseInt(permissionAr[2]);
		if (allP != 0)
			grantCollection.addAll(parsePermission(GroupGrantee.AllUsers, allP));

		AccessControlList bucketAcl = new AccessControlList();
        bucketAcl.grantAllPermissions(grantCollection.toArray(new Grant[0]));
        bucketAcl.setOwner(s3Client.getS3AccountOwner());
        //showBucketAcl(bucketAcl);
        s3Client.setBucketAcl(bucketName, bucketAcl);
	}
	
	/**
	 * This class for authenticazed for bucket 
	 * 0 none
	 * 1 read
	 * 2 read Acp
	 * 4 write
	 * 8 write Acp
	 * 15 Full
	 * The first number be for user owner
	 * The second number be for ASW groups (access log)
	 * The latest number be for other user
	 */
	public void setBucketAcl(String bucketName, String userEmail, int permission) {
		ArrayList<Grant> grantCollection = new ArrayList<Grant>();
        // Grant the special account by email
        grantCollection.addAll(parsePermission(new EmailAddressGrantee(userEmail), permission));
        
        AccessControlList bucketAcl = s3Client.getBucketAcl(bucketName);
        bucketAcl.grantAllPermissions(grantCollection.toArray(new Grant[0]));
        bucketAcl.setOwner(s3Client.getS3AccountOwner());
        showBucketAcl(bucketAcl);
        s3Client.setBucketAcl(bucketName, bucketAcl);
	}
	
	private ArrayList<Grant> parsePermission(Grantee grantee, int permission) {
		// Create a collection of grants to add to the bucket.
		ArrayList<Grant> grantCollection = new ArrayList<Grant>();
		if (permission == 0)
			return null;
		if (permission == 15) {
			grantCollection.add(new Grant(grantee, Permission.FullControl));
			return grantCollection;
		}
		if (permission >= 8) {
			grantCollection.add(new Grant(grantee, Permission.WriteAcp));
			permission-=8;
		}
		if (permission >= 4) {
			grantCollection.add(new Grant(grantee, Permission.Write));
			permission-=4;
		}
		if (permission >= 2) {
			grantCollection.add(new Grant(grantee, Permission.ReadAcp));
			permission-=2;
		}
		if (permission == 1) {
			grantCollection.add(new Grant(grantee, Permission.Read));
		}
		return grantCollection;
	}
}
