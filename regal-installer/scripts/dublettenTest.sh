zbmed=$1;

for i in `ls $zbmed`;
do 
echo $i -------; 
grep -o "<dc:alephsyncid>HT.*</dc:alephsyncid>" $zbmed/${i}/.${i}_DC.xml; 
done |grep -o "HT[^<]*" >alleHTNummerInDTL.log

sort alleHTNummerInDTL.log > sort.ls
sort -u alleHTNummerInDTL.log > uniq.ls
echo Problemfälle:
echo -----------------------------
diff sort.ls uniq.ls |grep -o "HT.*"
echo
echo Fundorte:
echo -----------------------------
for i in `diff sort.ls uniq.ls |grep -o "HT.*"`;
do 
echo $i; 
find $zbmed -name "*_DC.xml" -print | xargs grep -iRHo "<dc:alephsyncid>$i</dc:alephsyncid>" ;
done