package clb.core.production.mapper;
/**
 * Created by jiaolong.li on 2017-04-18.
 */
import com.hand.hap.mybatis.common.Mapper;
import clb.core.production.dto.PrdAttributeSet;

import java.util.List;

public interface PrdAttributeSetMapper extends Mapper<PrdAttributeSet>{

    public List<PrdAttributeSet> selectAllFields(PrdAttributeSet prdAttributeSet);
}