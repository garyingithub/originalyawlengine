FROM frolvlad/alpine-oraclejdk8:slim
#FROM korekontrol/ubuntu-java-python2
VOLUME /tmp
ADD originalyawlengine-1.0-SNAPSHOT.jar app.jar
ADD yawldb.h2.db /tmp/yawldb.h2.db
RUN sh -c 'touch /app.jar'
ENTRYPOINT java -Xms2g -Xmx2g -Djava.security.egd=file:/dev/./urandom -jar /app.jar
