package clb.core.production.mapper;

/**
 * Created by jiaolong.li on 2017-04-13.
 */
import com.hand.hap.mybatis.common.Mapper;
import clb.core.production.dto.PrdAttribute;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface PrdAttributeMapper extends Mapper<PrdAttribute>{

    public List<PrdAttribute> selectAllFields(PrdAttribute prdAttribute);

    public String queryDefaultValue(String codeId, String meaning);
    
    PrdAttribute selectAttrValue(@Param(value="attSetId")Long attSetId, @Param(value="attId")Long attId);
}