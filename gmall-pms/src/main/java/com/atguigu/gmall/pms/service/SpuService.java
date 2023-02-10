package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * spu信息
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2021-09-28 16:01:55
 */
public interface SpuService extends IService<SpuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 按照分类id分页查询商品列表
     * @param paramVo 带条件查询分页工具类
     * @param cid
     * @return
     */
    PageResultVo querySpuByCidAndIdOrName(PageParamVo paramVo, Long cid);

    /**
     * 保存之大保存
     * @param spu
     */
    void bigSava(SpuVo spu);
}

