FROM mysql:5.6
MAINTAINER Senio Caires <seniocaires@gmail.com>

ADD  docker-entrypoint-initdb.d/iniciar_banco.sql /docker-entrypoint-initdb.d/

RUN chmod 777 -R /docker-entrypoint-initdb.d/iniciar_banco.sql

ENV MYSQL_DATABASE=cpprodutosdb
ENV MYSQL_USER=cpprodutos
ENV MYSQL_PASSWORD=cpprodutospassword