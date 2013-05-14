
downloadDir=$1;

for i in `ls $downloadDir`
do 
file=$downloadDir/${i}/.${i}_DC.xml; 
htnummer=`grep -o "dc:alephsyncid>HT[^<]*" $file|sed s"/.*\(HT.*\)/\1/"`; 
type=`grep -o "10.4126[^<]*" $file`; 
pid=`echo $i|grep -o "[0-9]*"`; 
echo $htnummer , $pid , $type; 

done  > ellinetDOI.txt

#grep -v "|" ellinetTypes.txt > keinTyp.txt
#grep -v "HT" ellinetTypes.txt > keineHt.txt
#
#for i in `grep -o "[0-9]*" keineHt.txt`;do echo ===================; echo $i ; cat all/$i/${i}_DC.xml; echo -e "\n========================" ;done >keineHtDetails.txt 2>&1