#! /bin/bash

source pidlist.sh

function index()
{
type=$1
user=$2
password=$3
server=$4
for i in `pidlist $type $server repo`
do
curl -s -u ${user}:${password} -XPOST http://localhost/utils/lobidify/$i;echo
done
}


index $1 $2 $3 $4
