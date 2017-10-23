package clb.core.common.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.common.dto.Sequence;

/**
 * @name ISequenceService
 * @description 序列号规则配置
 * @author xianzhi.chen@hand-china.com
 */
public interface ISequenceService extends IBaseService<Sequence>, ProxySelf<ISequenceService>{
	
	/**
	 * 创建序列
	 * @param sequence
	 * @return
	 */
	Sequence createSequence(Sequence sequence);
	
	/**
	 * 更新序列
	 * @param sequence
	 * @return
	 */
	Sequence updateSequence(Sequence sequence);
	
	/**
	 * 获取序号
	 * @param sequenceCode 序列代码
	 * @return
	 */
	String getSequence(String sequenceCode);
	String getSequence(String sequenceCode,String sequencePrefix);

}