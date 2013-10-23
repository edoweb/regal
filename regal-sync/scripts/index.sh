#! /bin/bash

source pidlist.sh

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


index $1 $2 $3 $4
