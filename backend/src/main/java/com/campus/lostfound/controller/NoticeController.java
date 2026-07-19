package com.campus.lostfound.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lostfound.common.BusinessException;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.Notice;
import com.campus.lostfound.service.impl.NoticeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeServiceImpl noticeService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Notice::getCreateTime);
        return Result.success(noticeService.page(page, wrapper));
    }

    @PostMapping("/add")
    public Result<?> addNotice(@RequestBody Notice notice) {
        notice.setCreateTime(new Date());
        notice.setUpdateTime(new Date());
        noticeService.save(notice);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> updateNotice(@RequestBody Notice notice) {
        if (notice.getId() == null) {
            throw new BusinessException("公告ID不能为空");
        }
        notice.setUpdateTime(new Date());
        noticeService.updateById(notice);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteNotice(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success();
    }
}
