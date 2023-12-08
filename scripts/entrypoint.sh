#!/bin/bash

echo "Starting the proxy as a background process"

java -cp /etc/hadoop/conf:hms-proxy.jar com.google.App --conf thrift.listening.port=9083 hive.metastore.uri=localhost:9083 proxy.mode=thrift proxy.uri=hive-grpc-193c5750-74k6dyuvza-uc.a.run.app:443 google.credentials.applicationdefault.enabled=true &

# Pass command arguments to the default boot script.
/opt/apache/beam/boot "$@"