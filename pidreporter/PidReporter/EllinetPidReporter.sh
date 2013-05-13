#!/bin/bash
#Author: Jan Schnasse / Kuss
#Sprache: Java / Shell
#Ort: urania.hbz-nrw.de:/hbz/proc/schnasse/edoweb/PIDReporter
#Logs: PIDReporter.log*
#Config: pidreporter.properties
#Timestamp: .oaitimestamp
#Aufruf: java -jar pidreporter.jar pidreporter.properties
#Aufruf: ./PIDReporter.sh
#
#
#Konfiguration:
# pidreporter.properties
#
#Weiteres siehe: https://wiki.hbz-nrw.de/display/EDO/PIDReporter
#
PROPERTIES=ellinet.properties
# Timestamp Backup machen
TIMESTAMPDATEI=`grep 'timestamp' $PROPERTIES | sed 's/^.*=\(.*\)/\1/'`
AKTDAT=`date +"%Y%m%d%H%M%S"`
cp -p $TIMESTAMPDATEI $TIMESTAMPDATEI.$AKTDAT
java -jar pidreporter.jar $PROPERTIES

