/*
 * #{copyright}#
 */
package clb.core.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.Profile;

import clb.core.system.mapper.ClbCommonMapper;
import clb.core.system.service.IClbCommonService;

/**
 * @author tiansheng.ye@hand-china.com
 */
@Service
@Transactional
public class ClbCommonServiceImpl implements IClbCommonService {
    @Autowired
    private ClbCommonMapper clbCommonMapper;

	@Override
	public List<Profile> queryProfiles(IRequest request, int page, int pagesize,Profile profile) {
		PageHelper.startPage(page, pagesize);
        return clbCommonMapper.queryProfiles(profile);
	}

    
}