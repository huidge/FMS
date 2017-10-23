package clb.core.channel.service.impl;

import clb.core.channel.mapper.CnlChannelArchiveMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.channel.dto.CnlChannelArchive;
import clb.core.channel.service.ICnlChannelArchiveService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author jun.zhao@hand-china.com
 * @version 1.0
 * @name CnlChannelArchiveServiceImpl
 * @description 渠道档案信息 service 接口实现类
 * @date 2017/4/25
 */
@Service
@Transactional
public class CnlChannelArchiveServiceImpl extends BaseServiceImpl<CnlChannelArchive> implements ICnlChannelArchiveService{
    @Autowired
    private CnlChannelArchiveMapper cnlChannelArchiveMapper;
    @Autowired
    private ISysFileService sysFileService;

    /**
     * 查询
     * @param request
     * @param cnlChannelArchive
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<CnlChannelArchive> queryArchive(IRequest request, CnlChannelArchive cnlChannelArchive, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<CnlChannelArchive> cList = cnlChannelArchiveMapper.queryArchive(cnlChannelArchive);
        for (CnlChannelArchive k : cList) {
            if (k.getFileId() != null) {
                SysFile sysFile = sysFileService.selectByPrimaryKey(request, k.getFileId());
                if (sysFile != null) {
                  k.set_token(sysFile.get_token());
                }else {
                    k.set_token(null);
                }

            }
        }
        return cList;
    }
}