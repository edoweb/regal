#!/bin/bash

server=$1
pages=$2

i=0; 
while [ $i -lt $pages ] 
do 

i=`expr $i + 1`;
echo $i; 
curl -s http://$server/resource?page=$i;

done