package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：谷粒商城首页服务接口层
 * @date 2024/7/7 19:23
 */
public interface IndexService {

    /**
     * 查询所有一级分类
     *
     * @return 一级分类
     */
    List<CategoryEntity> queryLv1Categories();

    /**
     * 根据一级分类id查询二、三级分类
     *
     * @param pid 一级分类id
     * @return 二、三级分类
     */
    List<CategoryEntity> queryLv23CategoriesByPid(Long pid);

    /**
     * 本地锁测试
     *
     * @return
     */
    void testLocalLock();

    /**
     * 分布式锁(基于redis)测试 版本1
     *
     * @return
     */
    void testDistributedLock1();

    /**
     * 分布式锁(基于redis)测试 版本2
     *
     * @return
     */
    void testDistributedLock2();

    /**
     * 分布式锁(基于redis) 版本3
     *
     * @return
     */
    void testDistributedLock3();

    /**
     * 分布式锁(基于redis) 版本4
     *
     * @return
     */
    void testDistributedLock4();

    /**
     * 分布式锁(基于redis) 版本5
     *
     * @return
     */
    void testDistributedLock5();

    /**
     * 分布式锁(基于redis) 版本6
     *
     * @return
     */
    void testDistributedLock6();
}
