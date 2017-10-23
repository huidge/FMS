package clb.core.fnd.service;

import java.util.Map;
/**
 * @name IImportExecute
 * @description 导入验证接口
 * @author xiang.ding@hand-china.com	2017年3月20日下午4:12:45
 * @version 1.0
 */
public interface IImportExecute {
	/**
	 * @Description: 导入验证执行方法
	 * @param args
	 * @throws Exception void
	 */
	void execute(Map<String, Object> args) throws Exception;
}
