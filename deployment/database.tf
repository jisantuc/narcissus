#module "aurora_cluster" {
#  source = "terraform-aws-modules/rds-aurora/aws"
#
#  name           = "narcissus-postgres-${var.environment}-${var.aws_region}"
#  engine         = "aurora-postgresql"
#  engine_version = "15"
#  instance_class = "t4g.small"
#  instances = {
#    main = {}
#  }
#
#  vpc_id               = module.vpc.vpc_id
#  db_subnet_group_name = aws_db_subnet_group.narcissus_subnet_group.name
#
#  storage_encrypted   = true
#  apply_immediately   = true
#  monitoring_interval = 10
#
#  enabled_cloudwatch_logs_exports = ["postgresql"]
#}

resource "aws_db_instance" "narcissus_database" {
  identifier           = "narcissus-database-${var.environment}-${var.aws_region}"
  db_name              = "narcissus"
  allocated_storage    = 10
  engine               = "postgres"
  instance_class       = var.database_instance_class
  username             = var.database_username
  password             = var.database_password
  db_subnet_group_name = aws_db_subnet_group.narcissus_subnet_group.name
  parameter_group_name = aws_db_parameter_group.narcissus.name
  # vpc_security_group_ids = [aws_security_group.rds.id]
  deletion_protection = true
  skip_final_snapshot = true
}

resource "aws_db_parameter_group" "narcissus" {
  name   = "narcissus-parameter-group-${var.environment}"
  family = "postgres14"

  parameter {
    name  = "log_connections"
    value = "1"
  }

  parameter {
    name  = "log_min_duration_statement"
    value = "1000"
  }
}
