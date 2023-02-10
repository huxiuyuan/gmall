package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.CommentEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品评价
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2021-09-28 16:01:55
 */
public interface CommentService extends IService<CommentEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

