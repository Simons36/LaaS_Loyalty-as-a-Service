#!/bin/bash

source ./access.sh

# #Terraform - Quarkus loyaltycard
cd Quarkus-Terraform/discount_coupon
terraform destroy -auto-approve
cd ../..

# #Terraform - Quarkus loyaltycard
cd Quarkus-Terraform/cross_selling_recomendation
terraform destroy -auto-approve
cd ../..


# #Terraform - RDS
cd RDS-Terraform
terraform destroy -auto-approve
cd ..

# # #Terraform - Kafka
cd Kafka
terraform destroy -auto-approve
cd ..
