package clb.core.channel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.channel.dto.CnlPaymentTerm;
/**
 * Created by wanjun.feng on 2017/4/19.
 */
public interface CnlPaymentTermMapper extends Mapper<CnlPaymentTerm>{
    List<CnlPaymentTerm> selectCnlPaymentTerms(@Param("paymentTerm")CnlPaymentTerm cnlPaymentTerm);
    
    String selectMaxTermCode();
}