
#如何统计所有日志文件（tomcat的access log）中，列出访问次数最多的10个IP，以及对应的次数。
 awk '{print $1}' ~/programs/apache-tomcat-9.0.0.M6/logs/localhost_access_log.2016-06-12.txt |sort|uniq -c|sort -nr|head -n 10

