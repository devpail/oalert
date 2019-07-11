FROM centos7.6/openjdk8/tesseract3.04:1.0
# 中文乱码
# ENV LANG C.UTF-8
# 下面是一些创建者的基本信息
# MAINTAINER devpail (devpail@163.com)
# 安装openjdk
# RUN yum install -y java-1.8.0-openjdk
# VOLUME /tmp
# 更新安装源
# RUN apt-get update
# 更新时区
# RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
# RUN export DEBIAN_FRONTEND=noninteractive
# RUN apt-get install -y tzdata
# RUN dpkg-reconfigure --frontend noninteractive tzdata
# 安装图像识别软件
# RUN apt-get install -y tesseract-ocr
# RUN yum -y install epel-release
#RUN yum -y install tesseract
ADD target/oalert-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

#FROM ubuntu/jdk12/tesseract:1.0
## 中文乱码
#ENV LANG C.UTF-8
## 下面是一些创建者的基本信息
#MAINTAINER devpail (devpail@163.com)
#VOLUME /tmp
## 更新安装源
## RUN apt-get update
## 更新时区
#RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
#RUN export DEBIAN_FRONTEND=noninteractive
## RUN apt-get install -y tzdata
#RUN dpkg-reconfigure --frontend noninteractive tzdata
## 安装图像识别软件
## RUN apt-get install -y tesseract-ocr
#ADD target/oalert-0.0.1-SNAPSHOT.jar /app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]



#FROM ubuntu/jdk12:latest
## 下面是一些创建者的基本信息
#MAINTAINER devpail (devpail@163.com)
#VOLUME /tmp/oalert
## 更新安装源
#RUN apt-get update
## 更新时区
#RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
#RUN export DEBIAN_FRONTEND=noninteractive
#RUN apt-get install -y tzdata
#RUN dpkg-reconfigure --frontend noninteractive tzdata
## 安装图像识别软件
#RUN apt-get install -y tesseract-ocr
#ADD target/oalert-0.0.1-SNAPSHOT.jar /tmp/oalert/app.jar
#ENTRYPOINT ["java","-jar","/tmp/oalert/app.jar"]