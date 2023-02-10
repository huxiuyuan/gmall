package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.service.AttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrMapper, AttrEntity> implements AttrService {

    @Resource
    private AttrMapper attrMapper;

    /**
     * 查询分类下的规格参数 一个分类下有许多规格大分组
     * @param cid
     * @param type
     * @param searchType
     * @return List<AttrEntity>
     */
    @Override
    public List<AttrEntity> queryAttrByCidOrTypeOrSearchType(Long cid, Integer type, Integer searchType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id",cid);
        // 判断type是否为空
        if (type != null){
            queryWrapper.eq("type",type);
        }
        // 判断searchType是否为空
        if (searchType != null){
            queryWrapper.eq("search_type",searchType);
        }
        return attrMapper.selectList(queryWrapper);
    }

    /**
     * 分组下的规格参数查询 大分组下的小规格参数
     * @param gid group_id
     * @return List<AttrEntity>
     */
    @Override
    public List<AttrEntity> queryAttrListByGid(Long gid) {
        QueryWrapper<AttrEntity> query = new QueryWrapper<>();
        query.eq("group_id",gid);

        return attrMapper.selectList(query);
    }

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrEntity>()
        );

        return new PageResultVo(page);
    }

}