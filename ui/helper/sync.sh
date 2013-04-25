#! /bin/bash

PREFIX=$1

export LANG=en_US.UTF-8

cd /opt/archive/sync

cp .oaitimestamp$PREFIX oaitimestamp$PREFIX`date +"%Y%m%d"`

java -jar -Xms512m -Xmx512m $PREFIXsync.jar --mode SYNC -list /opt/archive/sync/pidlist.txt --user fedoraAdmin --password fedoraAdmin1 --dtl http://themis.hbz-nrw.de:9280/fedora/ --cache /opt/archive/${PREFIX}base --oai  http://www.dipp.nrw.de/repository/ --set pub-type:journal --timestamp .oaitimestamp$PREFIX --fedoraBase http://localhost.de:8080/fedora --host http://localhost >> ${PREFIX}log`date +"%Y%m%d"`.txt 2>&1

cd -
