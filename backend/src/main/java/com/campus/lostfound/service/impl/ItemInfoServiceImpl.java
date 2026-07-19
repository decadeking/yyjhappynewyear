package com.campus.lostfound.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lostfound.entity.ItemInfo;
import com.campus.lostfound.mapper.ItemInfoMapper;
import com.campus.lostfound.service.ItemInfoService;
import org.springframework.stereotype.Service;

@Service
public class ItemInfoServiceImpl extends ServiceImpl<ItemInfoMapper, ItemInfo> implements ItemInfoService {
}
