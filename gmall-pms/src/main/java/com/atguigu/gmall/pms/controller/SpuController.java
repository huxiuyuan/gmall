package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.service.SpuService;
import com.atguigu.gmall.pms.vo.SpuVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * spu信息
 *
 * @author huXiuYuan
 * @email a811437621@gmail.com
 * @date 2021-11-21 05:23:24
 */
@Api(tags = "spu信息 管理")
@RestController
@RequestMapping("pms/spu")
public class SpuController {

    @Autowired
    private SpuService spuService;

    /**
     * 商品列表 - spu查询按钮
     *
     * @param cid
     * @param paramVo
     * @return ResponseVo<PageResultVo>
     */
    @GetMapping("/category/{categoryId}")
    @ApiOperation("spu分页查询")
    public ResponseVo<PageResultVo> queryCategorysByCid(@PathVariable("categoryId") Long cid,
                                                        PageParamVo paramVo) {
        PageResultVo pageResultVo = spuService.queryCategorysByCid(cid, paramVo);

        return ResponseVo.ok(pageResultVo);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySpuByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = spuService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }

    /**
     * 为搜索服务单独提供的分页查询
     *
     * @param paramVo
     * @return
     */
    @PostMapping("page")
    @ApiOperation("分页查询")
    public ResponseVo<List<SpuEntity>> querySpuByPageJson(@RequestBody PageParamVo paramVo) {
        PageResultVo pageResultVo = spuService.queryPage(paramVo);

        List<SpuEntity> spuEntities = pageResultVo.getList().stream().map(a -> {
            SpuEntity spuEntity = new SpuEntity();
            BeanUtils.copyProperties(a, spuEntity);
            return spuEntity;
        }).collect(Collectors.toList());

        return ResponseVo.ok(spuEntities);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id) {
        SpuEntity spu = spuService.getById(id);

        return ResponseVo.ok(spu);
    }

    /**
     * spu新增之大保存(spu,sku,营销信息 九张表)
     *
     * @param spu
     * @return ResponseVo<Object>
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SpuVo spu) {
        spuService.bigSave(spu);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SpuEntity spu) {
        spuService.updateById(spu);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        spuService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
