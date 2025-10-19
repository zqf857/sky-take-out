package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @AutoFill(OperationType.INSERT)
    void save(Setmeal setmeal);

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 套餐分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO selectById(Long id);

    /**
     * 根据套餐id查询套餐status
     * @param id
     * @return
     */
    @Select("select status from setmeal where id = #{id}")
    Integer selectStatusById(Long id);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 修改套餐
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);
}
