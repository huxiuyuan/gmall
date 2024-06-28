package com.atguigu.gmall.pms.feignclient;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.api.GmallSmsApi;
import com.atguigu.gmall.sms.vo.SkuSalesVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author huXiuYuan
 * @Description：sms模块远程调用
 * @date 2023/4/1 17:42
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {

    /**
     * 保存sku营销信息
     *
     * @param skuSaleVo sku营销信息
     * @return
     */
    @Override
    @PostMapping("/sms/skubounds/saveSkuSales")
    ResponseVo<Void> saveSkuSales(@RequestBody SkuSalesVo skuSaleVo);
}
