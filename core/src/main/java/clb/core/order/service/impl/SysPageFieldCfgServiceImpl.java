package clb.core.order.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.order.dto.MenuAuth;
import clb.core.order.dto.SysPageFieldCfg;
import clb.core.order.mapper.SysPageFieldCfgMapper;
import clb.core.order.service.ISysPageFieldCfgService;

@Service
@Transactional
public class SysPageFieldCfgServiceImpl extends BaseServiceImpl<SysPageFieldCfg> implements ISysPageFieldCfgService{

	@Autowired
	private SysPageFieldCfgMapper sysPageFieldCfgMapper;
	
	@Override
	public List<SysPageFieldCfg> selectSettingInfo(IRequest requestContext, SysPageFieldCfg dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return sysPageFieldCfgMapper.selectSettingInfo(dto);
	}
	
	
	@Override
	public List<MenuAuth> accessMenuAuthList(String roleCode) {
		
		List<MenuAuth> MenuAuthList = new ArrayList<MenuAuth>();
		 
		//通过id查询所有一级菜单信息
		List<SysPageFieldCfg> sysPageFieldCfgOneList= sysPageFieldCfgMapper.queryByRoleCode(roleCode);
		
		//循环一级目录
		for (SysPageFieldCfg sysPageFieldCfg : sysPageFieldCfgOneList) {
			//新建一个菜单
			MenuAuth menuOneAuth = new MenuAuth();
			//设置一级目录名称和状态
			menuOneAuth.setId(sysPageFieldCfg.getLineId());
			menuOneAuth.setMeaning(sysPageFieldCfg.getMeaning());
			menuOneAuth.setQueryFlag(sysPageFieldCfg.getQueryFlag());
			menuOneAuth.setUpdateFlag(sysPageFieldCfg.getUpdateFlag());
			List<MenuAuth> menOneList = new ArrayList<MenuAuth>();
			//查询二级目录
			List<SysPageFieldCfg> sysPageFieldCfgTwoList = sysPageFieldCfgMapper.queryByParentLineId(menuOneAuth.getId());
			
			//循环二级目录信息
			for (SysPageFieldCfg sysPageFieldCfg2 : sysPageFieldCfgTwoList) {
				
				//设置二级目录的名称和状态
				MenuAuth menuTwoAuth = new MenuAuth();
				menuTwoAuth.setParentMeaning(sysPageFieldCfg2.getParentMeaning());;
				menuTwoAuth.setParentId(menuOneAuth.getId());
				menuTwoAuth.setMeaning(sysPageFieldCfg2.getMeaning());
				menuTwoAuth.setId(sysPageFieldCfg2.getLineId());
				menuTwoAuth.setQueryFlag(sysPageFieldCfg2.getQueryFlag());
				menuTwoAuth.setUpdateFlag(sysPageFieldCfg2.getUpdateFlag());
				List<MenuAuth> menTwoList = new ArrayList<MenuAuth>();
				//查询三级目录
				List<SysPageFieldCfg> sysPageFieldCfgThreeList =  sysPageFieldCfgMapper.queryByParentLineId(menuTwoAuth.getId());
				
				// 循环三级目录
				for (SysPageFieldCfg sysPageFieldCfg3 : sysPageFieldCfgThreeList) {
					MenuAuth menuThreeAuth = new MenuAuth();
					menuThreeAuth.setId(sysPageFieldCfg3.getLineId());
					menuThreeAuth.setMeaning(sysPageFieldCfg3.getMeaning());;
					menuThreeAuth.setParentMeaning(sysPageFieldCfg3.getParentMeaning());
					menuThreeAuth.setParentId(menuTwoAuth.getId());
					menuThreeAuth.setQueryFlag(sysPageFieldCfg3.getQueryFlag());
					menuThreeAuth.setUpdateFlag(sysPageFieldCfg3.getUpdateFlag());
					menTwoList.add(menuThreeAuth);
				}
				menuTwoAuth.setChildren(menTwoList);
				menOneList.add(menuTwoAuth);
			}
			menuOneAuth.setChildren(menOneList);
			MenuAuthList.add(menuOneAuth);
		}
		return MenuAuthList;
	}


	@Override
	public int updateFlag(SysPageFieldCfg sysPageFieldCfg) {
		return sysPageFieldCfgMapper.updateFlag(sysPageFieldCfg);
	}

}