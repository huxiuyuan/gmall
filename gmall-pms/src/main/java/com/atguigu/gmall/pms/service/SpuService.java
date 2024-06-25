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
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:23:24
 */
public interface SpuService extends IService<SpuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 商品列表 - spu查询按钮
     *
     * @param cid
     * @param paramVo
     * @return ResponseVo<PageResultVo>
     */
    PageResultVo queryCategorysByCid(Long cid, PageParamVo paramVo);

    /**
     * spu新增之大保存
     *
     * @param spu
     * @return ResponseVo<Object>
     */
    void bigSave(SpuVo spu);
}

