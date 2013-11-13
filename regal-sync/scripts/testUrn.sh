#! /bin/bash

http://nbn-resolving.org/


for i in `grep urn:nbn:de:hbz:929 pid2urn.sorted.txt`
do
pid=`echo $i|grep -o -m1 "^[^,]*"`
urn=`echo $i|grep -o -m1 "urn:nbn:de:hbz:929[^,]*"`
#curl -s -i http://nbn-resolving.org/$urn
cout=`curl -s -i http://nbn-resolving.org/$urn |grep "307"`

if [ $? -eq 0 ]
then
echo "http://nbn-resolving.org/$urn Success"
else
echo "http://nbn-resolving.org/$urn ERROR"
fi
done
