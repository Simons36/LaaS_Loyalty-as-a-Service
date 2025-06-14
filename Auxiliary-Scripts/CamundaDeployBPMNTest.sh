#!/bin/bash

# Usage: ./deploy.sh path/to/main.bpmn http://localhost:8081/engine-rest [additional files...]

# Check if at least two arguments are provided
if [ "$#" -lt 2 ]; then
  echo "Usage: $0 <main_bpmn_file> <engine_rest_url> [additional_files...]"
  exit 1
fi

MAIN_BPMN="$1"
ENGINE_REST_URL="$2"
shift 2

# Start building the curl command
CURL_CMD=(curl -X POST "$ENGINE_REST_URL/deployment/create"
  -u demo:demo
  -H "Content-Type: multipart/form-data"
  -F "deployment-name=my-automated-deployment"
  -F "deployment-source=script"
  -F "enable-duplicate-filtering=true"
  -F "deploy-changed-only=false"
  -F "data1=@${MAIN_BPMN}"
)

# Add additional files as data2, data3, etc.
i=2
for file in "$@"; do
  CURL_CMD+=(-F "data${i}=@${file}")
  ((i++))
done

# Execute the command
"${CURL_CMD[@]}"
