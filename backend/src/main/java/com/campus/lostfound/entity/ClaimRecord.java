package com.campus.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("claim_record")
public class ClaimRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long itemId;

    private Long claimUserId;

    private Long ownerUserId;

    private String claimInfo;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
