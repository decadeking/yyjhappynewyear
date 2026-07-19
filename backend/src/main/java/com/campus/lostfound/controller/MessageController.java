package com.campus.lostfound.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.Message;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.service.impl.MessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageServiceImpl messageService;

    @PostMapping("/add")
    public Result<?> addMessage(@RequestBody Message message, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        message.setUserId(currentUser.getId());
        message.setCreateTime(new Date());
        messageService.save(message);
        return Result.success();
    }

    @GetMapping("/list/{itemId}")
    public Result<?> listMessages(@PathVariable Long itemId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getItemId, itemId);
        wrapper.orderByAsc(Message::getCreateTime);
        return Result.success(messageService.list(wrapper));
    }
}
