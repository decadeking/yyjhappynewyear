package com.campus.lostfound.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lostfound.entity.ClaimRecord;
import com.campus.lostfound.mapper.ClaimRecordMapper;
import com.campus.lostfound.service.ClaimRecordService;
import org.springframework.stereotype.Service;

@Service
public class ClaimRecordServiceImpl extends ServiceImpl<ClaimRecordMapper, ClaimRecord> implements ClaimRecordService {
}
