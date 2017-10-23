package clb.core.whtcustom.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.whtcustom.dto.WhtOfficialAccountCfg;

public interface WhtOfficialAccountCfgMapper extends Mapper<WhtOfficialAccountCfg>{
	
	List<WhtOfficialAccountCfg> selectAllField(WhtOfficialAccountCfg whtOfficialAccountCfg);
	
	List<WhtOfficialAccountCfg> selectAccountName(WhtOfficialAccountCfg whtOfficialAccountCfg);

	WhtOfficialAccountCfg selectAppId(WhtOfficialAccountCfg whtOfficialAccountCfg);
}