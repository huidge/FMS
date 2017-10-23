package clb.core.whtcustom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.wecorp.dto.WecorpResponseDTO;
import clb.core.whtcustom.dto.WhtCustomMenu;
import clb.core.whtcustom.mapper.WhtCustomMenuMapper;
import clb.core.whtcustom.service.IWhtCustomMenuService;

@Service
@Transactional
public class WhtCustomMenuServiceImpl extends BaseServiceImpl<WhtCustomMenu> implements IWhtCustomMenuService{
	@Autowired
    private WhtCustomMenuMapper WhtCustomMenuMapper;
	@Autowired
    private ISysFileService fileService;
	
	@Override
	public List<WhtCustomMenu> selectAll(IRequest requestContext, WhtCustomMenu WhtCustomMenu) {
		List<WhtCustomMenu> WhtCustomMenu2 = WhtCustomMenuMapper.selectAllField(WhtCustomMenu);
		/*if(WhtCustomMenu2.size()>0){
			WhtCustomMenuMapper.elete(WhtCustomMenu2);
    		List<WhtCustomMenu> menuConTentList = WhtCustomMenu.getMenuConTentList();
    		for (WhtCustomMenu whtCustomMenu3 : menuConTentList) {
    			if("Y".equals(whtCustomMenu3.getCustomerServiceFlag())){
    				whtCustomMenu3.setMenuOperation(""); 
    			}else{
    				whtCustomMenu3.setMenuOperation(whtCustomMenu3.getWhtType());
    			}
    			whtCustomMenu3.setOriginalId(WhtCustomMenu.getOriginalId());
    			WhtCustomMenu insertSelective = service.insertSelective(requestContext,whtCustomMenu3);
    			list.add(insertSelective);
			}
    		return new ResponseData(list);
    	}else{
    		List<WhtCustomMenu> menuConTentList = WhtCustomMenu.getMenuConTentList();
    		for (WhtCustomMenu whtCustomMenu3 : menuConTentList) {
    			if("Y".equals(whtCustomMenu3.getCustomerServiceFlag())){
    				whtCustomMenu3.setMenuOperation("");
    			}else{
    				whtCustomMenu3.setMenuOperation(whtCustomMenu3.getWhtType());
    			}
    			whtCustomMenu3.setOriginalId(WhtCustomMenu.getOriginalId());
    			WhtCustomMenu insertSelective = service.insertSelective(requestContext,whtCustomMenu3);
    			list.add(insertSelective);
			}
    		return new ResponseData(list);
    	}*/
		
		/*if(WhtCustomMenu2.size()>0){
			for (WhtCustomMenu whtCustomMenu3 : WhtCustomMenu2) {
				if (whtCustomMenu3.getPictureTextId() != null) {
	                SysFile sysFile = fileService.selectByPrimaryKey(requestContext, whtCustomMenu3.getPictureTextId());
	                if (sysFile != null) {
	                	whtCustomMenu3.setPictureTextIdPath(sysFile.getFilePath());
	                }
	            }
				if (whtCustomMenu3.getPictureId() != null) {
					SysFile sysFile = fileService.selectByPrimaryKey(requestContext, whtCustomMenu3.getPictureId());
					if (sysFile != null) {
						whtCustomMenu3.setPictureIdPath(sysFile.getFilePath());
					}
				}
			}
		}*/
		return WhtCustomMenu2;
	}

	@Override
	public WecorpResponseDTO menuClick(String appId , String key) throws Exception {
		WecorpResponseDTO wecorpResponseDTO = new WecorpResponseDTO();
		WhtCustomMenu menuClick = null;
		if(!"".equals(key) && key != null){
			menuClick = WhtCustomMenuMapper.menuClick(key);
		}else{
			wecorpResponseDTO.setContent("请在财联邦系统配置菜单");
			wecorpResponseDTO.setType("text");
		}
		if(menuClick == null){
			wecorpResponseDTO.setContent("请在财联邦系统配置菜单");
			wecorpResponseDTO.setType("text");
		}else{
			if("text".equals(menuClick.getMenuContentType())){
				wecorpResponseDTO.setType("text");
				wecorpResponseDTO.setContent(menuClick.getText());
			}else if("image".equals(menuClick.getMenuContentType())){
				wecorpResponseDTO.setType("image");
				wecorpResponseDTO.setMediaId(menuClick.getPictureId());
			}else if("news".equals(menuClick.getMenuContentType())){
				wecorpResponseDTO.setType("news");
				wecorpResponseDTO.setMediaId(menuClick.getPictureTextId());
			}else if("voice".equals(menuClick.getMenuContentType())){
				wecorpResponseDTO.setType("voice");
				wecorpResponseDTO.setMediaId(menuClick.getVoiceId());
			}else if("video".equals(menuClick.getMenuContentType())){
				wecorpResponseDTO.setType("video");
				wecorpResponseDTO.setMediaId(menuClick.getVideoId());
			}else{
				wecorpResponseDTO.setContent("该菜单暂无任何操作");
				wecorpResponseDTO.setType("text");
			}
		}
		return wecorpResponseDTO;
	}
}