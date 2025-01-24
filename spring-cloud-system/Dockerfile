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
WORKDIR /system
# 设置时区链接
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 将 jar 源文件拷贝到容器目标路径下
COPY spring-cloud-eureka/target/spring-cloud-system-${VERSION}.jar /system
# 启动
ENTRYPOINT ["sh", "-c","java $JAVA_OPTS -jar /system/spring-cloud-system-${VERSION}.jar"]