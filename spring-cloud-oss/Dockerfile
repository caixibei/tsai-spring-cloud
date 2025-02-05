# 使用 openjdk:8 作为基础构建镜像
FROM openjdk:8
# 声明维护人员信息
MAINTAINER caixibei@139.com
# 声明版本号
ARG VERSION
ENV VERSION=${VERSION}
# 环境参数
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV TZ=Asia/Shanghai
ENV LANG C.UTF-8
# 工作目录
WORKDIR /oss
# 设置时区链接
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 将 jar 源文件拷贝到容器目标路径下
# COPY spring-cloud-eureka/target/spring-cloud-oss-${VERSION}.jar /oss
COPY ./spring-cloud-eureka/ /oss
# 列出目标目录内容，目前遇到的问题就是 window 本地部署 COPY 能够正确找到 target 下
# 的 jar 文件并添加到目标目录，但是使用 Idea 远程部署会出问题，原因不祥
RUN ls -l /oss
# 启动
ENTRYPOINT ["sh", "-c","java $JAVA_OPTS -jar /oss/spring-cloud-oss-${VERSION}.jar"]