package com.atguigu.gmall.index.feignclient;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;


/**
 * @author huXiuYuan
 * @Description：pms服务远程接口
 * @date 2024/7/1 21:42
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
