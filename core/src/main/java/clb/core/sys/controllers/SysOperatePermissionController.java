package clb.core.sys.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.account.dto.Role;
import com.hand.hap.account.mapper.RoleMapper;
import com.hand.hap.account.service.IRoleService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.exceptions.CommonException;
import clb.core.production.dto.PrdClass;
import clb.core.production.dto.PrdClassSet;
import clb.core.production.mapper.PrdClassMapper;
import clb.core.production.mapper.PrdClassSetMapper;
import clb.core.sys.dto.SysOperatePermission;
import clb.core.sys.service.ISysOperatePermissionService;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;

@Controller
@RequestMapping(value = "/fms/sys/operate/permission")
public class SysOperatePermissionController extends BaseController{

    @Autowired
    private ISysOperatePermissionService service;
    
    @Autowired
    private PrdClassMapper classMapper;
    
    @Autowired
    private PrdClassSetMapper classSetMapper;
    
    @Autowired
	private IClbCodeService clbCodeService;
    
    @Autowired
	private RoleMapper roleMapper;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(SysOperatePermission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<SysOperatePermission> data = service.select(requestContext,dto,page,pageSize);
        for(SysOperatePermission d:data){
        	Role role = new Role();
        	role.setRoleId(d.getRoleId());
        	role = roleMapper.selectByPrimaryKey(role);
        	if(role != null){
        		d.setRoleName(role.getRoleName());
        	}
        }
        return new ResponseData(data);
    }
    
    @RequestMapping(value = "/queryClassSetData")
    @ResponseBody
    public ResponseData queryClassSetData(SysOperatePermission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        //查询分类集代码
        PrdClassSet classSet = new PrdClassSet();
        classSet.setSetCode("FUNC.FIELD");
        //查询分类集
        classSet = classSetMapper.selectOne(classSet);
        //分类集的值
        List<String> className = new ArrayList<>();
        List<ClbCodeValue> codevalues = new ArrayList<>();

        if(dto.getClassNameIndex() == 1){
        	//查询分类集的值
            PrdClass prdClass = new PrdClass();
            prdClass.setSetId(classSet.getSetId());
          	List<PrdClass> prdClassData = classMapper.select(prdClass);
          	for(PrdClass p:prdClassData){
          		className.add(p.getClassName1());
          	}
        	//查询代码维护中一级分类的值
          	codevalues = clbCodeService.selectCodeValuesByCodeName(requestContext,classSet.getClassName1Code());
          	Iterator<ClbCodeValue> iterator = codevalues.iterator();
          	//剔除分类集中没定义的值
          	while(iterator.hasNext()){
        		if(!className.contains(iterator.next().getValue())){
        			iterator.remove();
        		}
        	}
        }
        
        if(dto.getClassNameIndex() == 2){
        	//查询分类集的值
            PrdClass prdClass = new PrdClass();
            prdClass.setSetId(classSet.getSetId());
            if(dto.getFuncCode() == null){
            	return new ResponseData(codevalues);
            }
            prdClass.setClassName1(dto.getFuncCode());
          	List<PrdClass> prdClassData = classMapper.select(prdClass);
          	//分类集2的值
          	for(PrdClass p:prdClassData){
          		className.add(p.getClassName2());
          	}
        	//查询代码维护中二级分类的值
          	codevalues = clbCodeService.selectCodeValuesByCodeName(requestContext,classSet.getClassName2Code());
          	Iterator<ClbCodeValue> iterator = codevalues.iterator();
          	//剔除分类集中没定义的值
          	while(iterator.hasNext()){
        		if(!className.contains(iterator.next().getValue())){
        			iterator.remove();
        		}
        	}
        }
        return new ResponseData(codevalues);
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SysOperatePermission> dto) throws CommonException{
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.operatePermissionBatchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SysOperatePermission> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
}