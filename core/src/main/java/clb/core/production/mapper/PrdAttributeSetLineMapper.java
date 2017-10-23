package clb.core.production.mapper;
/**
 * Created by jiaolong.li on 2017-04-18.
 */
import clb.core.production.dto.PrdAttributeSet;
import com.hand.hap.mybatis.common.Mapper;
import clb.core.production.dto.PrdAttributeSetLine;

import java.util.List;

public interface PrdAttributeSetLineMapper extends Mapper<PrdAttributeSetLine>{

    public List<PrdAttributeSetLine> selectAllFields(PrdAttributeSetLine prdAttributeSetLine);
}