package clb.core.fnd.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.fnd.dto.ImportTemp;

/**
 * 
 * @name HssSetupImportTempMapper
 * @author zili.wang@hand-china.com	2016年12月22日下午3:02:09
 * @description 通用导入
 * @version 
 */
public interface ImportTempMapper extends Mapper<ImportTemp> {

	/**
	 * @description 查询当前接口错误数据
	 * @param importBatchId
	 * @return 错误数据
	 * @author zili.wang@hand-china.com	2016年12月22日下午3:02:09
	 * @version 1.0
	 */
	public List<ImportTemp> selectErrorMessage(Long importBatchId);

	/**
	 * @description 查询所有导入数据
	 * @param importBatchId
	 * @return 错误数据
	 * @author zili.wang@hand-china.com	2016年12月22日下午3:02:09
	 * @version 1.0
	 */
	public List<ImportTemp> selectImportData(Long importBatchId);

	/**
	 * @description 查询当前接口错误数据
	 * @param importBatchId
	 * @return 错误个数
	 * @author zili.wang@hand-china.com	2016年12月22日下午3:02:09
	 * @version 1.0
	 */
	public Long selectErrorCount(Long importBatchId);

	/**
	 * @Description: 根据所得的流水号验证是否已经存在  
	 * @param importBatchId
	 * @return int
	 */
	public int selectImportBactIdIsExist(Long importBatchId);

	/**
	 * @Description: 清空临时表
	 */
	public void deleteAll();

	public int selectCountNeedDelete();
	
}