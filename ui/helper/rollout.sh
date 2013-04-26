#! /bin/bash

PREFIX=$1
ARCHIVE_HOME=$2


SRC=$ARCHIVE_HOME/src
WEBAPPS=$ARCHIVE_HOME/fedora/tomcat/webapps
API_SRC=$SRC/edoweb2-api/target/edoweb2-api.war
API_DEST=$WEBAPPS/edoweb2-api.war
SYNCER_SRC=$SRC/${PREFIX}Sync/target/${PREFIX}Sync-0.0.1-SNAPSHOT-jar-with-dependencies.jar
SYNCER_DEST=$ARCHIVE_HOME/sync/${PREFIX}sync.jar
FRONTEND_SRC=$SRC/ui/htdocs
FRONTEND_DEST=$ARCHIVE_HOME/html

echo "Update src must be done manually!"
echo "OK?"
$ARCHIVE_HOME/fedora/tomcat/bin/shutdown.sh

echo "Compile..."
cd $SRC
mvn -q -e clean install
cd $SRC/${PREFIX}Sync
mvn -q -e assembly:assembly 
cd $SRC/edoweb2-api
echo "Compile end ..."

echo "Install Webapi"
mvn -q -e war:war
echo "Rollout..."
rm -rf  $WEBAPPS/edoweb2-api*
cp $API_SRC $API_DEST
cp $SYNCER_SRC $SYNCER_DEST 

rm -rf  $WEBAPPS/oai-pmh*
cp $SRC/ui/compiled/oai-pmh.war $WEBAPPS
cp $SRC/ui/conf/proai.properties $WEBAPPS/oai-pmh/WEB-INF/classes

$ARCHIVE_HOME/fedora/tomcat/bin/startup.sh
echo "FINISHED!"
echo install htdocs
cp -r $FRONTEND_SRC/* $FRONTEND_DEST

tail -f $ARCHIVE_HOME/fedora/tomcat/logs/catalina.out
