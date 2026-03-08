package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.OrderDTO;
import com.herb.platform.service.OrderService;
import com.herb.platform.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 订单控制器
 */
@Api(tags = "订单管理")
@RestController
@RequestMapping("/order")
@Validated
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @ApiOperation("分页查询订单")
    @GetMapping("/page")
    @RequireUserType({Constants.USER_TYPE_FARMER, Constants.USER_TYPE_BUYER})
    public Result<IPage<OrderVO>> page(
            @ApiParam("订单状态") @RequestParam(required = false) Integer orderStatus,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        IPage<OrderVO> page = orderService.page(userId, userType, orderStatus, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("查询订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }

    @ApiOperation("创建订单")
    @PostMapping
    @RequireUserType(Constants.USER_TYPE_BUYER)
    public Result<Long> createOrder(@Validated @RequestBody OrderDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long orderId = orderService.createOrder(userId, dto);
        return Result.success(orderId);
    }

    @ApiOperation("卖家确认订单")
    @PutMapping("/{id}/confirm")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> confirmOrder(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.confirmOrder(userId, id);
        return Result.success();
    }

    @ApiOperation("卖家发货")
    @PutMapping("/{id}/deliver")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> deliverOrder(
            @PathVariable Long id,
            @ApiParam("物流公司") @RequestParam String logisticsCompany,
            @ApiParam("物流单号") @RequestParam String logisticsNo,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.deliverOrder(userId, id, logisticsCompany, logisticsNo);
        return Result.success();
    }

    @ApiOperation("买家确认收货")
    @PutMapping("/{id}/complete")
    @RequireUserType(Constants.USER_TYPE_BUYER)
    public Result<Void> completeOrder(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.completeOrder(userId, id);
        return Result.success();
    }

    @ApiOperation("取消订单")
    @PutMapping("/{id}/cancel")
    @RequireUserType({Constants.USER_TYPE_FARMER, Constants.USER_TYPE_BUYER})
    public Result<Void> cancelOrder(
            @PathVariable Long id,
            @ApiParam("取消原因") @RequestParam(required = false) String cancelReason,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancelOrder(userId, id, cancelReason);
        return Result.success();
    }
}
