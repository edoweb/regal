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

echo "write install.properties"

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
echo >> install.properties

echo "write api.properties"

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
echo >> api.properties

echo "write tomcat-users.xml"

echo "<?xml version='1.0' encoding='utf-8'?>" > tomcat-users.xml
echo "<tomcat-users>" >>tomcat-users.xml
echo "<role rolename="manager"/>" >>tomcat-users.xml
echo -e "<user username=\"$USER\" password=\"$PWD\" roles="manager"/>" >>tomcat-users.xml
echo "</tomcat-users>" >>tomcat-users.xml
echo >> tomcat-users.xml

echo "write setenv.xml"

echo -e "CATALINA_OPTS=\" \\"  > setenv.xml
echo -e "-Xms1536m \\" >> setenv.xml
echo -e "-Xmx1536m \\" >> setenv.xml
echo -e "-XX:NewSize=256m \\" >> setenv.xml
echo -e "-XX:MaxNewSize=256m \\" >> setenv.xml
echo -e "-XX:PermSize=256m \\"  >> setenv.xml
echo -e "-XX:MaxPermSize=256m \\" >> setenv.xml
echo -e "-server \\" >> setenv.xml
echo -e "-Djava.awt.headless=true \\" >> setenv.xml
echo -e "-Dorg.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER=true\"" >> setenv.xml
echo >> setenv.xml
echo -e "export CATALINA_OPTS" >> setenv.xml
echo >> setenv.xml

echo "write web.properties"
echo "security.ssl.api.management=REQUIRES_SECURE_CHANNEL">web.properties
echo "security.auth.filters=AuthFilterJAAS" >>web.properties
echo "security.fesl.authN.jaas.apia.enabled=false" >>web.properties
echo "fedora.port=$TOMCAT_PORT" >>web.properties
echo "security.fesl.authZ.enabled=false" >>web.properties
echo "fedora.port.secure=8443" >>web.properties
echo "security.ssl.api.default=ANY_CHANNEL" >>web.properties
echo "security.ssl.api.access=ANY_CHANNEL" >>web.properties
echo  >>web.properties

echo "write elasticsearch.yml"

echo "cluster.name: $PREFIX$SERVER" > elasticsearch.yml
echo >> elasticsearch.yml

echo "download some files"
git clone https://github.com/jschnasse/edoweb2.git $HOME/src
wget http://ares.hbz-nrw.de/fcrepo-installer-3.6.1.jar 
wget http://ares.hbz-nrw.de/elasticsearch-0.19.11.tar.gz

echo "install fedora"
java -jar fcrepo-installer-3.6.1.jar install.properties
cp tomcat-users.xml $HOME/fedora/tomcat/conf
cp setenv.xml $HOME/fedora/tomcat/bin
cp web.properties $HOME/fedora/server/config/spring/web/

echo "install elasticsearch"
tar -xzf elasticsearch-0.19.11.tar.gz
mv elasticsearch-0.19.11 $HOME/elasticsearch
mv $HOME/elasticsearch/conf/elasticsearch.yml $HOME/elasticsearch/conf/elasticsearch.yml.bck
cp elasticsearch.yml $HOME/elasticsearch/conf/

echo "install api"
cp api.properties $HOME/src/edoweb2-api/src/main/resources
sh $HOME/src/ui/helper/$ROLLOUT

