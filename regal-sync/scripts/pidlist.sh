#! /bin/bash
function pidlist()
{
type=$1
server=$2
fromSrc=$3
curl -XGET "http://$server/resource?type=$type&getListingFrom=$fromSrc" 2> /dev/null| sed s/"{\"list\":"/""/g | sed s/"\,"/"\n"/g | sed s/"\"\([^\"]*\)".*/"\1"/g | sed s/"^\["/""/g
echo
}

