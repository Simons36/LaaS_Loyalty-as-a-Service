#!/bin/bash

source ./access.sh

# #Terraform - Quarkus - discount_coupon
cd Quarkus-Terraform/discount_coupon
terraform destroy -auto-approve
cd ../..

# #Terraform - Quarkus - cross_selling_recommendation
cd Quarkus-Terraform/cross_selling_recomendation
terraform destroy -auto-approve
cd ../..

# #Terraform - Quarkus shop
cd Quarkus-Terraform/shop
terraform destroy -auto-approve
cd ../..

# #Terraform - Quarkus customer
cd Quarkus-Terraform/customer
terraform destroy -auto-approve
cd ../..

# #Terraform - Quarkus purchase
cd Quarkus-Terraform/Purchase
terraform destroy -auto-approve
cd ../..

# #Terraform - Quarkus loyaltycard
cd Quarkus-Terraform/loyaltycard
terraform destroy -auto-approve
cd ../..

# #Terraform - Quarkus selled_product
cd Quarkus-Terraform/selled_product
terraform destroy -auto-approve
cd ../..

# #Terraform - Ollama
cd Ollama-Terraform
terraform destroy -auto-approve
cd ..

# #Terraform - RDS
cd RDS-Terraform
terraform destroy -auto-approve
cd ..

# # #Terraform - Kafka
cd Kafka
terraform destroy -auto-approve
cd ..
