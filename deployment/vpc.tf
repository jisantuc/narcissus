data "aws_availability_zones" "available" {}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "2.77.0"

  name                 = "narcissus-vpc-${var.environment}-${var.aws_region}"
  cidr                 = "10.0.0.0/16"
  azs                  = data.aws_availability_zones.available.names
  public_subnets       = ["10.0.4.0/24", "10.0.5.0/24", "10.0.6.0/24"]
  enable_dns_hostnames = true
  enable_dns_support   = true
}

resource "aws_db_subnet_group" "narcissus_subnet_group" {
  name       = "narcissus-subnet-group-${var.environment}-${var.aws_region}"
  subnet_ids = module.vpc.public_subnets

  tags = {
    Name = "Narcissus"
  }
}
