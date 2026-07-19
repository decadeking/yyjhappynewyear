package com.campus.lostfound.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lostfound.common.BusinessException;
import com.campus.lostfound.common.Constants;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.ClaimRecord;
import com.campus.lostfound.entity.ItemInfo;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.service.impl.ClaimRecordServiceImpl;
import com.campus.lostfound.service.impl.ItemInfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/api/claim")
public class ClaimController {

    @Autowired
    private ClaimRecordServiceImpl claimService;

    @Autowired
    private ItemInfoServiceImpl itemService;

    @PostMapping("/submit")
    public Result<?> submitClaim(@RequestBody ClaimRecord claim, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");

        ItemInfo item = itemService.getById(claim.getItemId());
        if (item == null) {
            throw new BusinessException("物品不存在");
        }

        if (item.getStatus() != Constants.ITEM_STATUS_PUBLISHED) {
            throw new BusinessException("该物品不可认领");
        }

        if (item.getUserId().equals(currentUser.getId())) {
            throw new BusinessException("不能认领自己发布的物品");
        }

        claim.setClaimUserId(currentUser.getId());
        claim.setOwnerUserId(item.getUserId());
        claim.setStatus(Constants.CLAIM_STATUS_PENDING);
        claim.setCreateTime(new Date());
        claimService.save(claim);

        return Result.success();
    }

    @GetMapping("/my")
    public Result<?> getMyClaims(@RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        Page<ClaimRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ClaimRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClaimRecord::getClaimUserId, currentUser.getId());
        wrapper.orderByDesc(ClaimRecord::getCreateTime);
        return Result.success(claimService.page(page, wrapper));
    }

    @GetMapping("/received")
    public Result<?> getReceivedClaims(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        Page<ClaimRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ClaimRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClaimRecord::getOwnerUserId, currentUser.getId());
        wrapper.orderByDesc(ClaimRecord::getCreateTime);
        return Result.success(claimService.page(page, wrapper));
    }

    @PutMapping("/approve/{id}")
    public Result<?> approveClaim(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        ClaimRecord claim = claimService.getById(id);

        if (claim == null) {
            throw new BusinessException("认领记录不存在");
        }

        if (!claim.getOwnerUserId().equals(currentUser.getId())) {
            throw new BusinessException("您不是该物品的发布者");
        }

        claim.setStatus(Constants.CLAIM_STATUS_APPROVED);
        claim.setUpdateTime(new Date());
        claimService.updateById(claim);

        ItemInfo item = itemService.getById(claim.getItemId());
        item.setStatus(Constants.ITEM_STATUS_COMPLETED);
        item.setUpdateTime(new Date());
        itemService.updateById(item);

        return Result.success();
    }

    @PutMapping("/reject/{id}")
    public Result<?> rejectClaim(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        ClaimRecord claim = claimService.getById(id);

        if (claim == null) {
            throw new BusinessException("Claim record not found");
        }

        if (!claim.getOwnerUserId().equals(currentUser.getId())) {
            throw new BusinessException("You are not the owner of this item");
        }

        claim.setStatus(Constants.CLAIM_STATUS_REJECTED);
        claim.setUpdateTime(new Date());
        claimService.updateById(claim);

        return Result.success();
    }
}
