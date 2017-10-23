package clb.core.supplier.service.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.supplier.dto.SpeArchives;
import clb.core.supplier.dto.SpeContract;
import clb.core.supplier.dto.SpeSupplierSettle;
import clb.core.supplier.service.ISpeArchivesService;
import clb.core.supplier.service.ISpeContractService;
import clb.core.supplier.service.ISpeSupplierSettleService;
import clb.core.system.dto.ClbCode;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.mapper.ClbCodeMapper;
import clb.core.system.mapper.ClbCodeValueMapper;
import clb.core.system.service.IClbCodeService;

@Service
@Transactional
public class SpeContractServiceImpl extends BaseServiceImpl<SpeContract> implements ISpeContractService{

	@Autowired
	private ISysFileService fileService;
	
	@Autowired
	private ISpeArchivesService speArchivesService;
	
	@Autowired
	private ISpeSupplierSettleService settleService;
	

	@Override
	public List<SpeContract> supplierContractBatchUpdate(IRequest request, List<SpeContract> data) {
		for(SpeContract d:data){
			if(d.get__status().equals(DTOStatus.ADD)){
				self().insertSelective(request,d);
			}else if(d.get__status().equals(DTOStatus.UPDATE)){
				self().updateByPrimaryKeySelective(request, d);
			}
			//提取附件信息
			List<SpeArchives> archiveses = d.getAttachData();
			if(archiveses != null && archiveses.size() != 0){
				for(SpeArchives archives:archiveses){
					if(archives.getOldFileId() != null){
						Long fileId = archives.getOldFileId();
						SysFile file = fileService.selectByPrimaryKey(request,fileId);
						fileService.delete(request,file);
					}
					if(archives.get__status().equals(DTOStatus.ADD)){
						archives.setContractId(d.getContractId());
					}
				}
				speArchivesService.batchUpdate(request, archiveses);
			}
			//提取结算信息
			List<SpeSupplierSettle> settles = d.getSettleData();
			if(settles != null && settles.size() != 0){
				for(SpeSupplierSettle settle:settles){
					if(settle.get__status().equals(DTOStatus.ADD)){
						settle.setContractId(d.getContractId());
					}
					
				}
				settleService.batchUpdate(request,settles);
			}
		}
		return data;
	}

}