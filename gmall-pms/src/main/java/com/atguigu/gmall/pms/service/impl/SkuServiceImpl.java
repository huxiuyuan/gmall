package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("skuService")
public class SkuServiceImpl extends ServiceImpl<SkuMapper, SkuEntity> implements SkuService {

    @Autowired
    private SkuMapper skuMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 库存管理 - 商品库存 - 库存维护
     *
     * @param sid
     * @return ResponseVo<List < SkuEntity>>
     */
    @Override
    public List<SkuEntity> querySkusBySpuId(Long sid) {
        QueryWrapper<SkuEntity> query = new QueryWrapper<>();
        query.eq("spu_id", sid);
        return skuMapper.selectList(query);
    }

}