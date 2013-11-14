#! /bin/bash

source pidlist.sh

type=$1
host=$2
api=api.$2

list=`pidlist $type $api`

echo "|| Aleph || URN || URI ||"
for i in $list
do

ntriple=`curl -s $api/resource/${i}.rdf -H"accept:text/plain"`;
aleph=`echo $ntriple | grep -o "http://purl.org/lobid/lv#hbzID[^\.]*"|sed s/"http.*>"/""/|sed s/"\""/""/g` 
urn=`echo $ntriple | grep -o "http://geni-orca.renci.org/owl/topology.owl#hasURN[^\.]*"|sed s/"http.*>"/""/|sed s/"\""/""/g| sed s/"\^\^.*"/""/`
uri=http://$host/resource/${i}

echo "| $aleph | $urn | $uri |"

done