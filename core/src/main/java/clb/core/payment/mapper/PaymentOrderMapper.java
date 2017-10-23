package clb.core.payment.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.payment.dto.PaymentOrder;
/*****
 * @author tiansheng.ye
 * @Date 2017-05-25
 */
public interface PaymentOrderMapper extends Mapper<PaymentOrder>{

	List<PaymentOrder> queryPayOverdue(PaymentOrder dto);
}
