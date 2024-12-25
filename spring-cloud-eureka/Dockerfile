# 基于java镜像创建新镜像
FROM openjdk:8
# 作者
MAINTAINER caixibei
# 将jar包添加到容器中并更名为 mvn 打包后的jar包名
# 在target目录下找得到
ADD  target/spring-cloud-eureka-1.1.0.jar spring-cloud-eureka-1.1.0.jar
# 运行jar包
ENTRYPOINT ["java","-jar","/spring-cloud-eureka-1.1.0.jar","&"]
EXPOSE 8761