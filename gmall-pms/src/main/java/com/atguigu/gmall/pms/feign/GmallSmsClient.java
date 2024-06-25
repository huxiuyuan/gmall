package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author huXiuYuan
 * @email a811437621@gmail.com
 * @date 2022/1/1 20:57
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
