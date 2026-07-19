package com.campus.lostfound.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.lostfound.common.Constants;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.ClaimRecord;
import com.campus.lostfound.entity.ItemInfo;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.service.impl.ClaimRecordServiceImpl;
import com.campus.lostfound.service.impl.ItemInfoServiceImpl;
import com.campus.lostfound.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private ItemInfoServiceImpl itemService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ClaimRecordServiceImpl claimService;

    @GetMapping("/stats")
    public Result<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalItems", itemService.count());
        stats.put("totalUsers", userService.count());
        stats.put("publishedItems", itemService.count(new LambdaQueryWrapper<ItemInfo>()
                .eq(ItemInfo::getStatus, Constants.ITEM_STATUS_PUBLISHED)));
        stats.put("completedItems", itemService.count(new LambdaQueryWrapper<ItemInfo>()
                .eq(ItemInfo::getStatus, Constants.ITEM_STATUS_COMPLETED)));
        stats.put("lostItems", itemService.count(new LambdaQueryWrapper<ItemInfo>()
                .eq(ItemInfo::getType, Constants.ITEM_TYPE_LOST)));
        stats.put("foundItems", itemService.count(new LambdaQueryWrapper<ItemInfo>()
                .eq(ItemInfo::getType, Constants.ITEM_TYPE_FOUND)));
        stats.put("pendingClaims", claimService.count(new LambdaQueryWrapper<ClaimRecord>()
                .eq(ClaimRecord::getStatus, Constants.CLAIM_STATUS_PENDING)));
        return Result.success(stats);
    }

    @GetMapping("/category-stats")
    public Result<?> getCategoryStats() {
        List<ItemInfo> items = itemService.list();
        Map<String, Integer> categoryMap = new HashMap<>();
        for (ItemInfo item : items) {
            String category = item.getCategory() != null ? item.getCategory() : "Other";
            categoryMap.put(category, categoryMap.getOrDefault(category, 0) + 1);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", entry.getKey());
            map.put("value", entry.getValue());
            result.add(map);
        }
        return Result.success(result);
    }

    @GetMapping("/monthly-stats")
    public Result<?> getMonthlyStats() {
        List<ItemInfo> items = itemService.list();
        Map<String, Integer> monthlyMap = new LinkedHashMap<>();

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        for (ItemInfo item : items) {
            if (item.getCreateTime() != null) {
                String month = sdf.format(item.getCreateTime());
                monthlyMap.put(month, monthlyMap.getOrDefault(month, 0) + 1);
            }
        }

        List<String> months = new ArrayList<>(monthlyMap.keySet());
        List<Integer> counts = new ArrayList<>(monthlyMap.values());

        Map<String, Object> result = new HashMap<>();
        result.put("months", months);
        result.put("counts", counts);
        return Result.success(result);
    }
}
