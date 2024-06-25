package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.service.AttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrMapper, AttrEntity> implements AttrService {

    @Autowired
    private AttrMapper attrMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 属性维护 - 属性分组 - 维护属性
     *
     * @param gid
     * @return ResponseVo<List < AttrEntity>>
     */
    @Override
    public List<AttrEntity> queryAttrsByGid(Long gid) {
        QueryWrapper<AttrEntity> query = new QueryWrapper<>();
        query.eq("group_id", gid);
        return attrMapper.selectList(query);
    }

    /**
     * 查询分类下的规格参数
     *
     * @param cid
     * @param type
     * @param searchType
     * @return ResponseVo<List < AttrEntity>>
     */
    @Override
    public List<AttrEntity> queryAttrsByCIdOrTypeOrSearchType(Long cid, Integer type, Integer searchType) {
        QueryWrapper<AttrEntity> query = new QueryWrapper<>();
        query.eq("category_id", cid);
        // 判断type是否为空 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
        if (type != null) {
            query.eq("type", type);
        }
        // 判断searchType是否为空 是否需要检索[0-不需要，1-需要]
        if (searchType != null) {
            query.eq("search_type", searchType);
        }
        return attrMapper.selectList(query);
    }

}