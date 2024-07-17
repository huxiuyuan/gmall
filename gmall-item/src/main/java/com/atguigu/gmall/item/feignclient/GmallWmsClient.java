package com.atguigu.gmall.item.feignclient;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author huXiuYuan
 * @Description：wms远程接口调用
 * @date 2024/7/16 21:14
 */
@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {
}
