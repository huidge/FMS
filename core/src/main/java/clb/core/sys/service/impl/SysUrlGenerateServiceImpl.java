package clb.core.sys.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.sys.dto.SysUrlGenerate;
import clb.core.sys.service.ISysUrlGenerateService;

@Service
@Transactional
public class SysUrlGenerateServiceImpl extends BaseServiceImpl<SysUrlGenerate> implements ISysUrlGenerateService{

}