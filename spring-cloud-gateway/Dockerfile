# 使用 openjdk 运行时作为基础镜像，版本 8
FROM openjdk:8

# 指定维护人员信息
MAINTAINER caixibei@139.com

# 设置环境信息
# TZ：设置时区为亚洲/上海
# LANG：设置语言环境为 UTF-8
# JAVA_OPTS：设置 JVM 启动参数，包括最小堆内存 128m，最大堆内存为 256m，以及使用 /dev/urandom 作为熵源：
# ENV JAVA_OPTS="-Xms128m -Xmx256m -Djava.security.egd=file:/dev/./urandom"
ARG VERSION
ENV VERSION=${VERSION}
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV TZ=Asia/Shanghai
ENV LANG C.UTF-8

# 设置工作目录为 /spring-cloud-gateway
WORKDIR /spring-cloud-gateway

# 创建符号链接将时区信息链接到 /etc/localtime，并写入时区信息到 /etc/timezone 文件
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 将当前目录下的 jar 包 文件添加到容器的 /spring-cloud-gateway 目录下
# COPY ./target/spring-cloud-gateway-${VERSION}.jar /spring-cloud-gateway
COPY . /spring-cloud-gateway

# 容器启动时命令，先休眠 30s ，然后使用指定的JVM参数运行 /tsai-spring-cloud/spring-cloud-gateway-**.jar
CMD ls -al /spring-cloud-gateway
ENTRYPOINT ["sh", "-c","java $JAVA_OPTS -jar /spring-cloud-gateway/spring-cloud-gateway-${VERSION}.jar"]
