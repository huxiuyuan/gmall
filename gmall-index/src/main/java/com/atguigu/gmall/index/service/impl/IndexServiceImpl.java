package com.atguigu.gmall.index.service.impl;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.feignclient.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：谷粒商城首页服务实现层
 * @date 2024/7/7 19:24
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    /**
     * 查询所有一级分类
     *
     * @return 一级分类
     */
    @Override
    public List<CategoryEntity> queryLv1Categories() {
        ResponseVo<List<CategoryEntity>> categoryResponseVo = this.gmallPmsClient.queryCategoryByParentId(0L);
        return categoryResponseVo.getData();
    }

    /**
     * 根据一级分类id查询二、三级分类
     *
     * @param pid 一级分类id
     * @return 二、三级分类
     */
    @Override
    public List<CategoryEntity> queryLv23CategoriesByPid(Long pid) {
        ResponseVo<List<CategoryEntity>> listResponseVo = this.gmallPmsClient.queryCategoriesWithSubsByPid(pid);
        return listResponseVo.getData();
    }
}
