package clb.core.production.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.production.dto.PrdClassSet;
/**
 * Created by wanjun.feng on 17/4/12.
 */
public abstract interface PrdClassSetMapper extends Mapper<PrdClassSet> {
    List<PrdClassSet> selectClassSets(@Param("classSet")PrdClassSet prdClassSet);
}