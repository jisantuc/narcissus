terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.16"
    }
  }

  required_version = ">= 1.2.0"

  backend "s3" {
    bucket = "narcissus-tf-state-${var.environment}-${var.aws_region}"
    key    = "tf-state"
    region = var.aws_region
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      environment       = var.environment
      project           = "Narcissus"
      terraform-managed = true
    }
  }
}

