package clb.core.system.mapper;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.system.dto.ClbCodeValue;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wanjun.feng@hand-china.com
 */
public interface ClbCodeValueMapper extends Mapper<ClbCodeValue> {
    int deleteByCodeId(ClbCodeValue key);

    int deleteTlByCodeId(ClbCodeValue key);

    List<ClbCodeValue> selectCodeValuesByCodeName(String codeName);

    List<ClbCodeValue> selectCodeValueByMeaning(@Param("code") String code, @Param("meaning") String meaning);

    List<ClbCodeValue> queryMsgTemCodeLov(@Param("value") String value, @Param("meaning") String meaning);
    
    List<ClbCodeValue> queryEmlAccountCodeLov(@Param("value") String value, @Param("meaning") String meaning);

    List<ClbCodeValue> selectCodeValuesByCodeId(ClbCodeValue codeValue);
    
    List<ClbCodeValue> selectCodeValuesByParam(ClbCodeValue code);
}