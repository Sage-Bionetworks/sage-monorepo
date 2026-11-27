output "bucket_arns" {
  description = "ARNs of the managed buckets, keyed by bucket name"
  value       = { for name, bucket in module.bucket : name => bucket.bucket_arn }
}
