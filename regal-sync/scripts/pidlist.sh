#! /bin/bash
<<<<<<< HEAD
=======

>>>>>>> bugfix_branch_0.1.3
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

