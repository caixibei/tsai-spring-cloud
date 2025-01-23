# 使用 openjdk 运行时作为基础镜像，版本 8
FROM openjdk:8

# 指定维护人员信息
MAINTAINER caixibei@139.com

# 设置环境信息
ARG VERSION
ENV VERSION=${VERSION}
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV TZ=Asia/Shanghai
ENV LANG C.UTF-8

# 设置工作目录为 /app
WORKDIR /oss

# 创建时区符号链接
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 将当前目录下的 jar 包 文件添加到容器的 /app 目录下
COPY ./spring-cloud-oss/target/spring-cloud-oss-${VERSION}.jar /oss

# 容器启动时命令
ENTRYPOINT ["sh", "-c","java $JAVA_OPTS -jar /oss/spring-cloud-oss-${VERSION}.jar"]
