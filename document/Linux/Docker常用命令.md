[TOC]



# Docker常用命令

- 开机自启：sudo systemctl enable docker

- 镜像

  - 拉取：docker pull 镜像名:版本号
  - 查看：docker images
  - 删除：docker rmi 镜像名:版本号/镜像ID

- 容器

  - 初始化

    ```shell
    docker run --restart=always -d -p 8080:8080 镜像名:版本号 --name 容器名称
    --restart=always: 随着docker启动而启动
    -d: 后台运行
    -p: 容器端口号映射虚拟机端口号
    -v: 数据卷挂载,- /home/redis/myredis/myredis.conf:/etc/redis/redis.conf 是将 liunx 路径下的myredis.conf 和redis下的redis.conf 挂载在一起
    ```

  - 启动：docker start 容器名称/容器ID

  - 重启：docker restart 容器名称/容器ID

  - 停止：docker stop 容器名称/容器ID

  - 查看容器日志：docker logs 容器名称/容器ID

  - 删除：docker rm (-f 强制删除) 容器名称/容器ID

  - 进入容器：docker exec -it 容器名称/容器ID bash

  - 容器随着docke启动而启动：docker container update --restart=always 容器名称/容器ID