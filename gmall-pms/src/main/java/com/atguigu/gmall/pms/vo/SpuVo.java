package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：spu扩展对象
 * @date 2023/3/25 16:31
 */
@Data
@ApiModel("spu扩展对象")
public class SpuVo extends SpuEntity {

    private List<String> spuImages;

    private List<SpuAttrValueVo> baseAttrs;

    private List<SkuVo> skus;
}
