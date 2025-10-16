package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品和对应的口味
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 向菜品表插入一条数据
        dishMapper.insert(dish);

        // 获取insert语句生成的主键值
        Long dishId = dish.getId();

        // 向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    public void deleteByIds(List<Long> ids) {
        // 是否存在起售中的菜品
        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                // 当前菜品处于起售中
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 当前菜品是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            // 当前菜品被其他套餐关联了,不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品表的菜品数据
        for (Long id : ids) {
            dishMapper.deleteById(id);
            // 删除口味
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    /**
     * 菜品启用禁用
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        // 当前菜品是否被其他套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(Collections.singletonList(id));
        if(setmealIds != null && setmealIds.size() > 0){
            throw new BaseException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Transactional
    public void modifyWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        // 先删除口味,再添加口味就行了,不好修改
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        // 向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 查询菜品
     * @param id
     * @return
     */
    public DishVO selectById(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        // 返回口味用于前端表单回显
        dishVO.setFlavors(dishFlavorList);
        return dishVO;
    }

    /**
     * 根据分类查询菜品
     * @param categoryId
     */
    public List<DishVO> selectByCategoryId(Long categoryId) {
        List<DishVO> list = dishMapper.getByCategoryId(categoryId);
        return list;
    }
}