#!/bin/bash
JAVA_HOME=/usr/local/java/jdk1.8.0_291
CLASSPATH=$JAVA_HOME/lib/
PATH=$PATH:$JAVA_HOME/bin
JRE_HOME=$JAVA_HOME/jre
export PATH JAVA_HOME CLASSPATH JRE_HOME
export MAVEN_HOME=/opt/apache-maven-3.3.9
export PATH=$MAVEN_HOME/bin:$PATH
appName(){
    param=$1
    v=${param##*/}
    result=${v%.*}
    echo $result
}

url=$1
port=$2
if [ -z $url ]; then
	echo "remoteUrl is null!"
	exit
fi
#根据远程地址来构建目录
echo "拉取的远程地址是  $url"
appName=$(appName $url)
echo "项目名称是 $appName"
echo "项目运行的端口是 $port"

PROFILE="/application/$appName"
CODEFILE="/application/$appName/code"
#第一次发布创建文件，否则清除构建文件
if [ -d "$PROFILE/jar" ]; then
	rm -rf "$PROFILE/jar"
	echo "清除上一次构建文件"   
fi
mkdir -p "$PROFILE/jar"
mkdir -p  $CODEFILE

cd $CODEFILE
echo "开始拉取代码...."
git init;
git clone $url >> /dev/null
if [ -d $CODEFILE/$appName ]; then
	echo "拉取成功"
    else 
	echo "代码拉取失败"
	exit
fi

cd $appName
if [ $isConfigFile = true ];then
  echo "开始添加配置文件"
  for file in `ls $PROFILE/`
		do
   			if [[ $file =~ \.yml$ || $file =~ \.properties$ ]];then
     				mv "$PROFILE/$file" "$CODEFILE/$appName/"
  			fi
  done
  for file in `ls $CODEFILE/$appName/`
		do
   			if [[ $file =~ \.yml$ || $file =~ \.properties$ ]];then
     				  echo "配置文件添加成功"
     				else
     				  echo "配置文件添加失败,无法进行打包"
     				  exit
  			fi
  done
fi
echo "正在打包代码......"
mvn clean>/dev/null
mvn package>/dev/null
if [ -d $CODEFILE/$appName/target ]; then 
	echo "打包成功"
	echo "检查端口....."
	pid= $(netstat -nlp | grep :$port | awk '{print $7}' | awk -F"/" '{print $1}');
	echo "pid:$pid"
	if [ -n $pid ]; then 
		echo "杀死进程 $pid"
		kill -9 $pid
	fi
	echo "复制最新的jar到指定目录...."
	for file in `ls $CODEFILE/$appName/target/ `
		do
   			if [[ $file =~ \.jar$ ]];then 
     				mv "$CODEFILE/$appName/target/$file" "$PROFILE/jar"
  			 fi
	done
	cd "$PROFILE/jar"
	ls
	nohup java -jar *.jar  &
	echo "项目启动...."
	rm -rf $CODEFILE

else
	echo "打包失败"
	rm -rf $CODEFILE
	exit
fi
