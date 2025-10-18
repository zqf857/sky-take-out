package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        if(setmealDTO.getSetmealDishes() == null || setmealDTO.getSetmealDishes().isEmpty()){
            throw new BaseException(MessageConstant.SETMEAL_DISHES_IS_NULL);
        }

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.save(setmeal);

        if(setmeal.getId() == null){
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        // 遍历setmealDish 插入setmealId
        for(SetmealDish setmealDish : setmealDishes){
            setmealDish.setSetmealId(setmeal.getId());
        }

        setmealDishMapper.save(setmealDishes);
    }
}
