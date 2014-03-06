#! /bin/bash

function pidlist()
{
type=$1
server=$2
fromSrc=$3
from=$4
until=$5
curl -XGET "http://$server/resource?type=$type&getListingFrom=$fromSrc&from=$from&until=$until" 2> /dev/null| sed s/"{\"list\":"/""/g | sed s/"\,"/"\n"/g | sed s/"\"\([^\"]*\)".*/"\1"/g | sed s/"^\["/""/g
echo
}

function index()
{
type=$1
user=$2
password=$3
server=$4
for i in `pidlist $type $server repo 0 10000`
do
curl -s -u ${user}:${password} -XPOST "http://$server/utils/index/$i?type=$type";echo
done
}

function generateIdTable()
{
type=$1
host=$2
api=api.$2

list=`pidlist $type $api es 0 30000`

echo "|| Aleph || URN || URI ||"
for i in $list
do

ntriple=`curl -s $api/resource/${i}.rdf -H"accept:text/plain"`;
aleph=`echo $ntriple | grep -o "http://purl.org/lobid/lv#hbzID[^\.]*"|sed s/"http.*>"/""/|sed s/"\""/""/g` 
urn=`echo $ntriple | grep -o "http://geni-orca.renci.org/owl/topology.owl#hasURN[^\.]*"|sed s/"http.*>"/""/|sed s/"\""/""/g| sed s/"\^\^.*"/""/`
uri=http://$host/resource/${i}

echo "| $aleph | $urn | $uri |"

done

}


function listCatalogIds ()
{
type=$1
server=$2

pidlist=`pidlist $type $server repo 0 10000`
for i in $pidlist;do TT=`curl -s http://${server}/fedora/objects/$i/datastreams/DC/content|grep -o "HT[^<]*\|TT[^<]*"`; echo $i,$TT;done >tmp

sort tmp |uniq
rm tmp
}


function pid2urn()
{
type=$1
host=$2

generateIdTable $type $host > idTable.txt
while read line
do 
#echo $line
urn=`echo $line|grep -o "urn[^\ ]*"`
pid=`echo $line|grep -o "\(edoweb:[0-9][0-9][0-9][0-9][0-9][0-9][0-9]\)\ \|$"`

echo $pid , $urn
done < idTable.txt | sort 

}


function testUrn()
{

type=$1
host=$2

pid2urn $type $host >pid2urn.sorted.txt
while read line
do
pid=`echo $line|grep -o -m1 "^[^,]*"`
urn=`echo $line|grep -o -m1 "urn:nbn:de:hbz:929.*$"`
cout=`curl -s -I http://nbn-resolving.org/$urn`
out=`echo $cout | grep -o "HTTP........"`
test=`echo $cout |grep -o "307\|200"`
if [ $? -eq 0 ]
then
echo "http://$host/resource/$pid , http://nbn-resolving.org/$urn , $out , Success"
else
echo "http://$host/resource/$pid , http://nbn-resolving.org/$urn , $out , ERROR"
fi
done <pid2urn.sorted.txt
}

function testOai()
{
type=$1
host=$2

pid2urn $type $host >pid2urn.sorted.txt
while read line
do
pid=`echo $line|grep -o -m1 "^[^,]*"`
cout=`curl -s -i "http://api.edoweb-rlp.de/dnb-urn/?verb=GetRecord&metadataPrefix=oai_dc&identifier=http://api.edoweb-rlp.de/resource/$pid"`
out=`echo $cout | grep -o "code=.............."`
test=`echo $cout |grep -o "dc:identifier"`
if [ $? -eq 0 ]
then
echo "http://$host/resource/$pid , http://api.$host/dnb-urn/?verb=GetRecord&metadataPrefix=oai_dc&identifier=http://api.$host/resource/$pid , $out , Success"
else
echo "http://$host/resource/$pid , http://api.$host/dnb-urn/?verb=GetRecord&metadataPrefix=oai_dc&identifier=http://api.$host/resource/$pid , $out , ERROR"
fi
done <pid2urn.sorted.txt
}