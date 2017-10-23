package clb.core.production.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.production.dto.PrdDiscount;
/**
 * Created by wanjun.feng on 17/4/18.
 */
public abstract interface PrdDiscountMapper extends Mapper<PrdDiscount> {
    List<PrdDiscount> selectDiscounts(@Param("prdDiscount")PrdDiscount prdDiscount);
}