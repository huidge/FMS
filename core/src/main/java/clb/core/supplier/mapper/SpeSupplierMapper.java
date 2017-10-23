package clb.core.supplier.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.supplier.dto.SpeSupplier;

/**
 * @name SpeSupplierMapper
 * @description 供应商Mapper
 * @author bo.wu@hand-china.com 2017年4月18日19:36:25
 * @version 1.0 
 */
public interface SpeSupplierMapper extends Mapper<SpeSupplier>{
	
	/**
     *@description 查询当前最大的供应商流水号
     *@return String 供应商流水号
     */
	public String selectMaxSupplierCode();
	
	public List<SpeSupplier> selectData(SpeSupplier dto);
	//查询只包含主险记录
    public List<SpeSupplier> selectDataWithItems(SpeSupplier dto);

	public List<SpeSupplier> selectDataByChannel(SpeSupplier dto);

	public List<SpeSupplier> selectByName(SpeSupplier dto); 
	
	public List<SpeSupplier> selectByNameAndSort(SpeSupplier dto);
	
}