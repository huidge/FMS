package clb.core.whtcustom.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.wecorp.dto.HmsWxResponseDto;
import clb.core.wecorp.dto.WecorpAccountMenu;
import clb.core.wecorp.dto.WecorpResponseDTO;
import clb.core.wecorp.service.IWecorpAccountMenuService;
import clb.core.whtcustom.dto.Button;
import clb.core.whtcustom.dto.CommonButton;
import clb.core.whtcustom.dto.ComplexButton;
import clb.core.whtcustom.dto.Menu;
import clb.core.whtcustom.dto.WhtCustomMenu;
import clb.core.whtcustom.dto.WhtCustomReply;
import clb.core.whtcustom.service.IWhtCustomMenuService;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

    @Controller
    public class WhtCustomMenuController extends BaseController{

    @Autowired
    private IWhtCustomMenuService service;
    
    @Autowired
    private IWecorpAccountMenuService wecorpAccountMenuService;


    @RequestMapping(value = "/fms/wht/custom/menu/query")
    @ResponseBody
    public ResponseData query(WhtCustomMenu dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
    /*@RequestMapping(value = "/fms/wht/custom/menu/menuClick")
    @ResponseBody
    public ResponseData menuClick(HttpServletRequest request,String appId , String key) {
    	IRequest requestContext = createRequestContext(request);
    	WecorpResponseDTO subscribeReplyMsg = service.menuClick(requestContext,appId,key);
    	System.out.println(subscribeReplyMsg.toString());
    	return new ResponseData();
    }*/
    
    @RequestMapping(value = "/fms/wht/custom/menu/selectAll")
    @ResponseBody
    public ResponseData selectAll(WhtCustomMenu dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.selectAll(requestContext,dto));
    }
    
    @RequestMapping(value = "/fms/wht/custom/menu/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<WhtCustomMenu> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value="/fms/wht/custom/menu/addMenu")
    @ResponseBody
    public ResponseData addMenu(HttpServletRequest request, @RequestBody List<WhtCustomMenu> obj){
    	IRequest requestContext = createRequestContext(request);
    	ResponseData responseData = new ResponseData();
    	WhtCustomMenu WhtCustomMenu = obj.get(0);
    	String menuContent = WhtCustomMenu.getMenuContent();
		WecorpAccountMenu wecorpAccountMenu = new WecorpAccountMenu();
        wecorpAccountMenu.setAccountNum(WhtCustomMenu.getOriginalId());
        wecorpAccountMenu.setContent(menuContent);
        HmsWxResponseDto createMenu = null;
		try {
			createMenu = wecorpAccountMenuService.createMenu(wecorpAccountMenu);
			if(createMenu.getErrcode()=="0"){
				WhtCustomMenu WhtCustomMenu2 = new WhtCustomMenu();
		    	WhtCustomMenu2.setOriginalId(WhtCustomMenu.getOriginalId());
		    	List<WhtCustomMenu> selectAll = service.selectAll(requestContext,WhtCustomMenu2);
		    	//return new ResponseData(selectAll);
		    	List<WhtCustomMenu> list = new ArrayList<>();
		    	if(selectAll.size()>0){
		    		service.batchDelete(selectAll);
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
		    	}
			}else{
				responseData.setSuccess(false);
				responseData.setMessage(createMenu.getErrmsg());
				return responseData;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseData;
    	
    	/*//菜单类型分组
    	List<WhtCustomMenu> whtMenuConTentList = WhtCustomMenu.getMenuConTentList();
    	List<WhtCustomMenu> WhtCustomMenuList1 = new ArrayList<>();
    	List<WhtCustomMenu> WhtCustomBigMenuList1 = new ArrayList<>();
    	List<WhtCustomMenu> WhtCustomMenuList2 = new ArrayList<>();
    	List<WhtCustomMenu> WhtCustomBigMenuList2 = new ArrayList<>();
    	List<WhtCustomMenu> WhtCustomMenuList3 = new ArrayList<>();
    	List<WhtCustomMenu> WhtCustomBigMenuList3 = new ArrayList<>();
    	for (WhtCustomMenu whtCustomMenuTemp : whtMenuConTentList) {
			if(whtCustomMenuTemp.getParentMenuId() == 1){
				System.out.println(whtCustomMenuTemp.getMenuType());
				System.out.println(whtCustomMenuTemp.getMenuType().getClass());
				if("firstOrder".equals(whtCustomMenuTemp.getMenuType())){
					WhtCustomBigMenuList1.add(whtCustomMenuTemp);
				}else{
					WhtCustomMenuList1.add(whtCustomMenuTemp);
				}
			}else if(whtCustomMenuTemp.getParentMenuId() == 2){
				if("firstOrder".equals(whtCustomMenuTemp.getMenuType())){
					WhtCustomBigMenuList2.add(whtCustomMenuTemp);
				}else{
					WhtCustomMenuList2.add(whtCustomMenuTemp);
				}
			}else{
				if("firstOrder".equals(whtCustomMenuTemp.getMenuType())){
					WhtCustomBigMenuList3.add(whtCustomMenuTemp);
				}else{
					WhtCustomMenuList3.add(whtCustomMenuTemp);
				}
			}
		};
		
		//没有子菜单的一级菜单
		CommonButton btn10 = new CommonButton();  
		//子菜单集合
		int size1 = WhtCustomMenuList1.size();
		CommonButton[] CommonButton1 = new CommonButton[size1];
		//复杂菜单
		ComplexButton mainBtn1 = new ComplexButton();
		if(WhtCustomMenuList1.size()>0){
			for(int i = 0;i < WhtCustomMenuList1.size(); i ++){
				String whtType = WhtCustomMenuList1.get(i).getWhtType();
				CommonButton btn1 = new CommonButton();  
				btn1.setName(WhtCustomMenuList1.get(i).getMenuName());
				btn1.setType(whtType);
				if("click".equals(whtType)){
					String key = UUID.randomUUID().toString();
					btn1.setKey(key);
					CommonButton1[i] = btn1;
					for (WhtCustomMenu whtCustomMenuTemp : whtMenuConTentList) {
						if((whtCustomMenuTemp.getMenuType()).equals(WhtCustomMenuList1.get(i).getMenuType()) 
								&& whtCustomMenuTemp.getParentMenuId()==WhtCustomMenuList1.get(i).getParentMenuId() 
								&& whtCustomMenuTemp.getRowNumber()==WhtCustomMenuList1.get(i).getRowNumber()){
							whtCustomMenuTemp.setMenuOperation(WhtCustomMenuList1.get(i).getWhtType());
							whtCustomMenuTemp.setMenuKey(key);
							break;
						}
					}
				}else if("view".equals(whtType)){
					btn1.setUrl(WhtCustomMenuList1.get(i).getPageAddress());
				}
			}
			for(int i = 0;i < WhtCustomBigMenuList1.size(); i ++){
				mainBtn1.setName(WhtCustomBigMenuList1.get(i).getMenuName());
				mainBtn1.setSub_button(CommonButton1);
			}
		}else if(WhtCustomBigMenuList1.size()>0 && WhtCustomMenuList1.size()==0){
			for(int i = 0;i < WhtCustomBigMenuList1.size(); i ++){
				btn10.setName(WhtCustomBigMenuList1.get(i).getMenuName()); 
				String whtType = WhtCustomBigMenuList1.get(i).getWhtType();
				btn10.setType(whtType);  
				if("click".equals(whtType)){
					String key = UUID.randomUUID().toString();
					btn10.setKey(key);
					for (WhtCustomMenu whtCustomMenuTemp : whtMenuConTentList) {
						if((whtCustomMenuTemp.getMenuType()).equals(WhtCustomBigMenuList1.get(i).getMenuType()) && whtCustomMenuTemp.getParentMenuId()==WhtCustomBigMenuList1.get(i).getParentMenuId()&& whtCustomMenuTemp.getRowNumber()==WhtCustomBigMenuList1.get(i).getRowNumber()){
							whtCustomMenuTemp.setMenuOperation(WhtCustomBigMenuList1.get(i).getWhtType());
							whtCustomMenuTemp.setMenuKey(key);
							break;
						}
					}
				}else if("view".equals(whtType)){
					btn10.setUrl(WhtCustomBigMenuList1.get(i).getPageAddress());
				}
			}
		}
		System.out.println(mainBtn1.toString());
		
		//没有子菜单的一级菜单
		CommonButton btn20 = new CommonButton();  
		//子菜单集合
		int size2 = WhtCustomMenuList2.size();
		CommonButton[] CommonButton2 = new CommonButton[size2];
		//复杂菜单
		ComplexButton mainBtn2 = new ComplexButton();
		if(WhtCustomMenuList2.size()>0){
			for(int i = 0;i < WhtCustomMenuList2.size(); i ++){
				CommonButton btn2 = new CommonButton();  
				String menuName = WhtCustomMenuList2.get(i).getMenuName();
				btn2.setName(menuName);
				String whtType = WhtCustomMenuList2.get(i).getWhtType();
				btn2.setType(whtType);
				if("click".equals(whtType)){
					String key = UUID.randomUUID().toString();
					btn2.setKey(key);
					CommonButton2[i] = btn2;
					for (WhtCustomMenu whtCustomMenuTemp : whtMenuConTentList) {
						if((whtCustomMenuTemp.getMenuType()).equals(WhtCustomMenuList2.get(i).getMenuType()) && whtCustomMenuTemp.getParentMenuId()==WhtCustomMenuList2.get(i).getParentMenuId()&& whtCustomMenuTemp.getRowNumber()==WhtCustomMenuList2.get(i).getRowNumber()){
							whtCustomMenuTemp.setMenuOperation(WhtCustomMenuList2.get(i).getWhtType());
							whtCustomMenuTemp.setMenuKey(key);
							break;
						}
					}
				}else if("view".equals(whtType)){
					btn2.setUrl(WhtCustomMenuList2.get(i).getPageAddress());
				}
			}
			for(int i = 0;i < WhtCustomBigMenuList2.size(); i ++){
				mainBtn2.setName(WhtCustomBigMenuList2.get(i).getMenuName());
				mainBtn2.setSub_button(CommonButton2);
			}
		}else if(WhtCustomBigMenuList2.size()>0 && WhtCustomMenuList2.size()==0){
			for(int i = 0;i < WhtCustomBigMenuList2.size(); i ++){
				btn20.setName(WhtCustomBigMenuList2.get(i).getMenuName());
				String whtType = WhtCustomBigMenuList2.get(i).getWhtType();
				btn20.setType(whtType); 
				if("click".equals(whtType)){
					String key = UUID.randomUUID().toString();
					btn20.setKey(key);
					for (WhtCustomMenu whtCustomMenuTemp : whtMenuConTentList) {
						if((whtCustomMenuTemp.getMenuType()).equals(WhtCustomBigMenuList2.get(i).getMenuType()) && whtCustomMenuTemp.getParentMenuId()==WhtCustomBigMenuList2.get(i).getParentMenuId()&& whtCustomMenuTemp.getRowNumber()==WhtCustomBigMenuList2.get(i).getRowNumber()){
							whtCustomMenuTemp.setMenuOperation(WhtCustomBigMenuList2.get(i).getWhtType());
							whtCustomMenuTemp.setMenuKey(key);
							break;
						}
					}
				}else if("view".equals(whtType)){
					btn20.setUrl(WhtCustomBigMenuList2.get(i).getPageAddress());
				}
			}
		}
		System.out.println(mainBtn2.toString());
		
		//没有子菜单的一级菜单
		CommonButton btn30 = new CommonButton();  
		//子菜单集合
		int size3 = WhtCustomMenuList3.size();
		CommonButton[] CommonButton3 = new CommonButton[size3];
		//复杂菜单
		ComplexButton mainBtn3 = new ComplexButton();
		if(WhtCustomMenuList3.size()>0){
			for(int i = 0;i < WhtCustomMenuList3.size(); i ++){
				CommonButton btn3 = new CommonButton();  
				btn3.setName(WhtCustomMenuList3.get(i).getMenuName());
				String whtType = WhtCustomMenuList3.get(i).getWhtType();
				btn3.setType(whtType);
				if("click".equals(whtType)){
					String key = UUID.randomUUID().toString();
					btn3.setKey(key);
					CommonButton3[i] = btn3;
					for (WhtCustomMenu whtCustomMenuTemp : whtMenuConTentList) {
						if((whtCustomMenuTemp.getMenuType()).equals(WhtCustomMenuList3.get(i).getMenuType()) && whtCustomMenuTemp.getParentMenuId()==WhtCustomMenuList3.get(i).getParentMenuId()&& whtCustomMenuTemp.getRowNumber()==WhtCustomMenuList3.get(i).getRowNumber()){
							whtCustomMenuTemp.setMenuOperation(WhtCustomMenuList3.get(i).getWhtType());
							whtCustomMenuTemp.setMenuKey(key);
							break;
						}
					}
				}else if("view".equals(whtType)){
					btn3.setUrl(WhtCustomMenuList3.get(i).getPageAddress());
				}
			}
			for(int i = 0;i < WhtCustomBigMenuList3.size(); i ++){
				mainBtn3.setName(WhtCustomBigMenuList3.get(i).getMenuName());
				mainBtn3.setSub_button(CommonButton3);
			}
		}else if(WhtCustomBigMenuList3.size()>0 && WhtCustomMenuList3.size()==0){
			for(int i = 0;i < WhtCustomBigMenuList3.size(); i ++){
				btn30.setName(WhtCustomBigMenuList3.get(i).getMenuName()); 
				String whtType = WhtCustomBigMenuList3.get(i).getWhtType();
				btn30.setType(whtType);
				if("click".equals(whtType)){
					String key = UUID.randomUUID().toString();
					btn30.setKey(key);
					for (WhtCustomMenu whtCustomMenuTemp : whtMenuConTentList) {
						if((whtCustomMenuTemp.getMenuType()).equals(WhtCustomBigMenuList3.get(i).getMenuType()) && whtCustomMenuTemp.getParentMenuId()==WhtCustomBigMenuList3.get(i).getParentMenuId()&& whtCustomMenuTemp.getRowNumber()==WhtCustomBigMenuList3.get(i).getRowNumber()){
							whtCustomMenuTemp.setMenuOperation(WhtCustomBigMenuList3.get(i).getWhtType());
							whtCustomMenuTemp.setMenuKey(key);
							break;
						}
					}
				}else if("view".equals(whtType)){
					btn30.setUrl(WhtCustomBigMenuList3.get(i).getPageAddress());
				}
			}
		}
    	
		//组装菜单
		Menu menu = new Menu();  
        if(WhtCustomBigMenuList1.size()>0 && WhtCustomMenuList1.size()>0 && WhtCustomBigMenuList2.size()>0 && 
        		WhtCustomMenuList2.size()>0 && WhtCustomBigMenuList3.size()>0 && WhtCustomMenuList3.size()>0){
        	menu.setButton(new Button[] { mainBtn1, mainBtn2, mainBtn3 });
        }else if(WhtCustomBigMenuList1.size()>0 && WhtCustomMenuList1.size()>0 && WhtCustomBigMenuList2.size()>0 && 
        		WhtCustomMenuList2.size()>0 && WhtCustomBigMenuList3.size()>0 && WhtCustomMenuList3.size()==0){
        	menu.setButton(new Button[] { mainBtn1, mainBtn2, btn30 });
        }else if(WhtCustomBigMenuList1.size()>0 && WhtCustomMenuList1.size()>0 && WhtCustomBigMenuList2.size()>0 && 
        		WhtCustomMenuList2.size()>0 && WhtCustomBigMenuList3.size()==0 && WhtCustomMenuList3.size()==0){
        	menu.setButton(new Button[] { mainBtn1, mainBtn2 });
        }else if(WhtCustomBigMenuList1.size()>0 && WhtCustomMenuList1.size()>0 && WhtCustomBigMenuList2.size()>0 && 
        		WhtCustomMenuList2.size()==0 && WhtCustomBigMenuList3.size()==0 && WhtCustomMenuList3.size()==0){
        	menu.setButton(new Button[] { mainBtn1, btn20 });
        }else if(WhtCustomBigMenuList1.size()>0 && WhtCustomMenuList1.size()>0 && WhtCustomBigMenuList2.size()==0 && 
        		WhtCustomMenuList2.size()==0 && WhtCustomBigMenuList3.size()==0 && WhtCustomMenuList3.size()==0){
        	menu.setButton(new Button[] { mainBtn1 });
        }else if(WhtCustomBigMenuList1.size()>0 && WhtCustomMenuList1.size()==0 && WhtCustomBigMenuList2.size()==0 && 
        		WhtCustomMenuList2.size()==0 && WhtCustomBigMenuList3.size()==0 && WhtCustomMenuList3.size()==0){
        	menu.setButton(new Button[] { btn10 });
        }
        
        JSONObject fromObject = JSONObject.fromObject(menu);
		System.out.println(fromObject);
		String jsonStr = fromObject.toString();
		System.out.println(jsonStr);
		WecorpAccountMenu wecorpAccountMenu = new WecorpAccountMenu();
        wecorpAccountMenu.setAccountNum(WhtCustomMenu.getOriginalId());
        wecorpAccountMenu.setContent(jsonStr);
        HmsWxResponseDto createMenu = null;
		try {
			createMenu = wecorpAccountMenuService.createMenu(wecorpAccountMenu);
			if(createMenu.getErrcode()=="0"){
				WhtCustomMenu WhtCustomMenu2 = new WhtCustomMenu();
		    	WhtCustomMenu2.setOriginalId(WhtCustomMenu.getOriginalId());
		    	List<WhtCustomMenu> selectAll = service.select(requestContext,WhtCustomMenu2,1,10000);
		    	List<WhtCustomMenu> list = new ArrayList<>();
		    	if(selectAll.size()>0){
		    		service.batchDelete(selectAll);
		    		List<WhtCustomMenu> menuConTentList = WhtCustomMenu.getMenuConTentList();
		    		for (WhtCustomMenu whtCustomMenu3 : menuConTentList) {
		    			whtCustomMenu3.setOriginalId(WhtCustomMenu.getOriginalId());
		    			WhtCustomMenu insertSelective = service.insertSelective(requestContext,whtCustomMenu3);
		    			list.add(insertSelective);
					}
		    		return new ResponseData(list);
		    	}else{
		    		List<WhtCustomMenu> menuConTentList = WhtCustomMenu.getMenuConTentList();
		    		for (WhtCustomMenu whtCustomMenu3 : menuConTentList) {
		    			whtCustomMenu3.setOriginalId(WhtCustomMenu.getOriginalId());
		    			WhtCustomMenu insertSelective = service.insertSelective(requestContext,whtCustomMenu3);
		    			list.add(insertSelective);
					}
		    		return new ResponseData(list);
		    	}
		    	//return new ResponseData();
			}else{
				responseData.setSuccess(false);
				responseData.setMessage(createMenu.getErrmsg());
				return responseData;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseData;*/
    }
    
    @RequestMapping(value = "/fms/wht/custom/menu/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<WhtCustomMenu> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }