package com.atguigu.gmall.item.feignclient;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author huXiuYuan
 * @Description：pms服务远程调用
 * @date 2024/7/16 20:32
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
