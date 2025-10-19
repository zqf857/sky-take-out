package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
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

    /**
     * 套餐分类查询
     * @param categoryPageQueryDTO
     * @return
     */
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(categoryPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Transactional
    public SetmealVO selectById(Long id) {

        SetmealVO setmealVO = setmealMapper.selectById(id);

        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);

        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }


    /**
     * 删除套餐
     * @param ids
     */
    @Transactional
    public void deleteByIds(List<Long> ids) {

        // 查看套餐是否是启用状态
        for (Long id : ids){
            Integer status = setmealMapper.selectStatusById(id);
            if(status == null || status != StatusConstant.DISABLE){
                throw new BaseException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        // 删除套餐
        setmealMapper.deleteByIds(ids);

        // 删除套餐对应菜品
        setmealDishMapper.deleteBySetmealIds(ids);
    }
}
