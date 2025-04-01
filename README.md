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

明文（输入）：jdbc:mysql://116.205.112.132:33016/tsai-db?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true

```shell
java -cp ./jasypt-1.9.3.jar ^
org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI ^
input="jdbc:mysql://116.205.112.132:33016/tsai-db?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true" ^
password=DdNwDFt2D5v5OVstBTr4h565ZRGVnSO7 algorithm=PBEWithMD5AndDES
```

密文（输出）：JUPM62+FP6fL27IHYJGmdQ/UeyezFBDPPuy37ySgVe7IVZOgnxAOMW85Q2/kiaQnTgXGEuku7Jbkg1YgWbRJGpDbB3hVeYBGCJSkxDRIyxhZGbKu7CfE5UDz6y90gaJHsjbcyHwcFJ9nDHq/KFOi1tCpb6fcvrs2P/bfSffKblGkqI88/0/+RO1pbYt1njTzVusaRGuMnfL8izVU87OORUuSJut9yWWY7fi6qnCR8MVbPHexn/aVFR0le5JGoVMTDAGRNolisDNS8ELYTmFDVtE6VllTHnn4

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
-- 重建数据库
drop database if exists `tsai-db`;

-- 创建数据库
create database `tsai-db` character set utf8mb4;

-- 指定数据库
use `tsai-db`;

-- 创建 Oauth2 认证表
create table oauth_client_details (
      client_id                varchar(256) character set utf8 collate utf8_general_ci  not null,
      resource_ids             varchar(256) character set utf8 collate utf8_general_ci  null default null,
      client_secret            varchar(256) character set utf8 collate utf8_general_ci  null default null,
      scope                    varchar(256) character set utf8 collate utf8_general_ci  null default null,
      authorized_grant_types   varchar(256) character set utf8 collate utf8_general_ci  null default null,
      web_server_redirect_uri  varchar(256) character set utf8 collate utf8_general_ci  null default null,
      authorities              varchar(256) character set utf8 collate utf8_general_ci  null default null,
      access_token_validity    int(11)                                                  null default null,
      refresh_token_validity   int(11)                                                  null default null,
      additional_information   varchar(4096) character set utf8 collate utf8_general_ci null default null,
      autoapprove              varchar(256) character set utf8 collate utf8_general_ci  null default null,
      primary key (client_id) using btree
) engine = innodb character set = utf8mb4 collate = utf8mb4_general_ci row_format = dynamic;

-- 接入应用信息
insert into oauth_client_details (client_id,
                                resource_ids,
                                client_secret,
                                scope,
                                authorized_grant_types,
                                web_server_redirect_uri,
                                authorities,
                                access_token_validity,
                                refresh_token_validity,
                                additional_information,
                                autoapprove)
values ('SPRING-CLOUD-SYSTEM', 'SPRING-CLOUD-SYSTEM', '$2a$10$mcEwJ8qqhk2DYIle6VfhEOZHRdDbCSizAQbIwBR7tTuv9Q7Fca9Gi', 'all', 'password,refresh_token,authorization_code', null, null, 120, 240, null, null);

-- 授权码记录信息
create table oauth_code (
    code           varchar(256) default null,
    authentication blob
) engine=innodb default charset=utf8;

-- 创建用户信息表
create table tsai_user(
    id          varchar(128) not null primary key,
    username    varchar(128) not null,
    password    varchar(256) not null,
    create_time timestamp    null,
    update_time timestamp    null,
    constraint username unique (username)
) engine = innodb character set = utf8mb4 collate = utf8mb4_general_ci row_format = dynamic;

-- 用户信息数据
insert into `tsai_user` values ('001', 'admin', '$2a$10$mcEwJ8qqhk2DYIle6VfhEOZHRdDbCSizAQbIwBR7tTuv9Q7Fca9Gi', NULL, NULL);
```