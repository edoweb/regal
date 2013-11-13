#! /bin/bash

function pidlist()
{
type=$1
server=$2
curl -XGET http://$server/$type/ 2> /dev/null| sed s/"\,"/"\n"/g | sed s/"\"\([^\"]*\)".*/"\1"/ | tail -n +2
echo
}

