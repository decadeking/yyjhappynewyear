package com.campus.lostfound.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lostfound.common.BusinessException;
import com.campus.lostfound.common.Constants;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.Blacklist;
import com.campus.lostfound.service.impl.BlacklistServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/blacklist")
public class BlacklistController {

    @Autowired
    private BlacklistServiceImpl blacklistService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Blacklist> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Blacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Blacklist::getCreateTime);
        return Result.success(blacklistService.page(page, wrapper));
    }

    @PostMapping("/add")
    public Result<?> addBlacklist(@RequestBody Blacklist blacklist) {
        Blacklist existing = blacklistService.getOne(new LambdaQueryWrapper<Blacklist>()
                .eq(Blacklist::getUserId, blacklist.getUserId())
                .eq(Blacklist::getStatus, Constants.BLACKLIST_STATUS_ACTIVE));
        if (existing != null) {
            throw new BusinessException("该用户已被列入黑名单");
        }
        blacklist.setStatus(Constants.BLACKLIST_STATUS_ACTIVE);
        blacklist.setCreateTime(new Date());
        blacklistService.save(blacklist);
        return Result.success();
    }

    @PutMapping("/remove/{id}")
    public Result<?> removeBlacklist(@PathVariable Long id) {
        Blacklist blacklist = blacklistService.getById(id);
        if (blacklist == null) {
            throw new BusinessException("记录不存在");
        }
        blacklist.setStatus(Constants.BLACKLIST_STATUS_INACTIVE);
        blacklistService.updateById(blacklist);
        return Result.success();
    }
}
