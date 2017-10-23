package clb.core.production.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.production.dto.PrdCashValue;
/*****
 * @author tiansheng.ye
 * @Date 2017/05/16
 */
public abstract interface PrdCashValueMapper extends Mapper<PrdCashValue>{

	 public List<PrdCashValue> selectAllFields(PrdCashValue prdCashValue);
}
