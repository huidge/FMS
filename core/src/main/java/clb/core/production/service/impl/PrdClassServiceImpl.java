package clb.core.production.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.order.dto.MenuAuth;
import clb.core.prc.constants.Constants;
import clb.core.production.dto.PrdClass;
import clb.core.production.mapper.PrdClassMapper;
import clb.core.production.service.IPrdClassService;

/**
 * Created by wanjun.feng on 17/4/12.
 */
@Service
@Transactional
public class PrdClassServiceImpl extends BaseServiceImpl<PrdClass> implements IPrdClassService {
    
    @Autowired
    private PrdClassMapper prdClassMapper;

    @Override
    public List<PrdClass> query(IRequest iRequest, PrdClass prdClass, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return prdClassMapper.select(prdClass);
    }
}