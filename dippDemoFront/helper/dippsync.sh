#! /bin/bash

export LANG=en_US.UTF-8

cd /opt/ellinet/ellisync

cp .oaitimestamp oaitimestamp`date +"%Y%m%d"`

java -jar -Xms512m -Xmx512m ellisync.jar --mode SYNC -list /opt/ellinet/ellisync/pidlist.txt --user fedoraAdmin --password fedoraAdmin1 --dtl http://klio.hbz-nrw.de:1801 --cache /opt/ellinet/ellibase --oai http://klio.hbz-nrw.de:1801/zbmedOAI/ --set zbmed-oai_dc-all --timestamp .oaitimestamp --fedoraBase http://localhost.de:8080/fedora --host http://localhost >> log`date +"%Y%m%d"`.txt 2>&1

cd -
