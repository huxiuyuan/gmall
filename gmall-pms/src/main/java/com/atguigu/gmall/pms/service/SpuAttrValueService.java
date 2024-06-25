package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * spu属性值
 *
 * @author huxiuyuan
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:23:24
 */
public interface SpuAttrValueService extends IService<SpuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 数据导入第五步：根据 cid 和 spuId 查询检索类型规格参数及值
     *
     * @param cid
     * @param spuId
     * @return List<SpuAttrValueEntity>
     */
    List<SpuAttrValueEntity> querySearchAttrValueByCidAndSpuId(Long cid, Long spuId);
}

