usr=$1;
pwd=$2;
downloadDir=$3;

echo "pid,aleph" > pid2ht.txt;

for i in `ls $downloadDir`
do 
file=$downloadDir/${i}/.${i}_MARC.xml; 
htnummer=`grep -m 1 -o "<controlfield\ tag=\"001\">HT[^<]*<\|<controlfield\ tag=\"001\">TT[^<]*<" $file|sed s"/.*>\(.*\)</\1/"`; 
pid=`echo $i|grep -o "[0-9]*"`; 

curl -u $usr:$pwd -XPOST http://localhost/utils/lobidify/edoweb:$pid;echo;

echo $pid,$htnummer >> pid2ht.txt 

done 

