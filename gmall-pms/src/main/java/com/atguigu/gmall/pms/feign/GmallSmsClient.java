package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.vo.SkuSaleVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Title GmallSmsClient
 * @Author HuXiuYuan
 * @Date 2021/11/21 4:10
 */
@FeignClient("sms-service")
public interface GmallSmsClient {

    @LoadBalanced
    @PostMapping("sms/skubounds/sales/save")
    @ApiOperation("大保存之sku营销信息")
    ResponseVo savaSales(@RequestBody SkuSaleVo saleVo);
}
