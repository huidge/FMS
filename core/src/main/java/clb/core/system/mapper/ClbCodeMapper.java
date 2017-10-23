package clb.core.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.system.dto.ClbCode;

/**
 * @author wanjun.feng@hand-china.com
 */
public interface ClbCodeMapper extends Mapper<ClbCode> {
    public List<ClbCode> selectCode(@Param("code") ClbCode code);
}