package clb.core.channel.service.impl;

import clb.core.channel.mapper.CnlContractArchiveMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.channel.dto.CnlContractArchive;
import clb.core.channel.service.ICnlContractArchiveService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author jun.zhao@hand-china.com
 * @version 1.0
 * @name CnlChannelContactServiceImpl
 * @description 渠道合约档案信息 service 接口实现类
 * @date 2017/4/25
 */
@Service
@Transactional
public class CnlContractArchiveServiceImpl extends BaseServiceImpl<CnlContractArchive> implements ICnlContractArchiveService{
    @Autowired
    private CnlContractArchiveMapper cnlContractArchiveMapper;
    @Autowired
    private ISysFileService sysFileService;

    /**
     * 查询
     * @param request
     * @param cnlContractArchive
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<CnlContractArchive> queryArchive(IRequest request, CnlContractArchive cnlContractArchive, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<CnlContractArchive> cList = cnlContractArchiveMapper.queryArchive(cnlContractArchive);
        for (CnlContractArchive k : cList) {
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