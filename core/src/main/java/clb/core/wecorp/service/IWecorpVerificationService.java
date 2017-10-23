package clb.core.wecorp.service;


import net.sf.json.JSONObject;

/**
 * Created by Administrator on 2017/6/15.
 */
public interface IWecorpVerificationService {
    JSONObject verification(String phone,String code);
    void getCode(String code);
}
