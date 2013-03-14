#! /bin/bash

HOME=/opt/ellinet
SRC=ellinet
WEBAPP_SRC=edoweb2-api/target/edoweb2-api.war
WEBAPP_DEST=edoweb2-api.war
SYNCER_SRC=ellinetSync/target/ellinetSync-0.0.1-SNAPSHOT-jar-with-dependencies.jar
SYNCER_DEST=ellisync/ellisync.jar
FRONTENDSRC=ellinetDemoFront/htdocs
FRONTEND_DEST=ellihtml

echo "Update src must be done manually!"
echo "OK?"
 $HOME/fedora/tomcat/bin/shutdown.sh

echo "Compile..."
cd $HOME/$SRC
mvn clean install --settings  $HOME/$SRC/settings.xml
cd $HOME/$SRC/edowebSync
mvn assembly:assembly -o --settings  $HOME/$SRC/settings.xml
cd $HOME/$SRC/edoweb2-api
echo "Compile end ..."

echo "Install Webapi"

mvn war:war --settings  $HOME/$SRC/settings.xml
echo "Rollout..."
rm -rf  $HOME/fedora/tomcat/webapps/edoweb2-api*
cp $HOME/$SRC/$WEBAPP_SRC  $HOME/fedora/tomcat/webapps/$WEBAPP_DEST
cp $HOME/$SRC/§SYNCER_SRC $HOME/$SYNCER_DEST
$HOME/fedora/tomcat/bin/startup.sh
echo "FINISHED!"
echo install htdocs
cp -r $HOME/$SRC/FRONTEND_SRC/* $HOME/$FRONTEND_DEST

tail -f $HOME/tomcat/logs/catalina.out
