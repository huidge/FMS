package clb.core.wecorp.service;

import clb.core.wecorp.dto.WecorpArticle;
import clb.core.wecorp.dto.WecorpArticleDTO;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

/**
 * Created by shanhd on 2016/10/20.
 */
public interface IWecorpArticleService extends IBaseService<WecorpArticle>,ProxySelf<IWecorpArticleService> {

    int createSave(WecorpArticle[] articles, IRequest iRequest);
    int editSave(WecorpArticleDTO dto, IRequest iRequest);
    List<WecorpArticle> getArticle(int page, int pageSize);
    List<WecorpArticle> getArticleByGroupNumber(String groupNumber);
    boolean sendArticle(String articleId, boolean isGroup);

}
