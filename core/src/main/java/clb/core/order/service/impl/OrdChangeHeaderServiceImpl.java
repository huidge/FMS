package clb.core.order.service.impl;

import clb.core.order.mapper.OrdChangeHeaderMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import clb.core.common.utils.AESUtil;
import clb.core.order.dto.OrdChangeHeader;
import clb.core.order.dto.OrdChangeLine;
import clb.core.order.service.IOrdChangeHeaderService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdChangeHeaderServiceImpl extends BaseServiceImpl<OrdChangeHeader> implements IOrdChangeHeaderService{
    @Autowired
    private OrdChangeHeaderMapper ordChangeHeaderMapper;

    @Override
    public ResponseData queryOrdChange(IRequest request, OrdChangeHeader ordChangeHeader) {
        List<OrdChangeHeader> ordChanges = ordChangeHeaderMapper.queryOrdChangeHis(ordChangeHeader);
        if (ordChanges != null) {
            for (OrdChangeHeader ordChange : ordChanges) {
                if (ordChange.getOrdChangeLineList() == null || ordChange.getOrdChangeLineList().size() == 0) {
                    //ordChanges.remove(ordChange);
                } else {
                    for (OrdChangeLine ordChangeLine : ordChange.getOrdChangeLineList()) {
                        //电话号码解密
                        if (ordChangeLine.getColumnName().equals("投保人-联系电话")
                                || ordChangeLine.getColumnName().equals("受保人-联系电话")) {
                            try {
                                String phoneCode = AESUtil.decrypt("CLB", ordChangeLine.getOldValue().split("-")[0]);
                                String phone = AESUtil.decrypt("CLB", ordChangeLine.getOldValue().split("-")[1]);
                                ordChangeLine.setOldValue(phoneCode+"-"+phone);
                                phoneCode = AESUtil.decrypt("CLB", ordChangeLine.getNewValue().split("-")[0]);
                                phone = AESUtil.decrypt("CLB", ordChangeLine.getNewValue().split("-")[1]);
                                ordChangeLine.setNewValue(phoneCode+"-"+phone);
                            } catch (Exception e) {
                                ResponseData response = new ResponseData(false);
                                response.setMessage("联系电话解密出错！");
                                return response;
                            }
                        }
                    }
                }
            }
        }
        return new ResponseData(ordChanges);
    }

}