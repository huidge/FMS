package clb.core.order.service.impl;

import clb.core.order.mapper.OrdTeamVisitorMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdTeamVisitor;
import clb.core.order.service.IOrdTeamVisitorService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdTeamVisitorServiceImpl extends BaseServiceImpl<OrdTeamVisitor> implements IOrdTeamVisitorService{

    @Autowired
    private OrdTeamVisitorMapper ordTeamVisitorMapper;
    /**
     * 接口查询L团签旅客信息
     * jun.zhao02@hand-china.com
     *
     * @param request
     * @param ordTeamVisitor
     * @return
     */
    @Override
    public List<OrdTeamVisitor> queryWsOrdTeamVisitor(IRequest request, OrdTeamVisitor ordTeamVisitor) {
        return ordTeamVisitorMapper.queryOrdTeamVisitor(ordTeamVisitor);
    }
}