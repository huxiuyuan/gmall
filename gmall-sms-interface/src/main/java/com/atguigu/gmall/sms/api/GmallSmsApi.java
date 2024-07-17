package com.atguigu.gmall.sms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.vo.SkuSalesVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：sms - sku营销信息远程调用接口
 * @date 2023/4/1 17:29
 */
public interface GmallSmsApi {

    /**
     * 保存sku营销信息
     *
     * @param skuSalesVo sku营销信息实体
     * @return
     */
    @PostMapping("/sms/skubounds/saveSkuSales")
    @ApiOperation("保存sku营销信息")
    ResponseVo<Void> saveSkuSales(@RequestBody SkuSalesVo skuSalesVo);

    /**
     * 根据skuId查询营销信息
     *
     * @param skuId
     * @return 营销信息
     */
    @GetMapping("/sms/skubounds/querySalesBySkuId/{skuId}")
    ResponseVo<List<ItemSaleVo>> querySalesBySkuId(@PathVariable("skuId") Long skuId);
}
