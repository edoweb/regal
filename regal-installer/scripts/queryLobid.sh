
for i in `cat $1`
do

httpstatus=`curl -H "Accept: text/turtle" -i -L http://lobid.org/resource/ | grep -o "HTTP/1.1....";`

echo $i $httpstatus;


done
