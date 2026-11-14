terraform {
  required_providers {
    # cloudposse/mwaa/aws 0.16.0 sets worker_replacement_strategy, which the
    # aws_mwaa_environment resource only accepts from provider 6.x onwards
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
    http = {
      source  = "hashicorp/http"
      version = "~> 3.0"
    }
  }
}
