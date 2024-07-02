package com.atguigu.search.gmall.search.feignclient;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author huXiuYuan
 * @Description：wms服务远程接口
 * @date 2024/7/1 21:54
 */
@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {
}
