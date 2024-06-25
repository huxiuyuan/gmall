package com.atguigu.gmall.wms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author huXiuYuan
 * @email a811437621@gmail.com
 * @date 2022/2/26 16:58
 */
public interface GmallWmsApi {

    /**
     * 搜索服务数据导入第三步：根据skuId查询库存
     *
     * @param sid
     * @return ResponseVo<List < WareSkuEntity>>
     */
    @GetMapping("wms/waresku/sku/{skuId}")
    ResponseVo<List<WareSkuEntity>> queryWareSkuBySkuId(@PathVariable("skuId") Long sid);
}
