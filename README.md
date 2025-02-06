# Tsai Spring Cloud

## 一、相关组件版本

### 基础组件

- JDK 1.8
- Spring Boot 2.3.12.RELEASE
- Spring Cloud Hoxton.SR12
- Spring Cloud Alibaba 2.1.2.RELEASE

### 编排版本

- Docker 27.4.0
- Docker Compose 2.31.0
- CentOS 7.8

### 其他组件

- Jasypt 1.9.3

1. 添加镜像代理

```bash
echo '{"dns":["8.8.8.8","8.8.4.4"],"registry-mirrors":["https://docker.xuanyuan.me","https://docker.1ms.run","https://hub.rat.dev"]}' > /etc/docker/daemon.json
sudo systemctl daemon-reload
sudo systemctl restart docker
```

2. Docker Compose 安装

```bash
sudo curl -L "https://github.com/docker/compose/releases/download/v2.31.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
docker-compose --version
```

3.开启交换区，扩大运存（不推荐，速度会比较慢，有条件的购买更大的服务器）

```bash
sudo dd if=/dev/zero of=swapfile bs=1G count=3
sudo mkswap swapfile
sudo chmod 600 swapfile
sudo swapon swapfile
```

## 二、其他

> 🔈加密相关 `password` 、 `algorithm` 请前往配置文件 `application.yml` 中查找。

1. 数据库账户加密命令

明文（输入）：root

```shell
java -cp ./jasypt-1.9.3.jar ^
org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI ^
input="root" ^
password=DdNwDFt2D5v5OVstBTr4h565ZRGVnSO7 ^
algorithm=PBEWithMD5AndDES
```

密文（输出）：QkI0VPRiOCJR8EbbhdlIog==

2. 数据库链接地址加密命令

明文（输入）：jdbc:mysql://116.205.112.132:3306/tsai-db?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true

```shell
java -cp ./jasypt-1.9.3.jar ^
org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI ^
input="jdbc:mysql://116.205.112.132:3306/tsai-db?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true" ^
password=DdNwDFt2D5v5OVstBTr4h565ZRGVnSO7 algorithm=PBEWithMD5AndDES
```

密文（输出）：/5xJ0XvV4wg87/M+IHPP6B/qu91ZLspRWzrYdDmz6HpKLzk6Kh5gUDcz/bjbdul5tiUjNLn8NE1lhOVc6UAHlEG6GY8UU8HflSRACjzU3aKy3R2EEZCKAaRN2nlzLaBg0awR2J4D2lbXLqd0lhvWFeIQv8YWy6B61YeDNLHZvz8781bsMSfl4sZk0UtIY3UgTnx2FG5tzfGzr1b8+0PDkbo4C99/tkic+RlRIE8seRCi6k/UEYkZH2GksvC6oQWX+kppBjKBYwOWkp4EvFeqPTFm3PT+rbYB

3. 数据库口令加密命令：

明文（输入）：NJI(mko0

```shell
java -cp ./jasypt-1.9.3.jar ^
org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI ^
input="NJI(mko0" ^
password=DdNwDFt2D5v5OVstBTr4h565ZRGVnSO7 ^
algorithm=PBEWithMD5AndDES
```

密文（输出）：0iRXcamB2YeKTq5XjQTsas1WC0K2SWx4

4. druid账户加密命令

明文（输入）：admin

```shell
java -cp ./jasypt-1.9.3.jar ^
org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI ^
input="admin" ^
password=DdNwDFt2D5v5OVstBTr4h565ZRGVnSO7 ^
algorithm=PBEWithMD5AndDES
```

密文（输出）：LJJZq5/m+BsKZm753vxChQ==

5. druid口令加密命令

明文（输入）：123456

```shell
java -cp ./jasypt-1.9.3.jar ^
org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI ^
input="123456" ^
password=DdNwDFt2D5v5OVstBTr4h565ZRGVnSO7 ^
algorithm=PBEWithMD5AndDES
```

密文（输出）：8Wa/OwMogrS2z1vknfN3Fg==

6. jwt.jks 证书生成

```shell
# 删除
keytool -delete -alias jwt -keystore jwt.jks
# 生成
keytool -genkey -alias jwt ^
-keyalg RSA ^
-keypass DdNwDFt2D5v5OVstBTr4h565ZRGVnSO7 ^
-keystore jwt.jks ^
-storepass 123456
```

7. 基础建表语句

```sql
-- 创建数据库
create database `tsai-db`;
-- 指定数据库
use `tsai-db`;
-- 创建 Oauth2 认证表
CREATE TABLE `oauth_client_details` (
    `client_id`               varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL,
    `resource_ids`            varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    `client_secret`           varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    `scope`                   varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    `authorized_grant_types`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    `web_server_redirect_uri` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    `authorities`             varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    `access_token_validity`   int(11)                                                  NULL DEFAULT NULL,
    `refresh_token_validity`  int(11)                                                  NULL DEFAULT NULL,
    `additional_information`  varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `autoapprove`             varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
-- 接入应用信息
INSERT INTO `oauth_client_details` VALUES ('spring-cloud-system', 'spring-cloud-system', '$2a$10$mcEwJ8qqhk2DYIle6VfhEOZHRdDbCSizAQbIwBR7tTuv9Q7Fca9Gi', 'all', 'password,refresh_token,authorization_code', 'http://localhost:7080/login',NULL,300,3600,NULL,'all');
-- 创建用户信息表
create table `TSAI_USER`(
    ID          varchar(128) not null primary key,
    USERNAME    varchar(128) not null unique,
    PASSWORD    varchar(256) not null,
    CREATE_TIME TIMESTAMP    null,
    UPDATE_TIME TIMESTAMP    null
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
-- 用户信息数据
INSERT INTO `TSAI_USER` VALUES ('001', 'admin', '$2a$10$mcEwJ8qqhk2DYIle6VfhEOZHRdDbCSizAQbIwBR7tTuv9Q7Fca9Gi', NULL, NULL);
```