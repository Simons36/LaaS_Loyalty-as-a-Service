terraform { 
  required_version = ">= 1.0.0, < 2.0.0" 
 
  required_providers { 
    aws = { 
      source  = "hashicorp/aws" 
      version = "~> 4.0" 
    } 
  } 
} 
 
provider "aws" { 
  region      = "us-east-1" 
} 
 
resource "aws_instance" "exampleInstallKong" { 
  ami                     = "ami-045269a1f5c90a6a0" 
  instance_type           = "t2.small" 
  vpc_security_group_ids  = [aws_security_group.instance.id] 
  key_name                = "vockey" 
 
  user_data = "${file("kong.sh")}" 
 
  user_data_replace_on_change = true 
   
  tags = { 
    Name = "terraform-example-Kong" 
  } 
} 
 
resource "aws_security_group" "instance" { 
  name = var.security_group_name 
  ingress { 
    from_port   = 0 
    to_port     = 0 
    protocol    = "-1" 
    cidr_blocks = ["0.0.0.0/0"] 
    ipv6_cidr_blocks = ["::/0"] 
  } 
  egress { 
    from_port        = 0 
    to_port          = 0 
    protocol         = "-1" 
    cidr_blocks      = ["0.0.0.0/0"] 
    ipv6_cidr_blocks = ["::/0"] 
  } 
} 
 
variable "security_group_name" { 
  description = "The name of the security group" 
  type        = string 
  default     = "terraform-kong-instance" 
} 
 
output "address" { 
  value       = aws_instance.exampleInstallKong.public_dns 
  description = "Connect to the database at this endpoint" 
}
