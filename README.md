# vCOS

Bài toán: Xây dựng bộ Sample Code cho Cloud Storage
Project (vCOS). Việc kết nối từ Client đến vCOS sử dụng giao thức S3 Compatible.
ECS Data Access Guide: https://www.delltechnologies.com/asset/en-us/products/storage/technical-support/docu95766.pdf 
ECS REST API documentation: http://doc.isilon.com/ECS/3.4/API/apidocs/ 
ECS AdminGuide: https://www.dellemc.com/en-us/collaterals/unauth/technical-guides-support-information/products/storage/docu95698.pdf

Credential: 
-	Endpoint: serverHost.com.vn:port
-	Access key:""
-	Secretkey: ""
Về yêu cầu: 
-	Khách hàng cần Document guide hướng dẫn truy cập sử dụng Viettel Cloud Object Storage thông qua các API ngôn ngữ lập trình phổ biến như: Java, .Net, PhP, NodejS
-	Khởi tạo các sample code cho phép kết nối đến Viettel Cloud Object Storage 
-	Đảm bảo các tính năng cơ bản như:
o	BUCKET: PUT Bucket, PUT Bucket cors, PUT Bucket acl, PUT Bucket life cycle, PUT Bucket policy, PUT Bucket versioning, DELETE Bucket, DELETE Bucket cors, DELETE Bucket life cycle, DELETE Bucket policy, GET Bucket (List Objects), GET Bucket (List Objects) Version 2, GET Bucket cors, GET Bucket acl, GET Bucket life cycle, GET Bucket policy, GET Bucket Object versions, GET Bucket versioning, HEAD Bucket, List Multipart Uploads
o	OBJECT: GET Object, GET Object ACL, HEAD Object, PUT Object, PUT Object acl, PUT Object - Copy, OPTIONS object, GET Object tagging, PUT Object tagging, DELETE Object tagging, Initiate Multipart Upload, Upload Part, Upload Part - Copy, Complete Multipart Upload, Abort Multipart Upload, List Parts
Về Output: Sample code thực hiện các action nêu trên bằng các ngôn ngữ:
-	Java
-	C#
-	PhP
-	NodeJs
-	Python
-	Ruby

