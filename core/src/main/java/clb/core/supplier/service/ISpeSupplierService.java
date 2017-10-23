package clb.core.supplier.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.supplier.dto.SpeSupplier;

public interface ISpeSupplierService extends IBaseService<SpeSupplier>, ProxySelf<ISpeSupplierService>{
	
	/**
     *@description 更新供应商信息
     *@return List 供应商流水号
	 * @throws CommonException 
     */
	public List<SpeSupplier> supplierBatchUpdate(IRequest request,List<SpeSupplier> data) throws CommonException;
	
	/**
	 * 查询供应商数据 
	 */	
	public List<SpeSupplier> selectData(SpeSupplier dto,int page,int pagesize);
	
	/**
	 * 查询所有记录
	 * @param dto
	 * @return
	 */
	public List<SpeSupplier> selectAllData(SpeSupplier dto);
	
	/**
     * 查询只包含主险记录
     * @param dto
     * @return
     */
    public List<SpeSupplier> selectDataWithItems(SpeSupplier dto);


	/**
	 * 根据渠道查询产品公司
	 * @param dto
	 * @return
	 */
	public List<SpeSupplier> selectDataByChannel(SpeSupplier dto);
	
	/**
	 * 根据供应商名称查询
	 * @param dto
	 * @return
	 */
	public List<SpeSupplier> selectByName(SpeSupplier dto);
	
	/**
	 * 根据序号排序
	 * @param dto
	 * @return
	 */
	public List<SpeSupplier> selectByNameAndSort(SpeSupplier dto,int page,int pagesize);
}