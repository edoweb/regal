CATALINA_OPTS=" \
-Xms1536m \
-Xmx1536m \
-XX:NewSize=256m \
-XX:MaxNewSize=256m \
-XX:PermSize=256m \
-XX:MaxPermSize=256m \
-server \
-Djava.awt.headless=true \
-Dorg.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER=true"

export CATALINA_OPTS

