package clb.core.wecorp.service.impl;

import clb.core.wecorp.dto.WecorpEmoji;
import clb.core.wecorp.mapper.WecorpEmojiMapper;
import clb.core.wecorp.service.IWecorpEmojiService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lp on 2016/11/16.
 */
@Service
public class WecorpEmojiServiceImpl extends BaseServiceImpl<WecorpEmoji> implements IWecorpEmojiService {

    @Autowired
    private WecorpEmojiMapper wecorpEmojiMapper;

    @Override
    public List<WecorpEmoji> getAllEmoji() {
        return wecorpEmojiMapper.getAllEmoji();
    }
}
