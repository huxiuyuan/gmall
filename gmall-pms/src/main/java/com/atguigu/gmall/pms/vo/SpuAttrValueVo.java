package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：spu规格属性值扩展对象
 * @date 2023/3/26 23:12
 */
@Data
@ApiModel("spu规格属性值扩展对象")
public class SpuAttrValueVo extends SpuAttrValueEntity {

    /**
     * 调用attrValue属性的set方法将前端传回的字符串集合通过逗号拼接设置值
     *
     * @param valueSelected
     */
    public void setValueSelected(List<String> valueSelected) {
        if (CollectionUtils.isEmpty(valueSelected)) {
            return;
        }
        this.setAttrValue(StringUtils.join(valueSelected, ","));
    }
}
