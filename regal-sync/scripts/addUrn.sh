#! /bin/bash

server=$2
snid=$3
passwd=$4

./pidreporter.sh regal-epicur.properties
./pidreporter.sh regal-oai_dc.properties

for i in `cat oaidcpids.txt`
do 
a=`grep $i epicurpids.txt`; 
if [ $? -eq 0 ] 
then  
echo "$i success" 
else 
echo "$i fail"
fi 
done | grep fail |grep -o "edoweb:[^\ ]*" > pidswithnourn.txt

pidfile=pidswithnourn.txt

count=0
for i in `cat $pidfile`
do
count=`expr $count + 1`
echo $count
namespace=`echo $i|grep -o "^[^:]*"`
id=`echo $i|grep -o "[0-9]*$"`

echo "Add URN for id=$id,namespace=$namespace,snid=$snid"
echo
curl -uadmin:$passwd -XPOST "$server/utils/addUrn?id=$id&namespace=$namespace&snid=$snid"
echo
echo
if [ $count -eq 100 ]
then
echo sleep 5 sec for proai
sleep 5
count=0;
fi
done