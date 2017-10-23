package clb.core.pln.service.impl;

import clb.core.pln.mapper.PlnPlanSpiderUserpassMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.pln.dto.PlnPlanSpiderUserpass;
import clb.core.pln.service.IPlnPlanSpiderUserpassService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlnPlanSpiderUserpassServiceImpl extends BaseServiceImpl<PlnPlanSpiderUserpass> implements IPlnPlanSpiderUserpassService{


    @Autowired
    private PlnPlanSpiderUserpassMapper plnPlanSpiderUserpassMapper;

    /**
     * 查询数据
     * created by gan on 2017/8/23
     * lin.gan@hand-china.com
     */
    @Override
    public List<PlnPlanSpiderUserpass> findAll(IRequest request, PlnPlanSpiderUserpass plnPlanSpiderUserpass, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return plnPlanSpiderUserpassMapper.findAll(plnPlanSpiderUserpass);
    }

}