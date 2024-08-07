package com.atguigu.gmall.oms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.atguigu.gmall.oms.service.OrderItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单项信息
 *
 * @author huxiuyuan
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:34:50
 */
@Api(tags = "订单项信息 管理")
@RestController
@RequestMapping("oms/orderitem")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryOrderItemByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = orderItemService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<OrderItemEntity> queryOrderItemById(@PathVariable("id") Long id) {
        OrderItemEntity orderItem = orderItemService.getById(id);

        return ResponseVo.ok(orderItem);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody OrderItemEntity orderItem) {
        orderItemService.save(orderItem);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody OrderItemEntity orderItem) {
        orderItemService.updateById(orderItem);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        orderItemService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
