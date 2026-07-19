package com.campus.lostfound.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lostfound.entity.Blacklist;
import com.campus.lostfound.mapper.BlacklistMapper;
import com.campus.lostfound.service.BlacklistService;
import org.springframework.stereotype.Service;

@Service
public class BlacklistServiceImpl extends ServiceImpl<BlacklistMapper, Blacklist> implements BlacklistService {
}
