package clb.core.production.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.production.dto.PrdClassSet;
import clb.core.production.mapper.PrdClassSetMapper;
import clb.core.production.service.IPrdClassSetService;

/**
 * Created by wanjun.feng on 17/4/12.
 */
@Service
@Transactional
public class PrdClassSetServiceImpl extends BaseServiceImpl<PrdClassSet> implements IPrdClassSetService {
    
    @Autowired
    private PrdClassSetMapper prdClassSetMapper;
    
    @Override
    public List<PrdClassSet> query(IRequest iRequest, PrdClassSet prdClassSet, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return prdClassSetMapper.selectClassSets(prdClassSet);
    }
}