package clb.core.wecorp.service.impl;

import clb.core.attachment.Uploader;
import clb.core.attachment.UploaderFactory;
import clb.core.wecorp.dto.WecorpAttachment;
import clb.core.wecorp.mapper.WecorpAttachmentMapper;
import clb.core.wecorp.service.IWecorpAttachmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.attachment.exception.UniqueFileMutiException;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by shanhd on 2016/10/20.
 */
@Service
public class WecorpAttachmentServiceImpl extends BaseServiceImpl<WecorpAttachment> implements IWecorpAttachmentService {

    @Autowired
    private WecorpAttachmentMapper wecorpAttachmentMapper;

    @Override
    public int countAttachment(String groupId, String type) {
        return wecorpAttachmentMapper.countAttachment(groupId,type);
    }

    @Override
    public List<WecorpAttachment> queryByGroupIdAndType(String groupId, String type,int pageCount,int pageSize) {
        PageHelper.startPage(pageCount, pageSize);
        return wecorpAttachmentMapper.queryByGroupIdAndType(groupId,type);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int batchUpdateAttachment(IRequest iRequest, List<WecorpAttachment> list) throws Exception {
        WecorpAttachment result=null;
        for(WecorpAttachment attachment:list){
            result=self().updateByPrimaryKeySelective(iRequest, attachment);
            if(result==null){
                throw new Exception("更新失败");
            }
        }
        return 1;
    }

    public String Upload(HttpServletRequest request,IRequest requestContext)
            throws StoragePathNotExsitException, UniqueFileMutiException, JsonProcessingException {
        Map<String, Object> response = new HashMap<String, Object>();
        Uploader uploader = UploaderFactory.getMutiUploader();
        uploader.init(request);
        return null;
    }
}
