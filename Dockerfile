FROM ubuntu18/jdk8-tesseract:19.7.3
VOLUME /tmp
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD target/oalert-0.0.1-SNAPSHOT.jar oalert.jar
ENTRYPOINT ["java","-jar","/oalert.jar"]