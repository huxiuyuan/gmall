package com.atguigu.gmall.oms.mapper;

import com.atguigu.gmall.oms.entity.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录
 * 
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:34:50
 */
@Mapper
public interface OrderOperateHistoryMapper extends BaseMapper<OrderOperateHistoryEntity> {
	
}
