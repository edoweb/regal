#! /bin/bash

cd /home/edoweb/edosync

cp .oaitimestamp oaitimestamp`date +"%Y%m%d"`

java -jar -Xms512m -Xmx512m edosync.jar --mode UPDT -list /home/edoweb/edosync/pidlist.txt --user fedoraAdmin --password fedoraAdmin1 --dtl http://klio.hbz-nrw.de:1801 --cache /home/edoweb/edobase --oai http://klio.hbz-nrw.de:1801/edowebOAI/ --set edoweb-oai_dc-all --timestamp .oaitimestamp --fedoraBase http://localhost.de:8080/fedora >> log`date +"%Y%m%d"`.txt 2>&1

cd -
