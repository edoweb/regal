#! /bin/bash
#
# Author: Jan Schnasse
# schnasse@hbz-nrw.de
#

source variables.sh


function makeDir()
{
echo "Create ARCHIVE_HOME $ARCHIVE_HOME"
mkdir -v -p $ARCHIVE_HOME/src
mkdir -v $ARCHIVE_HOME/html
mkdir -v $ARCHIVE_HOME/sync
mkdir -v $ARCHIVE_HOME/fedora
mkdir -v $ARCHIVE_HOME/${PREFIX}base
mkdir -v $ARCHIVE_HOME/logs
mkdir -v $ARCHIVE_HOME/conf
}

function createConfig()
{
echo "write install.properties"

echo "keystore.file=default" > $ARCHIVE_HOME/conf/install.properties
echo "ri.enabled=true" >> $ARCHIVE_HOME/conf/install.properties
echo "messaging.enabled=true" >> $ARCHIVE_HOME/conf/install.properties
echo "apia.auth.required=true" >> $ARCHIVE_HOME/conf/install.properties
echo "database.jdbcDriverClass=org.apache.derby.jdbc.EmbeddedDriver" >> $ARCHIVE_HOME/conf/install.properties
echo "upstream.auth.enabled=false" >> $ARCHIVE_HOME/conf/install.properties
echo "tomcat.ssl.port=8443" >> $ARCHIVE_HOME/conf/install.properties
echo "ssl.available=true" >> $ARCHIVE_HOME/conf/install.properties
echo "database.jdbcURL=jdbc\:derby\:$ARCHIVE_HOME/fedora/derby/fedora3;create\=true" >> $ARCHIVE_HOME/conf/install.properties
echo "messaging.uri=vm\:(broker\:(tcp\://localhost\:61616))" >> $ARCHIVE_HOME/conf/install.properties
echo "database.password=$ARCHIVE_PASSWORD" >> $ARCHIVE_HOME/conf/install.properties
echo "keystore.type=JKS" >> $ARCHIVE_HOME/conf/install.properties
echo "database.username=$ARCHIVE_USER" >> $ARCHIVE_HOME/conf/install.properties
echo "fesl.authz.enabled=false" >> $ARCHIVE_HOME/conf/install.properties
echo "tomcat.shutdown.port=8005" >> $ARCHIVE_HOME/conf/install.properties
echo "deploy.local.services=false" >> $ARCHIVE_HOME/conf/install.properties
echo "xacml.enabled=true" >> $ARCHIVE_HOME/conf/install.properties
echo "tomcat.http.port=$TOMCAT_PORT" >> $ARCHIVE_HOME/conf/install.properties
echo "fedora.serverHost=$SERVER" >> $ARCHIVE_HOME/conf/install.properties
echo "database=included" >> $ARCHIVE_HOME/conf/install.properties
echo "database.driver=included" >> $ARCHIVE_HOME/conf/install.properties
echo "fedora.serverContext=fedora" >> $ARCHIVE_HOME/conf/install.properties
echo "keystore.password=changeit" >> $ARCHIVE_HOME/conf/install.properties
echo "llstore.type=akubra-fs" >> $ARCHIVE_HOME/conf/install.properties
echo "tomcat.home=$ARCHIVE_HOME/fedora/tomcat" >> $ARCHIVE_HOME/conf/install.properties
echo "fesl.authn.enabled=true" >> $ARCHIVE_HOME/conf/install.properties
echo "fedora.home=$ARCHIVE_HOME/fedora" >> $ARCHIVE_HOME/conf/install.properties
echo "install.type=custom" >> $ARCHIVE_HOME/conf/install.properties
echo "servlet.engine=included" >> $ARCHIVE_HOME/conf/install.properties
echo "apim.ssl.required=true" >> $ARCHIVE_HOME/conf/install.properties
echo "fedora.admin.pass=$ARCHIVE_PASSWORD" >> $ARCHIVE_HOME/conf/install.properties
echo "apia.ssl.required=false" >> $ARCHIVE_HOME/conf/install.properties
echo >> $ARCHIVE_HOME/conf/install.properties

echo "write api.properties"

echo "serverName=http://$SERVER"  > $ARCHIVE_HOME/conf/api.properties
echo "fedoraExtern=http://$SERVER/fedora"  >> $ARCHIVE_HOME/conf/api.properties
echo "fedoraIntern=http://$SERVER:$TOMCAT_PORT/fedora" >> $ARCHIVE_HOME/conf/api.properties
echo "user=$ARCHIVE_USER" >> $ARCHIVE_HOME/conf/api.properties
echo "password=$ARCHIVE_PASSWORD" >> $ARCHIVE_HOME/conf/api.properties
echo "namespace=$PREFIX" >> $ARCHIVE_HOME/conf/api.properties
echo "sesameStore=/tmp/myRepository" >> $ARCHIVE_HOME/conf/api.properties
echo "lobidUrl=http://lobid.org/resource/" >> $ARCHIVE_HOME/conf/api.properties
echo "verbundUrl=http://193.30.112.134/F/?func=find-c&ccl_term=IDN%3D" >> $ARCHIVE_HOME/conf/api.properties
echo "dataciteUrl=http://data.datacite.org/" >> $ARCHIVE_HOME/conf/api.properties
echo "baseUrl=http://www.base-search.net/Search/Results?lookfor=" >> $ARCHIVE_HOME/conf/api.properties
echo "culturegraphUrl=http://www.culturegraph.org/about/" >> $ARCHIVE_HOME/conf/api.properties
echo >> $ARCHIVE_HOME/conf/api.properties

echo "write tomcat-users.xml"

echo "<?xml version='1.0' encoding='utf-8'?>" > $ARCHIVE_HOME/conf/tomcat-users.xml
echo "<tomcat-users>" >> $ARCHIVE_HOME/conf/tomcat-users.xml
echo "<role rolename=\"manager\"/>" >>$ARCHIVE_HOME/conf/tomcat-users.xml
echo -e "<user username=\"$ARCHIVE_USER\" password=\"$ARCHIVE_PASSWORD\" roles=\"manager\"/>" >> $ARCHIVE_HOME/conf/tomcat-users.xml
echo "</tomcat-users>" >> $ARCHIVE_HOME/conf/tomcat-users.xml
echo >> $ARCHIVE_HOME/conf/tomcat-users.xml

echo "write setenv.xml"

echo -e "CATALINA_OPTS=\" \\"  > $ARCHIVE_HOME/conf/setenv.sh
echo -e "-Xms1536m \\" >> $ARCHIVE_HOME/conf/setenv.sh
echo -e "-Xmx1536m \\" >> $ARCHIVE_HOME/conf/setenv.sh
echo -e "-XX:NewSize=256m \\" >> $ARCHIVE_HOME/conf/setenv.sh
echo -e "-XX:MaxNewSize=256m \\" >> $ARCHIVE_HOME/conf/setenv.sh
echo -e "-XX:PermSize=256m \\"  >> $ARCHIVE_HOME/conf/setenv.sh
echo -e "-XX:MaxPermSize=256m \\" >> $ARCHIVE_HOME/conf/setenv.sh
echo -e "-server \\" >> $ARCHIVE_HOME/conf/setenv.sh
echo -e "-Djava.awt.headless=true \\" >> $ARCHIVE_HOME/conf/setenv.sh
echo -e "-Dorg.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER=true\"" >> $ARCHIVE_HOME/conf/setenv.sh
echo >> $ARCHIVE_HOME/conf/setenv.sh
echo -e "export CATALINA_OPTS" >> $ARCHIVE_HOME/conf/setenv.sh
echo >> $ARCHIVE_HOME/conf/setenv.sh

echo "write web.properties"

echo "security.ssl.api.management=REQUIRES_SECURE_CHANNEL" > $ARCHIVE_HOME/conf/web.properties
echo "security.auth.filters=AuthFilterJAAS" >> $ARCHIVE_HOME/conf/web.properties
echo "security.fesl.authN.jaas.apia.enabled=false" >> $ARCHIVE_HOME/conf/web.properties
echo "fedora.port=$TOMCAT_PORT" >> $ARCHIVE_HOME/conf/web.properties
echo "security.fesl.authZ.enabled=false" >>$ARCHIVE_HOME/conf/web.properties
echo "fedora.port.secure=8443" >> $ARCHIVE_HOME/conf/web.properties
echo "security.ssl.api.default=ANY_CHANNEL" >> $ARCHIVE_HOME/conf/web.properties
echo "security.ssl.api.access=ANY_CHANNEL" >> $ARCHIVE_HOME/conf/web.properties
echo  >> $ARCHIVE_HOME/conf/web.properties

echo "write elasticsearch.yml"

echo "cluster.name: $PREFIX$SERVER" > $ARCHIVE_HOME/conf/elasticsearch.yml
echo >> $ARCHIVE_HOME/conf/elasticsearch.yml

echo "write site.conf"

echo -e "<VirtualHost *:80>" > $ARCHIVE_HOME/conf/site.conf
echo -e "    ServerAdmin $EMAIL" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    DocumentRoot $ARCHIVE_HOME/html" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    <Directory />" >> $ARCHIVE_HOME/conf/site.conf
echo -e "	Options FollowSymLinks" >> $ARCHIVE_HOME/conf/site.conf
echo -e "	AllowOverride None" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    </Directory>" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    <Directory \"$ARCHIVE_HOME/html\">" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    	       Options Indexes FollowSymLinks" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    	       AllowOverride All" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    	       Order allow,deny" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    	       Allow from all" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    </Directory>" >> $ARCHIVE_HOME/conf/site.conf
echo -e "" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    ProxyRequests Off" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    ProxyPreserveHost On" >> $ARCHIVE_HOME/conf/site.conf
echo -e "" >> $ARCHIVE_HOME/conf/site.conf
echo -e "    <Proxy *>" >> $ARCHIVE_HOME/conf/site.conf
echo -e "       Order deny,allow" >> $ARCHIVE_HOME/conf/site.conf
echo -e "       Allow from all" >> $ARCHIVE_HOME/conf/site.conf
echo -e "     </Proxy>" >> $ARCHIVE_HOME/conf/site.conf
echo -e "" >> $ARCHIVE_HOME/conf/site.conf
echo -e "" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteEngine on" >> $ARCHIVE_HOME/conf/site.conf
echo -e "" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /fedora/(.*) http://localhost:$TOMCAT_PORT/fedora/$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /search/(.*) http://localhost:$ELASTICSEARCH_PORT/$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule ^/resources/(.*) http://localhost:$TOMCAT_PORT/edoweb2-api/resources/$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /ejournal/(.*) http://localhost:$TOMCAT_PORT/edoweb2-api/ejournal/$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /monograph/(.*) http://localhost:$TOMCAT_PORT/edoweb2-api/monograph/$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /webpage/(.*) http://localhost:$TOMCAT_PORT/edoweb2-api/webpage/$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /utils/(.*)  http://localhost:$TOMCAT_PORT/edoweb2-api/utils/$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /oai-pmh/(.*) http://localhost:$TOMCAT_PORT/oai-pmh/$1 [P] " >> $ARCHIVE_HOME/conf/site.conf
echo -e "" >> $ARCHIVE_HOME/conf/site.conf
echo -e "</VirtualHost>" >> $ARCHIVE_HOME/conf/site.conf

}

function install()
{
echo "download some files"
git clone https://github.com/jschnasse/edoweb2.git $ARCHIVE_HOME/src
wget http://ares.hbz-nrw.de/fcrepo-installer-3.6.1.jar 
wget http://ares.hbz-nrw.de/elasticsearch-0.19.11.tar.gz

echo "install fedora"
export FEDORA_ARCHIVE_HOME=$ARCHIVE_HOME/fedora
export CATALINA_ARCHIVE_HOME=$ARCHIVE_HOME/fedora/tomcat
java -jar fcrepo-installer-3.6.1.jar  $ARCHIVE_HOME/conf/install.properties
cp  $ARCHIVE_HOME/conf/tomcat-users.xml $ARCHIVE_HOME/fedora/tomcat/conf
cp  $ARCHIVE_HOME/conf/setenv.xml $ARCHIVE_HOME/fedora/tomcat/bin
cp  $ARCHIVE_HOME/conf/web.properties $ARCHIVE_HOME/fedora/server/config/spring/web/

echo "install elasticsearch"
tar -xzf elasticsearch-0.19.11.tar.gz
mv elasticsearch-0.19.11 $ARCHIVE_HOME/elasticsearch
mv $ARCHIVE_HOME/elasticsearch/config/elasticsearch.yml $ARCHIVE_HOME/elasticsearch/config/elasticsearch.yml.bck
cp $ARCHIVE_HOME/conf/elasticsearch.yml $ARCHIVE_HOME/elasticsearch/config/
$ARCHIVE_HOME/elasticsearch/bin/elasticsearch

echo "install apache"
cp $APACHE_CONF $ARCHIVE_HOME/conf/httpd.conf
echo "Include $ARCHIVE_HOME/conf/site.conf" >> $ARCHIVE_HOME/conf/httpd.conf

echo "install archive"
cp  $ARCHIVE_HOME/conf/api.properties $ARCHIVE_HOME/src/edoweb2-api/src/main/resources
$ARCHIVE_HOME/src/ui/helper/rollout.sh $PREFIX $ARCHIVE_HOME $ARCHIVE_USER $ARCHIVE_PASSWORD
}

function cleanUp()
{
rm fcrepo-installer-3.6.1.jar
rm elasticsearch-0.19.11.tar.gz
}

makeDir
createConfig
install
cleanUp
