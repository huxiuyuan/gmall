package com.atguigu.gmall.sms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.entity.CouponHistoryEntity;
import com.atguigu.gmall.sms.service.CouponHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券领取历史记录
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2021-11-21 00:29:59
 */
@Api(tags = "优惠券领取历史记录 管理")
@RestController
@RequestMapping("sms/couponhistory")
public class CouponHistoryController {

    @Autowired
    private CouponHistoryService couponHistoryService;

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryCouponHistoryByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = couponHistoryService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<CouponHistoryEntity> queryCouponHistoryById(@PathVariable("id") Long id){
		CouponHistoryEntity couponHistory = couponHistoryService.getById(id);

        return ResponseVo.ok(couponHistory);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody CouponHistoryEntity couponHistory){
		couponHistoryService.save(couponHistory);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody CouponHistoryEntity couponHistory){
		couponHistoryService.updateById(couponHistory);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		couponHistoryService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
