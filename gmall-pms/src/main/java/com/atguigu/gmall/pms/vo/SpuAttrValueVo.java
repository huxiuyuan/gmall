package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Title SpuAttrValueVo
 * @Author HuXiuYuan
 * @Date 2021/11/20 23:23
 */
public class SpuAttrValueVo extends SpuAttrValueEntity {

    /**
     * 前端传过来的字段是valueSelected，其实就是SpuAttrValueEntity中的setAttrValue
     * 由于这个属性是多选框，所以接收的时候是一个集合
     * 我们要把接收的 List 转为 String数组 赋值给 SpuAttrValueEntity 中的 attrValue
     * @param valueSelected
     */
    public void setValueSelected(List<String> valueSelected) {
        if (CollectionUtils.isEmpty(valueSelected)){
            return;
        }
        this.setAttrValue(StringUtils.join(valueSelected, ","));
    }
}
