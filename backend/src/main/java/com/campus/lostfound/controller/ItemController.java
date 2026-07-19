package com.campus.lostfound.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lostfound.common.BusinessException;
import com.campus.lostfound.common.Constants;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.Blacklist;
import com.campus.lostfound.entity.ItemInfo;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.service.impl.BlacklistServiceImpl;
import com.campus.lostfound.service.impl.ItemInfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    private ItemInfoServiceImpl itemService;

    @Autowired
    private BlacklistServiceImpl blacklistService;

    @PostMapping("/publish")
    public Result<?> publishItem(@RequestBody ItemInfo item, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");

        Blacklist blacklist = blacklistService.getOne(new LambdaQueryWrapper<Blacklist>()
                .eq(Blacklist::getUserId, currentUser.getId())
                .eq(Blacklist::getStatus, Constants.BLACKLIST_STATUS_ACTIVE));
        if (blacklist != null) {
            throw new BusinessException("您的账号已被列入黑名单，无法发布信息");
        }

        item.setUserId(currentUser.getId());
        item.setStatus(Constants.ITEM_STATUS_PENDING);
        item.setViewCount(0);
        item.setCreateTime(new Date());
        itemService.save(item);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<?> listItems(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Integer type,
                               @RequestParam(required = false) String category) {
        Page<ItemInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ItemInfo> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(ItemInfo::getTitle, keyword);
        }
        if (type != null) {
            wrapper.eq(ItemInfo::getType, type);
        }
        if (StrUtil.isNotBlank(category)) {
            wrapper.eq(ItemInfo::getCategory, category);
        }

        wrapper.eq(ItemInfo::getStatus, Constants.ITEM_STATUS_PUBLISHED);
        wrapper.orderByDesc(ItemInfo::getCreateTime);

        return Result.success(itemService.page(page, wrapper));
    }

    @GetMapping("/detail/{id}")
    public Result<?> getItemDetail(@PathVariable Long id) {
        ItemInfo item = itemService.getById(id);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }

        item.setViewCount(item.getViewCount() + 1);
        itemService.updateById(item);

        return Result.success(item);
    }

    @GetMapping("/my")
    public Result<?> getMyItems(@RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        Page<ItemInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ItemInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItemInfo::getUserId, currentUser.getId());
        wrapper.orderByDesc(ItemInfo::getCreateTime);
        return Result.success(itemService.page(page, wrapper));
    }

    @GetMapping("/admin/list")
    public Result<?> adminListItems(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) Integer status,
                                    @RequestParam(required = false) String keyword) {
        Page<ItemInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ItemInfo> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(ItemInfo::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(ItemInfo::getTitle, keyword);
        }
        wrapper.orderByDesc(ItemInfo::getCreateTime);

        return Result.success(itemService.page(page, wrapper));
    }

    @PutMapping("/admin/approve/{id}")
    public Result<?> approveItem(@PathVariable Long id) {
        ItemInfo item = itemService.getById(id);
        if (item == null) {
            throw new BusinessException("Item not found");
        }
        item.setStatus(Constants.ITEM_STATUS_PUBLISHED);
        item.setUpdateTime(new Date());
        itemService.updateById(item);
        return Result.success();
    }

    @PutMapping("/admin/reject/{id}")
    public Result<?> rejectItem(@PathVariable Long id, @RequestBody java.util.Map<String, String> params) {
        ItemInfo item = itemService.getById(id);
        if (item == null) {
            throw new BusinessException("Item not found");
        }
        item.setStatus(Constants.ITEM_STATUS_REJECTED);
        item.setRejectReason(params.get("reason"));
        item.setUpdateTime(new Date());
        itemService.updateById(item);
        return Result.success();
    }

    @PutMapping("/admin/remove/{id}")
    public Result<?> removeItem(@PathVariable Long id) {
        ItemInfo item = itemService.getById(id);
        if (item == null) {
            throw new BusinessException("Item not found");
        }
        item.setStatus(Constants.ITEM_STATUS_REJECTED);
        item.setRejectReason("管理员下架");
        item.setUpdateTime(new Date());
        itemService.updateById(item);
        return Result.success();
    }
}
