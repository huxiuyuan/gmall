package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SpuDescEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * spu信息介绍
 *
 * @author huxiuyuan
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:23:24
 */
public interface SpuDescService extends IService<SpuDescEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

