package clb.core.wecorp.service;

import clb.core.wecorp.dto.WecorpEmoji;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

/**
 * Created by lp on 2016/11/16.
 */
public interface IWecorpEmojiService extends IBaseService<WecorpEmoji>, ProxySelf<IWecorpEmojiService> {
    List<WecorpEmoji> getAllEmoji();
}
