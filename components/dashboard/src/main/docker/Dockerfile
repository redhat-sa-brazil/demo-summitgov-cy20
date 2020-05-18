####
# This Dockerfile is used in order to build a container that runs the Quarkus application in native (no JVM) mode
#
# Before building the docker image run:
#
# mvn package -Pnative -Dquarkus.native.container-build=true
#
# Then, build the image with:
#
# docker build -f src/main/docker/Dockerfile.native -t quarkus/saude-digital-dashboard .
#
# Then run the container using:
#
# docker run -i --rm -p 8080:8080 quarkus/saude-digital-dashboard
#
###
FROM registry.access.redhat.com/ubi8/ubi
#registry.access.redhat.com/ubi8/ubi-minimal:8.1
WORKDIR /work/
COPY target/*-runner /work/application.zip

#
# set up permissions for user `1001`
RUN yum install unzip -y \
  && unzip application.zip \
  && mv saude-digital-dashboard-1.0.0-SNAPSHOT-runner application \
  && chmod 775 /work /work/application \
  && ls -lh application\
  && chown -R 1001 /work \
  && chmod -R "g+rwX" /work \
  && chown -R 1001:root /work

EXPOSE 8080
USER 1001

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]