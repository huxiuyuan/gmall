[TOC]

# ab的简介

> ab是apachebench命令的缩写。ab是apache自带的压力测试工具。ab非常实用，它不仅可以对apache服务器进行网站访问压力测试，也可以对或其它类型的服务器进行压力测试。比如nginx、tomcat、IIS等。

# 安装

```shell
yum install -y httpd-tools
```

# 使用

```shell
ab  -n（一次发送的请求数）  -c（请求的并发数） 访问路径

例：ab -n 5000 -c 100 http://192.168.247.1:8888/index/test_local_lock
```

