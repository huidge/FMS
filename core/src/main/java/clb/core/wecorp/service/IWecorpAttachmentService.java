package clb.core.wecorp.service;

import clb.core.wecorp.dto.WecorpAttachment;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

/**
 * Created by shanhd on 2016/10/20.
 */
public interface IWecorpAttachmentService extends IBaseService<WecorpAttachment>,ProxySelf<IWecorpAttachmentService> {

    int countAttachment(String groupId, String type);

    List<WecorpAttachment> queryByGroupIdAndType(String groupId, String type, int pageCount, int pageSize);

    int batchUpdateAttachment(IRequest iRequest, List<WecorpAttachment> list) throws Exception;
}
