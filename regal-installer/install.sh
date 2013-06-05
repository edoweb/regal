#! /bin/bash
#
# Author: Jan Schnasse
# schnasse@hbz-nrw.de
#

source variables.conf


function makeDir()
{
echo "Create ARCHIVE_HOME $ARCHIVE_HOME"
mkdir -v -p $ARCHIVE_HOME/src
mkdir -v $ARCHIVE_HOME/html
mkdir -v $ARCHIVE_HOME/sync
mkdir -v $ARCHIVE_HOME/fedora
mkdir -v $ARCHIVE_HOME/${MODULE}base
mkdir -v $ARCHIVE_HOME/logs
mkdir -v $ARCHIVE_HOME/conf
mkdir -v $ARCHIVE_HOME/bin
mkdir -v -p $ARCHIVE_HOME/proai/cache
mkdir -v -p $ARCHIVE_HOME/proai/sessions
mkdir -v -p $ARCHIVE_HOME/proai/schemas
}

function createConfig()
{
echo "write install.properties"

echo "keystore.file=default" > $ARCHIVE_HOME/conf/install.properties
echo "ri.enabled=true" >> $ARCHIVE_HOME/conf/install.properties
echo "messaging.enabled=true" >> $ARCHIVE_HOME/conf/install.properties
echo "apia.auth.required=false" >> $ARCHIVE_HOME/conf/install.properties
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
echo "fesl.authn.enabled=false" >> $ARCHIVE_HOME/conf/install.properties
echo "fedora.home=$ARCHIVE_HOME/fedora" >> $ARCHIVE_HOME/conf/install.properties
echo "install.type=custom" >> $ARCHIVE_HOME/conf/install.properties
echo "servlet.engine=included" >> $ARCHIVE_HOME/conf/install.properties
echo "apim.ssl.required=false" >> $ARCHIVE_HOME/conf/install.properties
echo "fedora.admin.pass=$ARCHIVE_PASSWORD" >> $ARCHIVE_HOME/conf/install.properties
echo "apia.ssl.required=false" >> $ARCHIVE_HOME/conf/install.properties
echo >> $ARCHIVE_HOME/conf/install.properties

echo "write fedora-users.xml"

echo -e "<?xml version='1.0' ?>" > $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "  <users>" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "    <user name=\"$ARCHIVE_USER\" password=\"$ARCHIVE_PASSWORD\">" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "      <attribute name=\"fedoraRole\">" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "        <value>administrator</value>" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "      </attribute>" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "    </user>" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "    <user name=\"fedoraIntCallUser\" password=\"changeme\">" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "      <attribute name=\"fedoraRole\">" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "        <value>fedoraInternalCall-1</value>" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "        <value>fedoraInternalCall-2</value>" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "      </attribute>" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "    </user>" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "  </users>" >> $ARCHIVE_HOME/conf/fedora-users.xml
echo -e "" >> $ARCHIVE_HOME/conf/fedora-users.xml


echo "write api.properties"

echo "serverName=http://$SERVER"  > $ARCHIVE_HOME/conf/api.properties
echo "fedoraExtern=http://$SERVER/fedora"  >> $ARCHIVE_HOME/conf/api.properties
echo "fedoraIntern=http://localhost:$TOMCAT_PORT/fedora" >> $ARCHIVE_HOME/conf/api.properties
echo "user=$ARCHIVE_USER" >> $ARCHIVE_HOME/conf/api.properties
echo "password=$ARCHIVE_PASSWORD" >> $ARCHIVE_HOME/conf/api.properties
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

echo "write elasticsearch.yml"

echo "cluster.name: $MODULE$SERVER" > $ARCHIVE_HOME/conf/elasticsearch.yml
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
echo -e "RewriteRule /fedora/(.*) http://localhost:$TOMCAT_PORT/fedora/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /search/(.*) http://localhost:$ELASTICSEARCH_PORT/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule ^/resources/(.*) http://localhost:$TOMCAT_PORT/api/resources/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /journal/(.*) http://localhost:$TOMCAT_PORT/api/journal/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /monograph/(.*) http://localhost:$TOMCAT_PORT/api/monograph/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /webpage/(.*) http://localhost:$TOMCAT_PORT/api/webpage/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /utils/(.*)  http://localhost:$TOMCAT_PORT/api/utils/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /volume/(.*)  http://localhost:$TOMCAT_PORT/api/volume/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /version/(.*)  http://localhost:$TOMCAT_PORT/api/version/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /file/(.*)  http://localhost:$TOMCAT_PORT/api/file/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /issue/(.*)  http://localhost:$TOMCAT_PORT/api/issue/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /article/(.*)  http://localhost:$TOMCAT_PORT/api/article/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf
echo -e "RewriteRule /supplement/(.*)  http://localhost:$TOMCAT_PORT/api/article/\$1 [P]" >> $ARCHIVE_HOME/conf/site.conf

echo -e "RewriteRule /oai-pmh/(.*) http://localhost:$TOMCAT_PORT/oai-pmh/\$1 [P] " >> $ARCHIVE_HOME/conf/site.conf
echo -e "" >> $ARCHIVE_HOME/conf/site.conf
echo -e "</VirtualHost>" >> $ARCHIVE_HOME/conf/site.conf

}

function install()
{
echo "download some files"

if [ -f $ARCHIVE_HOME/src/README.textile ]
then
	echo "regal src clone already exists. Stop cloning!"
else
	git clone https://github.com/edoweb/regal.git $ARCHIVE_HOME/src
	cd $ARCHIVE_HOME/src
 	git checkout -b test origin/test
	git checkout master
	cd -
fi

if [ -f fcrepo-installer-3.6.1.jar ]
then
	echo "fcrepo is already here! Stop downloading!"
else
	wget http://repo1.maven.org/maven2/org/fcrepo/fcrepo-installer/3.6.1/fcrepo-installer-3.6.1.jar
fi

if [ -f elasticsearch-0.19.11.tar.gz ]
then
	echo "elasticsearch is already here! Stop downloading!"
else
	wget http://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-0.19.11.tar.gz
fi

echo "install fedora"
export FEDORA_ARCHIVE_HOME=$ARCHIVE_HOME/fedora
export CATALINA_ARCHIVE_HOME=$ARCHIVE_HOME/fedora/tomcat
java -jar fcrepo-installer-3.6.1.jar  $ARCHIVE_HOME/conf/install.properties

echo "install elasticsearch"
tar -xzf elasticsearch-0.19.11.tar.gz
mv elasticsearch-0.19.11 $ARCHIVE_HOME/elasticsearch
$ARCHIVE_HOME/elasticsearch/bin/elasticsearch
pwd
cp variables.conf $ARCHIVE_HOME/bin/
}

function copyConfig()
{
echo "copy tomcat config"
cp  $ARCHIVE_HOME/conf/tomcat-users.xml $ARCHIVE_HOME/fedora/tomcat/conf
cp  $ARCHIVE_HOME/conf/fedora-users.xml $ARCHIVE_HOME/fedora/server/config/
cp  $ARCHIVE_HOME/conf/setenv.sh $ARCHIVE_HOME/fedora/tomcat/bin
echo "copy elasticsearch config"
mv $ARCHIVE_HOME/elasticsearch/config/elasticsearch.yml $ARCHIVE_HOME/elasticsearch/config/elasticsearch.yml.bck
cp $ARCHIVE_HOME/conf/elasticsearch.yml $ARCHIVE_HOME/elasticsearch/config/
#echo "copy apache conf"
#cp $APACHE_CONF $ARCHIVE_HOME/conf/httpd.conf
#echo "Include $ARCHIVE_HOME/conf/site.conf" >> $ARCHIVE_HOME/conf/httpd.conf
echo "install archive"
cp  $ARCHIVE_HOME/conf/api.properties $ARCHIVE_HOME/src/regal-api/src/main/resources
}

function updateMaster()
{
echo "Fetch source..."
cd $ARCHIVE_HOME/src
git pull
git checkout master
cp  $ARCHIVE_HOME/conf/api.properties $ARCHIVE_HOME/src/regal-api/src/main/resources
echo "Start maven build. If this is your first regal installation, downloading all dependencies can take a lot of time."
mvn -e clean install -DskipTests --settings settings.xml
cd -
}

function updateTest()
{
echo "Fetch source..."
cd $ARCHIVE_HOME/src
git pull
git checkout test
cp  $ARCHIVE_HOME/conf/api.properties $ARCHIVE_HOME/src/regal-api/src/main/resources
echo "Start maven build. If this is your first regal installation, downloading all dependencies can take a lot of time."
mvn -e clean install -DskipTests --settings settings.xml
cd -
}

function rollout()
{
SRC=$ARCHIVE_HOME/src
WEBAPPS=$ARCHIVE_HOME/fedora/tomcat/webapps
SYNCER_SRC=$SRC/${MODULE}-sync/target/${MODULE}-sync-0.0.1-SNAPSHOT-jar-with-dependencies.jar
SYNCER_DEST=$ARCHIVE_HOME/sync/${MODULE}sync.jar

export FEDORA_HOME=$ARCHIVE_HOME/fedora
export CATALINA_HOME=$FEDORA_HOME/tomcat

if [ ! -d $ARCHIVE_HOME/${MODULE}base ]
then
	mkdir -v $ARCHIVE_HOME/${MODULE}base
fi
ln -s $ARCHIVE_HOME/${MODULE}base $ARCHIVE_HOME/html/${MODULE}base > /dev/null 2>&1
$ARCHIVE_HOME/fedora/tomcat/bin/shutdown.sh > /dev/null 2>&1
if [ $? -eq 0 ]
then
	echo "Tomcat successfully shutdown!"
else
	echo "Tomcat shutdown failed!"
fi


cd $SRC/regal-api
echo "Install Webapi"
mvn -q -e war:war -DskipTests --settings ../settings.xml
cd -
rm -rf  $WEBAPPS/api*
cp $SRC/regal-api/target/api.war $WEBAPPS/api.war


rm -rf  $WEBAPPS/oai-pmh*

cp $SRC/regal-ui/bin/oai-pmh.war $WEBAPPS
$ARCHIVE_HOME/fedora/tomcat/bin/startup.sh

if [ -n "$MODULE" ]
then
	echo "Generate Module $MODULE, templates can be found in $ARCHIVE_HOME/sync"
	cd $SRC/${MODULE}-sync
	mvn -q -e assembly:assembly -DskipTests --settings ../settings.xml
	cd -
	cp $SYNCER_SRC $SYNCER_DEST 
	

	echo -e "#! /bin/bash" > ${NAMESPACE}Sync.sh.tmpl
	echo -e "" >> ${NAMESPACE}Sync.sh.tmpl
	echo -e "source $ARCHIVE_HOME/sync/${NAMESPACE}Variables.conf" >> ${NAMESPACE}Sync.sh.tmpl
	echo -e "export LANG=en_US.UTF-8" >> ${NAMESPACE}Sync.sh.tmpl
	echo -e "" >> ${NAMESPACE}Sync.sh.tmpl
	echo -e "cd \$ARCHIVE_HOME/sync" >> ${NAMESPACE}Sync.sh.tmpl
	echo -e "" >> ${NAMESPACE}Sync.sh.tmpl
	echo -e "cp .oaitimestamp\$NAMESPACE oaitimestamp\${NAMESPACE}\`date +\"%Y%m%d\"\`" >> ${NAMESPACE}Sync.sh.tmpl
	echo -e "" >> ${NAMESPACE}Sync.sh.tmpl
	echo -e "java -jar -Xms512m -Xmx512m \${MODULE}sync.jar --mode INIT -list \$ARCHIVE_HOME/sync/pidlist.txt --user \$ARCHIVE_USER --password \$ARCHIVE_PASSWORD --dtl \$DOWNLOAD --cache \$ARCHIVE_HOME/\${NAMESPACE}base --oai  \$OAI --set \$SET --timestamp .oaitimestamp\$NAMESPACE --fedoraBase http://\$SERVER:\$TOMCAT_PORT/fedora --host http://\$SERVER --namespace \$NAMESPACE >> ${NAMESPACE}log\`date +\"%Y%m%d\"\`.txt 2>&1" >> ${NAMESPACE}Sync.sh.tmpl
	echo -e "" >> ${NAMESPACE}Sync.sh.tmpl
	echo -e "cd -" >> ${NAMESPACE}Sync.sh.tmpl

	mv ${NAMESPACE}Sync.sh.tmpl $ARCHIVE_HOME/sync
	cat $MODULE_CONF variables.conf > $ARCHIVE_HOME/sync/${NAMESPACE}Variables.conf.tmpl
fi

echo "copy html"
cp -r $ARCHIVE_HOME/src/regal-ui/htdocs/* $ARCHIVE_HOME/html/
sed "s/localhost/$SERVER/g" $ARCHIVE_HOME/html/js/EasyEllinetSearch.js > tmp && mv tmp "$ARCHIVE_HOME/html/js/EasyEllinetSearch.js"

#cp $SRC/regal-ui/conf/proai.properties $WEBAPPS/oai-pmh/WEB-INF/classes
cp $SRC/regal-installer/install.sh $ARCHIVE_HOME/bin/
}

usage="Wrong usage! Please try with: \n -u to update to last release \n -u test to update to last test build. \n -ext <sync.conf>. to create a sync module.";
if [ $# -eq 0 ]
then
	makeDir
	createConfig
	install
	copyConfig
        updateMaster
	rollout
elif [ $# -eq 1 ]
then
    
    if [ $1 == "-u" ]
    then
	makeDir
	createConfig
	copyConfig
	updateMaster
	rollout     
    else
	echo -e $usage
    fi
else

    if [[ $1 == "-u" ]] && [[ $2 == "test" ]]
    then
	makeDir
	createConfig
	copyConfig
	updateTest
	rollout
    elif [[ $1 == "-ext" ]] && [[ -n "$2" ]]
    then
	MODULE_CONF=$2
	source $MODULE_CONF
	rollout
    else
	echo -e $usage
    fi
fi

