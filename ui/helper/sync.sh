#! /bin/bash

PREFIX=$1
ARCHIVE_HOME=$2

export LANG=en_US.UTF-8

cd $ARCHIVE_HOME/sync

cp .oaitimestamp$PREFIX oaitimestamp$PREFIX`date +"%Y%m%d"`

java -jar -Xms512m -Xmx512m $PREFIXsync.jar --mode SYNC -list $ARCHIVE_HOME/sync/pidlist.txt --user $USER --password $PWD --dtl http://themis.hbz-nrw.de:9280/fedora/ --cache $ARCHIVE_HOME/${PREFIX}base --oai  http://www.dipp.nrw.de/repository/ --set pub-type:journal --timestamp .oaitimestamp$PREFIX --fedoraBase http://$SERVER:$TOMCAT_PORT/fedora --host http://localhost >> ${PREFIX}log`date +"%Y%m%d"`.txt 2>&1

cd -
