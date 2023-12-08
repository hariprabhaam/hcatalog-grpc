FROM apache/beam_java8_sdk:2.51.0 as beam_sdk

FROM gcr.io/cloud-metastore-public/hms-grpc-proxy/3.1.2:v0.0.46 as hms_grpc_proxy

FROM openjdk:8

RUN mkdir -p /etc/hadoop/conf
RUN mkdir -p /opt/apache/beam

COPY --from=beam_sdk /opt/apache/beam /opt/apache/beam
COPY --from=hms_grpc_proxy /etc/hadoop/conf /etc/hadoop/conf
COPY --from=hms_grpc_proxy /usr/src/hms-proxy/hms-proxy.jar hms-proxy.jar

COPY ./scripts/entrypoint.sh /app/entrypoint.sh
RUN chmod a+x /app/entrypoint.sh

ENTRYPOINT [ "/app/entrypoint.sh" ]