package clb.core.production.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.production.dto.PrdPremium;
/*****
 * @author tiansheng.ye
 * @Date 2017/05/16
 */
public abstract interface PrdPremiumMapper extends Mapper<PrdPremium>{
	
	public List<PrdPremium> selectAllFields(PrdPremium prdPremium);

	public Double queryAmount(PrdPremium prdPremium);
}
