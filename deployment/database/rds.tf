resource "aws_db_instance" "narcissus_database" {
  allocated_storage = 30
  db_name = "narcissus"
  engine = "aurora-postgresql"
  username = vars.database_username
  password = vars.database_password
  deletion_protection = true
}
