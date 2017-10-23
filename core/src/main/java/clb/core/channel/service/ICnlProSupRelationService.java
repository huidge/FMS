package clb.core.channel.service;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.channel.dto.CnlProSupRelation;


public interface ICnlProSupRelationService extends IBaseService<CnlProSupRelation>, ProxySelf<ICnlProSupRelationService>{

	/**
	 * 获取渠道供应商产品关系信息
	 * @param requestContext
	 * @param cnlProSupRelation
	 * @param page
	 * @param pagesize
	 * @return
	 */
	List<CnlProSupRelation> selectByCondition(IRequest requestContext, CnlProSupRelation cnlProSupRelation ,int page, int pagesize);
 	
	/**
	 * 导入数据解析插入
	 * @param wb
	 * @param iRequest
	 * @throws Exception
	 */
	void insertAllValue(Workbook wb,IRequest iRequest) throws Exception ;
	
	/**
	 * 判断去重逻辑
	 * @param cnlProSupRelation
	 * @return
	 */
	int selectByAllInfo(CnlProSupRelation cnlProSupRelation);

	List<CnlProSupRelation> selectByConditionNull(IRequest requestContext, CnlProSupRelation cnlProSupRelation ,int page, int pagesize);
}
