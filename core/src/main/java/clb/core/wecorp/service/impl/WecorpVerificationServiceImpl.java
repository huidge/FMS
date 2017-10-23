package clb.core.wecorp.service.impl;

import clb.core.wecorp.dto.WecorpVerificationCode;
import clb.core.wecorp.mapper.WecorpVerificationCodeMapper;
import clb.core.wecorp.service.IWecorpVerificationService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Administrator on 2017/6/15.
 */
@Service
@Transactional
public class WecorpVerificationServiceImpl implements IWecorpVerificationService {

    @Autowired
    WecorpVerificationCodeMapper wecorpVerificationCodeMapper;

    @Override
    public JSONObject verification(String phone, String code) {
        String userId = SecurityUtils.getCurrentUserLogin();
        WecorpVerificationCode verificationCode = wecorpVerificationCodeMapper.findByPhoneAndEnableFlag(phone);
        JSONObject obj=new JSONObject();
        if(verificationCode !=null && phone.equals(verificationCode.getPhone()) && code.equals(verificationCode.getCode()) ){
            if(new Date().getTime()-verificationCode.getCreationDate().getTime()<180000){
                obj.put("status","true");
                obj.put("meg","校验成功");
            }else {
                obj.put("status","false");
                obj.put("meg","验证码超时");
            }
        }else {
            obj.put("status","false");
            obj.put("meg","验证码错误");
        }
        return obj;
    }

    @Override
    public void getCode(String phone) {
        wecorpVerificationCodeMapper.disableOtherCode(phone);

        long verificationCode = 888888L;

        /*long verificationCode = Math.round(Math.random() * 9000 + 1000);
        String info = sendCancelMsg(phone, String.valueOf(verificationCode));*/
        if ("0".equals("0")) {
            WecorpVerificationCode code = new WecorpVerificationCode();
            code.setPhone(phone);
            code.setEnableFlag("Y");
            code.setCode(String.valueOf(verificationCode));
            wecorpVerificationCodeMapper.insertSelective(code);
        }
    }
}
