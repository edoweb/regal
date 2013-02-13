#! /bin/bash

echo "Update src must be done manually!"
echo "OK?"
/opt/fedora/tomcat/bin/shutdown.sh

echo "Compile..."
cd /opt/edoweb/edoweb2
mvn clean install -o
cd /opt/edoweb/edoweb2/edowebSync
mvn assembly:assembly -o
cd /opt/edoweb/edoweb2/edoweb2-api
echo "Compile end ..."

echo "Install Webapi"

mvn war:war -o
echo "Rollout..."
rm -rf /opt/fedora/tomcat/webapps/edoweb2-api*
cp /opt/edoweb/edoweb2/edoweb2-api/target/edoweb2-api.war /opt/fedora/tomcat/webapps/
cp /opt/edoweb/edoweb2/edowebSync/target/edowebSync-0.0.1-SNAPSHOT-jar-with-dependencies.jar /opt/edoweb/edosync/edosync.jar
/opt/fedora/tomcat/bin/startup.sh
echo "FINISHED!"
