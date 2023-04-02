package com.atguigu.gmall.pms.feignclient;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author huXiuYuan
 * @Description：sms模块远程调用
 * @date 2023/4/1 17:42
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
