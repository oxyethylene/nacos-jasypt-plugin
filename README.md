# Nacos Database password jasypt encrypt plugin

通过这个nacos插件，支持通过jasypt对nacos的数据库密码进行加密

## 使用方法

1. 下载jar包
2. 将jar包放到nacos的plugin目录下
3. 修改nacos的conf/application.properties文件，增加如下配置

```properties
nacos.custom.environment.enabled=true
jasypt.encryptor.password=password
jasypt.encryptor.algorithm=PBEWithMD5AndDES
jasypt.encryptor.iv-generator-classname=org.jasypt.iv.NoIvGenerator
```

在nacos的数据库密码配置`db.password.0`的值使用`ENC()`包裹时

```properties
db.password.0=ENC(encrypted_password)
```

插件会使用jasypt解密用`ENC()`包裹的字符串
