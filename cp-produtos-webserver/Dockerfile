FROM seniocaires/apache-tomee-plume
MAINTAINER Senio Caires <seniocaires@gmail.com>

ADD tomee/tomcat-users.xml /opt/apache-tomee-plume-1.7.4/conf/
ADD tomee/tomee.xml /opt/apache-tomee-plume-1.7.4/conf/
ADD tomee/mysql-connector-java-5.1.36-bin.jar /opt/apache-tomee-plume-1.7.4/lib/

RUN apt-get update && apt-get install -y git maven

RUN cd /tmp && git clone https://github.com/seniocaires/cp-produtos.git && cd /tmp/cp-produtos && mvn clean install && cp /tmp/cp-produtos/target/cp-produtos.war /opt/apache-tomee-plume-1.7.4/webapps

