package clb.core.wecorp.service;

import clb.core.wecorp.dto.WecorpMaterial;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;


public interface IWecorpMaterialService extends IBaseService<WecorpMaterial>,ProxySelf<IWecorpMaterialService> {

    WecorpMaterial findMaterial(String attachmentId, String purpose, String accountNum, String enableFlag);

    WecorpMaterial findMaterialByattachmentId(String attachmentId);

    WecorpMaterial findByAccountMediaIdAndAccountNum(String accountMediaId,String accountNum);

    List<WecorpMaterial> queryMaterial(int page, int pagesize, WecorpMaterial dto);

    Boolean deleteMaterial(String materialId, IRequest iRequest);

    JSONObject createNewsMaterialByArticles(List news_id_array) throws Exception;

    JSONArray synMaterial(int count, String appid , String type) throws Exception;

}
