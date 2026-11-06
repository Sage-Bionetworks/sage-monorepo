variable "enabled" {
  description = "When false no AWS resources are created. Set to true to provision the MWAA environment"
  type        = bool
  default     = false
}

variable "name" {
  description = "Name of the MWAA environment"
  type        = string
}

variable "region" {
  description = "AWS region the MWAA environment is created in"
  type        = string
  default     = "us-east-1"
}

variable "vpc_id" {
  description = "ID of the VPC the MWAA security group is created in. Required when enabled is true"
  type        = string
  default     = ""
}

variable "subnet_ids" {
  description = "Private subnet IDs for the MWAA environment. MWAA requires exactly two, in different availability zones. Required when enabled is true"
  type        = list(string)
  default     = []
}

variable "tags" {
  description = "Tags to apply to the MWAA resources"
  type        = map(string)
  default     = {}
}

variable "airflow_version" {
  description = "Airflow version of the MWAA environment. Empty uses the latest version MWAA supports. Raising the major version (for example 2.x to 3.x) replaces the environment"
  type        = string
  default     = ""
}

variable "orca_recipes_sha" {
  description = "Commit SHA of orca-recipes whose requirements-airflow.txt and mwaa/constraints.txt are installed on the environment"
  type        = string
}

variable "deployment_environment" {
  description = "dev or prod. Exposed to DAGs as AIRFLOW__ORCA__ENVIRONMENT, and selects the orca-recipes GitHub environment (mwaa-<value>) allowed to deploy DAGs"
  type        = string

  validation {
    condition     = contains(["dev", "prod"], var.deployment_environment)
    error_message = "deployment_environment must be dev or prod"
  }
}

variable "dag_s3_buckets" {
  description = "Names of S3 buckets that DAGs read and write as the MWAA execution role (S3Hook(aws_conn_id=None)). This only grants access. The dag-buckets stack creates the buckets"
  type        = list(string)
  default     = []
}
