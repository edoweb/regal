#! /bin/bash

cd /home/oai/Digitool2Fedora

cp .oaitimestamp oaitimestamp`date +"%Y%m%d"`

java -jar -Xms512m -Xmx512m dtl2fedora.jar --mode SYNC -list /home/oai/pidlist.txt --user fedoraAdmin --password fedoraAdmin1 --dtl http://klio.hbz-nrw.de:1801 --cache /home/oai/zbmed --oai http://klio.hbz-nrw.de:1801/zbmedOAI/ --set zbmed-oai_dc-all --timestamp .oaitimestamp --axisHome /home/oai/FedoraIngest/IngestClient/src/main/resources --fedoraBase http://localhost:8080/fedora --htmlExport /home/oai/ellinet >> log`date +"%Y%m%d"`.txt 2>&1

cd -


