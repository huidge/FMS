package clb.core.sys.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.sys.dto.SysCommonFile;

public interface SysCommonFileMapper extends Mapper<SysCommonFile>{

	/**
	 * 查询所有文件信息
	 * @return
	 */
	public List<SysCommonFile> selectSysCommonFileInfo(SysCommonFile sysCommonFile);
}