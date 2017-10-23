package clb.core.channel.service.impl;

import clb.core.channel.dto.*;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.channel.mapper.CnlContractRateMapper;
import clb.core.channel.service.ICnlContractArchiveService;
import clb.core.channel.service.ICnlContractRateHistoryService;
import clb.core.channel.service.ICnlContractRateService;
import clb.core.channel.service.ICnlContractRoleService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.channel.service.ICnlChannelContractService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author jun.zhao@hand-china.com
 * @version 1.0
 * @name CnlChannelContactServiceImpl
 * @description 渠道合约信息 service 接口实现类
 * @date 2017/4/25
 */
@Service
@Transactional
public class CnlChannelContractServiceImpl extends BaseServiceImpl<CnlChannelContract> implements ICnlChannelContractService{

    @Autowired
    private CnlChannelContractMapper cnlChannelContractMapper;
    @Autowired
    private ICnlContractRateService cnlContractRateService;
    @Autowired
    private CnlContractRateMapper cnlContractRateMapper;
    @Autowired
    private ICnlContractRateHistoryService cnlContractRateHistoryService;
    @Autowired
    private ICnlContractArchiveService cnlContractArchiveService;
    @Autowired
    private ICnlContractRoleService cnlContractRoleService;
    @Autowired
    private ISysFileService sysFileService;

    /**
     * 查询
     * @param request
     * @param cnlChannelContract
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<CnlChannelContract> queryContract(IRequest request, CnlChannelContract cnlChannelContract, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return cnlChannelContractMapper.queryContract(cnlChannelContract);
    }

    @Override
    public List<CnlChannelContract> queryContractPriv(IRequest request, CnlChannelContract cnlChannelContract, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return cnlChannelContractMapper.queryContractPriv(cnlChannelContract);
    }

    /**
     * 等级查询
     * @param request
     * @param cnlLevel
     * @return
     */
    @Override
    public List<CnlLevel> queryLevel(IRequest request, CnlLevel cnlLevel) {
        return cnlChannelContractMapper.queryLevel(cnlLevel);
    }

    /**
     * 等级查询
     * @param request
     * @param cnlPaymentTerm
     * @return
     */
    @Override
    public List<CnlPaymentTerm> queryPaymentTerm(IRequest request, CnlPaymentTerm cnlPaymentTerm) {
        return cnlChannelContractMapper.queryPaymentTerm(cnlPaymentTerm);
    }


    /**
     * 合约信息头行提交
     * @param request
     * @param cnlChannelContracts
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<CnlChannelContract> contractBatchUpdate(IRequest request, List<CnlChannelContract> cnlChannelContracts) {

        for(CnlChannelContract k:cnlChannelContracts){
            if (k.getChannelContractId() == null) {
                self().insertSelective(request, k);
            } else {
                self().updateByPrimaryKey(request, k);
            }
        }

        for(CnlChannelContract k:cnlChannelContracts){
            List<CnlContractArchive> cnlContractArchives = k.getCnlContractArchive();
            if(cnlContractArchives != null && cnlContractArchives.size() != 0){
                for(CnlContractArchive cnlContractArchive:cnlContractArchives){
                    if(cnlContractArchive.getOldFileId() != null){
                        Long fileId = cnlContractArchive.getOldFileId();
                        SysFile file = sysFileService.selectByPrimaryKey(request,fileId);
                        if (file != null) {
                            sysFileService.delete(request,file);
                        }
                    }
                    if (cnlContractArchive.getChannelContractId() == null) {
                        cnlContractArchive.setChannelContractId(k.getChannelContractId());
                        cnlContractArchive.set__status("add");
                    } else {
                        cnlContractArchive.set__status("update");
                    }

                }
                cnlContractArchiveService.batchUpdate(request, cnlContractArchives);
            }

            //当自定义费率=Y时，才需要保存费率调整记录表
            //当自定义费率=N时，需要把费率调整记录表的数据删除
            if (k.getDefineRateFlag().equals("Y")) {
                String flag = "";
                List<CnlContractRate> cnlContractRates = k.getCnlContractRate();
                if(cnlContractRates != null && cnlContractRates.size() != 0){
                    for(CnlContractRate cnlContractRate:cnlContractRates){
                        if (cnlContractRate.getChannelContractId() == null) {
                            cnlContractRate.setChannelContractId(k.getChannelContractId());
                            cnlContractRate.set__status("add");
                            if (flag.equals("")) {
                                flag = "add";
                            }
                            cnlContractRateService.insertSelective(request,cnlContractRate);
                        } else {
                            cnlContractRate.set__status("update");
                            flag = "update";
                            cnlContractRateService.updateByPrimaryKey(request,cnlContractRate);
                        }
                    }
                }
                //费率变更记录
                if (flag.equals("add") || flag.equals("update")) {
                    CnlContractRateHistory cnlContractRateHistory = new CnlContractRateHistory();
                    cnlContractRateHistory.setChannelContractId(k.getChannelContractId());
                    cnlContractRateHistory.setRateLevelName("自定义级别");
                    cnlContractRateHistoryService.insertSelective(request, cnlContractRateHistory);
                }
            } else {
                //删除记录
                CnlContractRate _cnlContractRate = new CnlContractRate();
                _cnlContractRate.setChannelContractId(k.getChannelContractId());
                cnlContractRateMapper.delete(_cnlContractRate);
                //费率变更记录
                if (!k.getRateLevelNameOld().equals(k.getRateLevelName())) {
                    CnlContractRateHistory cnlContractRateHistory = new CnlContractRateHistory();
                    cnlContractRateHistory.setChannelContractId(k.getChannelContractId());
                    cnlContractRateHistory.setRateLevelName(k.getRateLevelName());
                    cnlContractRateHistoryService.insertSelective(request, cnlContractRateHistory);
                }
            }

            List<CnlContractRole> cnlContractRoles = k.getCnlContractRoles();
            if(cnlContractRoles != null && cnlContractRoles.size() != 0){
                for(CnlContractRole cnlContractRole:cnlContractRoles){
                    if (cnlContractRole.getChannelContractId() == null) {
                        cnlContractRole.setChannelContractId(k.getChannelContractId());
                        cnlContractRole.set__status("add");
                    } else {
                        cnlContractRole.set__status("update");
                    }

                }
                cnlContractRoleService.batchUpdate(request, cnlContractRoles);
            }

        }
        return cnlChannelContracts;
    }

    /**
     * 根据条件查询渠道列表
     * @param cnlChannelContract
     * @return
     */
    public List<CnlChannelContract> selectByCondition(CnlChannelContract cnlChannelContract){
        return cnlChannelContractMapper.selectByCondition(cnlChannelContract);
    }
    /**
     * 渠道导入程序
     */
	@Override
	public List<CnlChannelContract> queryChannelContractByPartCodeAndPartyName(CnlChannelContract dto) {
		return cnlChannelContractMapper.queryChannelContractByPartCodeAndPartyName(dto);
	}

    public void updateRatioLevel(CnlChannelContract cnlChannelContract) {
        cnlChannelContractMapper.updateRatioLevel(cnlChannelContract);
    }

}