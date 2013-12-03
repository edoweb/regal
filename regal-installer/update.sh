#! /bin/bash
#
# Author: Jan Schnasse
# schnasse@hbz-nrw.de
#

source variables.conf

function changeToInstallDir()
{
cp $ARCHIVE_HOME/src/regal-installer/update.sh $ARCHIVE_HOME/bin
cd $ARCHIVE_HOME/bin
}

function shutdownElasticsearch()
{
`ps -ef | grep "elasticsearch" | awk '{print $2}' | xargs kill `
}

function removeElasticsearch()
{
rm -rf $ARCHIVE_HOME/elasticsearch/
}

function downloadElasticsearch()
{
wget http://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-0.90.5.tar.gz
tar -xzf elasticsearch-0.90.5.tar.gz
mv elasticsearch-0.90.5 $ARCHIVE_HOME/elasticsearch
}

function startupElasticsearch()
{
$ARCHIVE_HOME/elasticsearch/bin/elasticsearch
}

changeToInstallDir
shutdownElasticsearch
removeElasticsearch
downloadElasticsearch
startupElasticsearch

