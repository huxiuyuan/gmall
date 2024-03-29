package com.atguigu.gmall.wms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.mapper.WareSkuMapper;
import com.atguigu.gmall.wms.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuMapper, WareSkuEntity> implements WareSkuService {

    @Resource
    private WareSkuMapper wareSkuMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<WareSkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 获取某个sku的库存信息
     * @param sid skuId
     * @return List<WareSkuEntity>
     */
    @Override
    public List<WareSkuEntity> queryWareSkuBySkuId(Long sid) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("sku_id",sid);

        return wareSkuMapper.selectList(queryWrapper);
    }

}