variable "environment" {
  default = "dev"
  type    = string
}

variable "aws_region" {
  default = "us-west-2"
  type    = string
}

variable "database_username" {
  default = "narcissus"
  type    = string
}

variable "database_password" {
  type      = string
  sensitive = true
}

variable "database_instance_class" {
  type    = string
  default = "db.serverless"
}
