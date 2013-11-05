#! /bin/bash

pidfile=$1
server=$2
snid=$3
passwd=$4

for i in `cat $pidfile`
do
namespace=`echo $i|grep -o "^[^:]*"`
id=`echo $i|grep -o "[0-9]*$"`

echo "Add URN for id=$id,namespace=$namespace,snid=$snid"
echo
curl -uadmin:$passwd -XPOST "$server/utils/addUrn?id=$id&namespace=$namespace&snid=$snid"
echo
echo
done