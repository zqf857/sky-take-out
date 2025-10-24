package com.sky.service;

public interface ShopService {

    /**
     * 设置营业状态
     * @param status
     */
    void setStatus(Integer status);

    /**
     * 获取店铺营业状态
     * @return
     */
    Integer getStatus();
}
