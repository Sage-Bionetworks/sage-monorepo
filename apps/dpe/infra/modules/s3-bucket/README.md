# Purpose

This is a simple module that can be used within applications to create an S3 bucket.

## WARNING

If you are tearing down a stack with a deployed S3 Bucket, you will likely encounter an error similar to the following:

```
deleting S3 Bucket (my-beautiful-bucket): operation error S3: DeleteBucket, https response error StatusCode: 409, RequestID: 123, HostID: 123456789+g=, api error BucketNotEmpty: The bucket you tried to delete is not empty. You must delete all versions in the bucket.
```

We have intentionally not handled this behavior as a safeguard against accidental deletion of a bucket that contains important data. If you need to delete the bucket, you will need to manually delete all objects within it. If versioning is enabled for the bucket, you will also need to delete all versions of the objects.

# Usage

Using this module only requires calling it in your terraform code:

```
module "my_beautiful_bucket" {
  source      = "../s3-bucket"
  bucket_name = "my-beautiful-bucket"
  enable_versioning = false
}
```

The module makes only the bucket. Give access with the IAM policy of each user, for example `dag_s3_buckets` in `modules/mwaa`.
