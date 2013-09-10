#! /bin/bash
type=$1
server=$2
pidlist=`./list.sh $type $server`
for i in $pidlist;do TT=`curl -s http://orthos.hbz-nrw.de/fedora/objects/$i/datastreams/DC/content|grep -o "HT[^<]*\|TT[^<]*"`; echo $i,$TT;done >tmp

sort tmp |uniq
rm tmp
