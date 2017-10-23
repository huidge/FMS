package clb.core.sys.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.sys.dto.SysSeoManage;
import clb.core.sys.mapper.SysSeoManageMapper;
import clb.core.sys.service.ISysSeoManageService;

/*****
 * @author FengWanJun
 * @Date 2017-09-06
 */
@Service
@Transactional
public class SysSeoManageServiceImpl extends BaseServiceImpl<SysSeoManage> implements ISysSeoManageService {
    @Autowired
    private SysSeoManageMapper sysSeoManageMapper;
}