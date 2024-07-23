package com.atguigu.gmall.auth.feignclient;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author huXiuYuan
 * @Description：ums远程调用
 * @date 2024/7/23 21:35
 */
@FeignClient("ums-service")
public interface GmallUmsClient extends GmallUmsApi {
}
