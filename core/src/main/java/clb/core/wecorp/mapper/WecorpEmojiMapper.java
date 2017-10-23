package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpEmoji;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

/**
 * Created by lp on 2016/11/16.
 */
public interface WecorpEmojiMapper extends Mapper<WecorpEmoji> {
    List<WecorpEmoji> getAllEmoji();
}
