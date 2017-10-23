package clb.core.production.mapper;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.production.dto.PrdPremiumAdjust;

import java.util.List;

/*****
 * @author tiansheng.ye
 * @Date 2017/05/16
 */
public abstract interface PrdPremiumAdjustMapper extends Mapper<PrdPremiumAdjust>{

   public List<PrdPremiumAdjust> selectAllFields(PrdPremiumAdjust prdPremiumAdjust);
}
