package clb.core.commission.service.impl;
/**
 * Created by wanjun.feng on 2017-09-11.
 */

import clb.core.commission.dto.CmnChannelRatio;
import clb.core.commission.mapper.CmnChannelRatioMapper;
import clb.core.commission.service.ICmnChannelRatioService;

import com.github.pagehelper.PageHelper;
import com.hand.hap.account.dto.Role;
import com.hand.hap.account.service.IRoleService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CmnChannelRatioServiceImpl extends BaseServiceImpl<CmnChannelRatio> implements ICmnChannelRatioService {

    @Autowired
    private CmnChannelRatioMapper cmnChannelRatioMapper;
    @Autowired
    private IRoleService roleService;

    @Override
    public List<CmnChannelRatio> selectChannelRatios(IRequest requestContext, CmnChannelRatio cmnChannelRatio, int page,
            int pageSize) {
        Role role = new Role();
        role.setRoleId(requestContext.getRoleId());
        role = roleService.selectByPrimaryKey(requestContext, role);
        cmnChannelRatio.setRoleCode(role.getRoleCode());
        cmnChannelRatio.setUserId(requestContext.getUserId());
        PageHelper.startPage(page, pageSize);
        List<CmnChannelRatio> list = new ArrayList<CmnChannelRatio>();
        list = cmnChannelRatioMapper.selectChannelRatios(cmnChannelRatio);
        return list;
    }

    public List<CmnChannelRatio> selectByChannelIdAndRatioName(CmnChannelRatio cmnChannelRatio) {
        return cmnChannelRatioMapper.selectByChannelIdAndRatioName(cmnChannelRatio);
    }

    @Override
    public List<CmnChannelRatio> wsSelectChannelRatios(IRequest requestContext, CmnChannelRatio cmnChannelRatio,
            int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<CmnChannelRatio> list = new ArrayList<CmnChannelRatio>();
        list = cmnChannelRatioMapper.wsSelectChannelRatios(cmnChannelRatio);
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseData batchSubmit(IRequest requestContext, List<CmnChannelRatio> cmnChannelRatioList) {
        ResponseData response;
        try {
            for (CmnChannelRatio cmnChannelRatio : cmnChannelRatioList) {
                CmnChannelRatio _cmnChannelRatio = new CmnChannelRatio();
                _cmnChannelRatio.setChannelId(cmnChannelRatio.getChannelId());
                _cmnChannelRatio.setRatioName(cmnChannelRatio.getRatioName());
                if (cmnChannelRatio.get__status().equals("update")) {
                    List<CmnChannelRatio> _cmnChannelRatioList = cmnChannelRatioMapper.select(_cmnChannelRatio);
                    if (_cmnChannelRatioList != null) {
                        if (_cmnChannelRatioList.size() == 1) {
                            if (!_cmnChannelRatioList.get(0).getRatioId().equals(cmnChannelRatio.getRatioId())) {
                                response = new ResponseData(false);
                                response.setMessage("不能设置相同的自定义级别！");
                                return response;
                            }
                        } else if (_cmnChannelRatioList.size() > 1) {
                            response = new ResponseData(false);
                            response.setMessage("不能设置相同的自定义级别！");
                            return response;
                        }
                    }
                    cmnChannelRatio = self().updateByPrimaryKey(requestContext, cmnChannelRatio);
                } else if (cmnChannelRatio.get__status().equals("add")) {
                    List<CmnChannelRatio> _cmnChannelRatioList = cmnChannelRatioMapper.select(_cmnChannelRatio);
                    if (_cmnChannelRatioList != null && _cmnChannelRatioList.size() > 0) {
                        response = new ResponseData(false);
                        response.setMessage("不能设置相同的自定义级别！");
                        return response;
                    }
                    cmnChannelRatio = self().insertSelective(requestContext, cmnChannelRatio);
                }
            }
        } catch(Exception e) {
            response = new ResponseData(false);
            response.setMessage(e.getMessage());
            return response;
        }
        response = new ResponseData(cmnChannelRatioList);
        return response;
    }

}