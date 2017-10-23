package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpArticle;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

/**
 * Created by shanhd on 2016/10/20.
 */
public interface WecorpArticleMapper extends Mapper<WecorpArticle> {

    List<WecorpArticle> getArticle();
    List<WecorpArticle> getArticleByGroupNumber(String groupNumber);
}
