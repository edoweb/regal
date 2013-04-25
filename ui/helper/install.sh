#! /bin/bash

HOME=/opt/archive
PREFIX=edoweb
ROLLOUT=${PREFIX}Rollout.sh
SERVER=localhost
USER=jan
PWD=schnasse

TOMCAT_PORT=8080
ELASTICSEARCH_PORT=9200

echo "Create HOME $HOME"
mkdir -v $HOME/src
mkdir -v $HOME/html
mkdir -v $HOME/sync
mkdir -v $HOME/elasticsearch
mkdir -v $HOME/fedora
mkdir -v $HOME/${PREFIX}base

echo "keystore.file=default" > install.properties
echo "ri.enabled=true" >> install.properties
echo "messaging.enabled=true" >> install.properties
echo "apia.auth.required=true" >> install.properties
echo "database.jdbcDriverClass=org.apache.derby.jdbc.EmbeddedDriver" >> install.properties
echo "upstream.auth.enabled=false" >> install.properties
echo "tomcat.ssl.port=8443" >> install.properties
echo "ssl.available=true" >> install.properties
echo "database.jdbcURL=jdbc\:derby\:$HOME/fedora/derby/fedora3;create\=true" >> install.properties
echo "messaging.uri=vm\:(broker\:(tcp\://localhost\:61616))" >> install.properties
echo "database.password=$PWD" >> install.properties
echo "keystore.type=JKS" >> install.properties
echo "database.username=$USER" >> install.properties
echo "fesl.authz.enabled=false" >> install.properties
echo "tomcat.shutdown.port=8005" >> install.properties
echo "deploy.local.services=false" >> install.properties
echo "xacml.enabled=true" >> install.properties
echo "tomcat.http.port=$TOMCAT_PORT" >> install.properties
echo "fedora.serverHost=$SERVER" >> install.properties
echo "database=included" >> install.properties
echo "database.driver=included" >> install.properties
echo "fedora.serverContext=fedora" >> install.properties
echo "keystore.password=changeit" >> install.properties
echo "llstore.type=akubra-fs" >> install.properties
echo "tomcat.home=$HOME/fedora/tomcat" >> install.properties
echo "fesl.authn.enabled=true" >> install.properties
echo "fedora.home=$HOME/fedora" >> install.properties
echo "install.type=custom" >> install.properties
echo "servlet.engine=included" >> install.properties
echo "apim.ssl.required=true" >> install.properties
echo "fedora.admin.pass=$PWD" >> install.properties
echo "apia.ssl.required=false" >> install.properties

echo "serverName=http://$SERVER"  > api.properties
echo "fedoraExtern=http://$SERVER/fedora"  >> api.properties
echo "fedoraIntern=http://$SERVER:8080/fedora" >> api.properties
echo "user=$USER" >> api.properties
echo "password=$PWD" >> api.properties
echo "namespace=$PREFIX" >> api.properties
echo "sesameStore=/tmp/myRepository" >> api.properties
echo "lobidUrl=http://lobid.org/resource/" >> api.properties
echo "verbundUrl=http://193.30.112.134/F/?func=find-c&ccl_term=IDN%3D" >> api.properties
echo "dataciteUrl=http://data.datacite.org/" >> api.properties
echo "baseUrl=http://www.base-search.net/Search/Results?lookfor=" >> api.properties
echo "culturegraphUrl=http://www.culturegraph.org/about/" >> api.properties

git clone https://github.com/jschnasse/edoweb2.git $HOME/src
java -jar $HOME/src/ui/bin/fcrepo-installer-3.6.1.jar install.properties
cp api.properties $HOME/src/edoweb2-api/src/main/resources
sh $HOME/src/ui/helper/$ROLLOUT

