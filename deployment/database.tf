module "aurora_cluster" {
  source = "terraform-aws-modules/rds-aurora/aws"

  name = "narcissus-postgres-${var.environment}-${var.aws_region}"

  # database configuration
  db_parameter_group_name = aws_db_parameter_group.narcissus.name
  engine                  = "aurora-postgresql"
  engine_version          = "15"
  master_password         = var.database_password
  master_username         = var.database_username

  # node configuration
  instance_class = var.database_instance_class
  instances = {
    main = {}
  }
  serverlessv2_scaling_configuration = {
    min_capacity = 1
    max_capacity = 4
  }
  storage_encrypted = true

  # networking
  vpc_id                = module.vpc.vpc_id
  db_subnet_group_name  = aws_db_subnet_group.narcissus_subnet_group.name
  create_security_group = false

  # monitoring and lifecycle
  auto_minor_version_upgrade      = true
  apply_immediately               = true
  monitoring_interval             = 10
  enabled_cloudwatch_logs_exports = ["postgresql"]
  skip_final_snapshot             = true
}

resource "aws_db_parameter_group" "narcissus" {
  name   = "narcissus-parameter-group-aurora-${var.environment}"
  family = "aurora-postgresql15"

  parameter {
    name  = "log_connections"
    value = "1"
  }

  parameter {
    name  = "log_min_duration_statement"
    value = "1000"
  }
}
