package com.atguigu.gmall.item.feignclient;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author huXiuYuan
 * @Description：sms远程接口调用
 * @date 2024/7/16 21:12
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
