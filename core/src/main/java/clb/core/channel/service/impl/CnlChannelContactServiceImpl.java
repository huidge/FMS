package clb.core.channel.service.impl;

import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.mapper.CnlChannelContactMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.channel.dto.CnlChannelContact;
import clb.core.channel.service.ICnlChannelContactService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author jun.zhao@hand-china.com
 * @version 1.0
 * @name CnlChannelContactServiceImpl
 * @description 渠道联系人信息 service 接口实现类
 * @date 2017/4/25
 */
@Service
@Transactional
public class CnlChannelContactServiceImpl extends BaseServiceImpl<CnlChannelContact> implements ICnlChannelContactService{
    @Autowired
    private CnlChannelContactMapper cnlChannelContactMapper;

    /**
     * 查询
     * @param request
     * @param cnlChannelContact
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<CnlChannelContact> queryContact(IRequest request, CnlChannelContact cnlChannelContact, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return cnlChannelContactMapper.queryContact(cnlChannelContact);
    }
}