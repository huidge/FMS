package clb.core.order.service.impl;

import clb.core.order.mapper.OrdFileMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdFile;
import clb.core.order.service.IOrdFileService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdFileServiceImpl extends BaseServiceImpl<OrdFile> implements IOrdFileService{
    @Autowired
    private OrdFileMapper ordFileMapper;

    @Autowired
    private ISysFileService sysFileService;

    /**
     * 查询
     * @param request
     * @param ordFile
     * @return
     */
    @Override
    public List<OrdFile> queryOrdFile(IRequest request, OrdFile ordFile) {

        List<OrdFile> ordFiles = ordFileMapper.queryOrdFile(ordFile);

        for (OrdFile k : ordFiles) {
            if (k.getFileId() != null) {
                SysFile sysFile = sysFileService.selectByPrimaryKey(request, k.getFileId());
                if (sysFile != null) {
                    k.setToken(sysFile.get_token());
                    k.setFileName(sysFile.getFileName());
                } else {
                    k.setToken(null);
                    k.setFileName(null);
                }

            }

        }
        return ordFiles;
    }
}