package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置营业状态
     * @param status
     */
    public void setStatus(Integer status) {
        redisTemplate.opsForValue().set("SHOP_STATUS", status);
    }

    /**
     * 获取店铺营业状态
     * @return
     */
    public Integer getStatus() {
        Object shopStatus = redisTemplate.opsForValue().get("SHOP_STATUS");
        return (Integer) shopStatus;
    }
}
