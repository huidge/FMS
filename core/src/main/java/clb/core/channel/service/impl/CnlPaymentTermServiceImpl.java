package clb.core.channel.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.channel.dto.CnlPaymentTerm;
import clb.core.channel.mapper.CnlPaymentTermMapper;
import clb.core.channel.service.ICnlPaymentTermService;

import org.springframework.transaction.annotation.Transactional;
/**
 * Created by wanjun.feng on 2017/4/19.
 */
@Service
@Transactional
public class CnlPaymentTermServiceImpl extends BaseServiceImpl<CnlPaymentTerm> implements ICnlPaymentTermService{
    @Autowired
    private CnlPaymentTermMapper cnlPaymentTermMapper;
    
    @Override
    public List<CnlPaymentTerm> query(IRequest iRequest, CnlPaymentTerm cnlPaymentTerm, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return cnlPaymentTermMapper.selectCnlPaymentTerms(cnlPaymentTerm);
    }
    
    /**
     *@description 生成付款条件流水号
     *@return String 付款条件流水号
     */
    private String createTermCode() {
        //获取最大流水号的数字部分
        String code = cnlPaymentTermMapper.selectMaxTermCode();
        if(code == null)code="0";
        else code = code.substring(2);
        int data = Integer.parseInt(code);
        //流水号加1
        data += 1;
        //前面加上S
        code = "PA"+String.format("%06d",data);
        return code;
    }

    @Override
    public List<CnlPaymentTerm> paymentTermBatchUpdate(IRequest iRequest, List<CnlPaymentTerm> cnlPaymentTermList) {
        for(CnlPaymentTerm cnlPaymentTerm:cnlPaymentTermList){
            if(cnlPaymentTerm.get__status().equals(DTOStatus.ADD)){
                cnlPaymentTerm.setPaymentTermCode(createTermCode());
                this.insertSelective(iRequest,cnlPaymentTerm);
            } else {
                this.updateByPrimaryKeySelective(iRequest,cnlPaymentTerm);
            }
        }
        return cnlPaymentTermList;
    }
}