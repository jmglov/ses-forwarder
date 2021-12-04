# SES Forwarder

## Building

``` sh
clojure -X:uberjar
```

## Deploying

The first time you deploy, you need to create the function:

``` sh
export AWS_ACCOUNT=$(aws sts get-caller-identity | jq -r .Account)

aws lambda create-function \
  --function-name ses-forwarder \
  --handler ses_forwarder.handler \
  --runtime java11 \
  --memory 2048 \
  --timeout 10 \
  --role arn:aws:iam::${AWS_ACCOUNT}:role/mail-forwarder \
  --zip-file fileb://./build/output.jar
```

Subsequently, you can just update the function code:

``` sh
aws lambda update-function-code \
  --function-name ses-forwarder \
  --zip-file fileb://./build/output.jar
```
