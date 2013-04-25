#! /bin/bash

NORD_HOME=/opt/archive
PREFIX=edoweb
SERVER=localhost
USER=jan
PWD=schnasse

TOMCAT_PORT=8080
ELASTICSEARCH_PORT=9200

echo "Create HOME $NORD_HOME"
mkdir -v $NORD_HOME/src
mkdir -v $NORD_HOME/html
mkdir -v $NORD_HOME/sync
mkdir -v $NORD_HOME/elasticsearch
mkdir -v $NORD_HOME/${PREFIX}base

