package com.atguigu.gmall.index.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：谷粒商城首页服务控制层
 * @date 2024/7/7 19:22
 */
@Controller
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 首页加载
     *
     * @param model
     * @return
     */
    @GetMapping("/**")
    public String index(Model model) {
        List<CategoryEntity> categoryEntityList = this.indexService.queryLv1Categories();
        model.addAttribute("categories", categoryEntityList);
        return "index";
    }

    /**
     * 根据一级分类id获取二、三级分类
     *
     * @param pid 一级分类id
     * @return
     */
    @GetMapping("/index/cates/{pid}")
    @ResponseBody
    public ResponseVo<List<CategoryEntity>> queryLv23CategoriesByPid(@PathVariable("pid") Long pid) {
        return ResponseVo.ok(this.indexService.queryLv23CategoriesByPid(pid));
    }

    /**
     * 本地锁测试
     *
     * @return
     */
    @GetMapping("/index/test_local_lock")
    @ResponseBody
    public ResponseVo<Object> testLocalLock() {
        this.indexService.testLocalLock();
        return ResponseVo.ok(null);
    }

    /**
     * 分布式锁(基于redis) 版本1
     *
     * @return
     */
    @GetMapping("/index/test_distributed_lock1")
    @ResponseBody
    public ResponseVo<Object> testDistributedLock1() {
        this.indexService.testDistributedLock1();
        return ResponseVo.ok(null);
    }

    /**
     * 分布式锁(基于redis) 版本2
     *
     * @return
     */
    @GetMapping("/index/test_distributed_lock2")
    @ResponseBody
    public ResponseVo<Object> testDistributedLock2() {
        this.indexService.testDistributedLock2();
        return ResponseVo.ok(null);
    }

    /**
     * 分布式锁(基于redis) 版本3
     *
     * @return
     */
    @GetMapping("/index/test_distributed_lock3")
    @ResponseBody
    public ResponseVo<Object> testDistributedLock3() {
        this.indexService.testDistributedLock3();
        return ResponseVo.ok(null);
    }

    /**
     * 分布式锁(基于redis) 版本4
     *
     * @return
     */
    @GetMapping("/index/test_distributed_lock4")
    @ResponseBody
    public ResponseVo<Object> testDistributedLock4() {
        this.indexService.testDistributedLock4();
        return ResponseVo.ok(null);
    }

    /**
     * 分布式锁(基于redis) 版本5
     *
     * @return
     */
    @GetMapping("/index/test_distributed_lock5")
    @ResponseBody
    public ResponseVo<Object> testDistributedLock5() {
        this.indexService.testDistributedLock5();
        return ResponseVo.ok(null);
    }

    /**
     * 分布式锁(基于redis) 版本6
     *
     * @return
     */
    @GetMapping("/index/test_distributed_lock6")
    @ResponseBody
    public ResponseVo<Object> testDistributedLock6() {
        this.indexService.testDistributedLock6();
        return ResponseVo.ok(null);
    }

    /**
     * Redisson分布式锁
     *
     * @return
     */
    public ResponseVo<Object> testRedissonLock() {
        this.indexService.testRedissonLock();
        return ResponseVo.ok(null);
    }
}
