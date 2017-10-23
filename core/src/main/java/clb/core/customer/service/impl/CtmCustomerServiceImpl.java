package clb.core.customer.service.impl;

import clb.core.common.service.ISequenceService;
import clb.core.customer.dto.CtmCustomerFamily;
import clb.core.customer.mapper.CtmCustomerMapper;
import clb.core.customer.service.ICtmCustomerFamilyService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.service.ICtmCustomerService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Transactional
public class CtmCustomerServiceImpl extends BaseServiceImpl<CtmCustomer> implements ICtmCustomerService {

    @Autowired
    CtmCustomerMapper ctmCustomerMapper;

    @Autowired
    ICtmCustomerFamilyService ctmCustomerFamilyService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    ISequenceService sequenceService;

    @Autowired
    private ClbUserMapper userMapper;

    private final static String CUSTOMER_CODE = "CUSTOMER";

    @Override
    public List<CtmCustomer> query(IRequest requestContext, CtmCustomer dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return ctmCustomerMapper.query(dto);
    }

    @Override
    public List<CtmCustomer> wsQuery(IRequest requestContext, CtmCustomer dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return ctmCustomerMapper.wsQuery(dto);
    }

    @Override
    public List<CtmCustomer> selectByIdNumber(IRequest requestCtx, CtmCustomer dto) {
        return ctmCustomerMapper.selectByIdNumber(dto);
    }

    @Override
    public List<CtmCustomer> queryAllCustomer(IRequest requestContext, CtmCustomer dto) {
        return ctmCustomerMapper.query(dto);
    }

    @Override
    public List<CtmCustomer> createCustomer(IRequest request, Locale locale, CtmCustomer dto) {
        if (dto != null) {
            if ( dto.getCustomerId() == null || "add".equals(dto.get__status()) ) {
                //先插入到头表
                dto.setCustomerCode(sequenceService.getSequence(CUSTOMER_CODE));
                insertSelective(request, dto);
            } else {
                updateByPrimaryKeySelective(request, dto);
            }
            //update行表
            List<CtmCustomerFamily> customerFamilyList = dto.getCustomerFamilyList();
            if (customerFamilyList != null && customerFamilyList.size() > 0) {
                for (CtmCustomerFamily customerFamily : customerFamilyList) {
                    customerFamily.setCustomerId(dto.getCustomerId());
                }
                ctmCustomerFamilyService.batchUpdate(request, customerFamilyList);
            }
        }
        List<CtmCustomer> list = selectByIdNumber(request, dto);
        return list;
    }


    /**
     *
     * @param dto   客户基本信息
     * @param locale    校验出错错误信息
     * @return
     */
    @Override
    public String checkData(CtmCustomer dto, Locale locale) {
        StringBuilder stBuilder = new StringBuilder("");
        if (dto == null) {
            stBuilder.append(messageSource.getMessage("fms.ctmcustomer.empty", null, locale));
        }

        //校验用户名是否为空
        if (StringUtils.isEmpty(dto.getChineseName())) {
            stBuilder.append(messageSource.getMessage("fms.ctmcustomer.emptyname", null, locale));
        }

        //校验电话号码
        if (StringUtils.isEmpty(dto.getPhone())) {
            stBuilder.append(messageSource.getMessage("fms.ctmcustomer.emptyphone", null, locale));
        }

        //校验身份证号
        if (StringUtils.isEmpty(dto.getIdentityNumber())) {
            stBuilder.append(messageSource.getMessage("fms.ctmcustomer.emptyidnumber", null, locale));
//        } else if (!dto.getIdentityNumber().matches("^[A-Za-z0-9\\(\\)]+$")) {
//            stBuilder.append(messageSource.getMessage("fms.ctmcustomer.idnumbervali", null, locale));
        }else{
        	//校验身份证号唯一性
        	CtmCustomer newCtmCustomer=new CtmCustomer();
        	newCtmCustomer.setIdentityNumber(dto.getIdentityNumber());
        	/*if(!CollectionUtils.isEmpty(ctmCustomerMapper.query(newCtmCustomer))){
        		stBuilder.append("该身份证号已存在");
        	}*/
            List<CtmCustomer> customers = ctmCustomerMapper.query(newCtmCustomer);
            if (customers != null && customers.size() > 0) {
                //判断是否有用户关联该客户
                ClbUser clbuser = new ClbUser();
                clbuser.setUserType("CUSTOMER");
                clbuser.setRelatedPartyId(customers.get(0).getCustomerId());
                if (!CollectionUtils.isEmpty(userMapper.selectAllFields(clbuser))){
                    stBuilder.append("该身份证号已存在对应的客户及用户");
                } else {
                    stBuilder.append("CustomerExistUserNotExist");
                }
            }
        }

        //校验邮箱格式
        if (StringUtils.isNotEmpty(dto.getEmail())) {
            if (!dto.getEmail().matches("^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$")) {
                stBuilder.append(messageSource.getMessage("fms.ctmcustomer.emailvali", null, locale));
            }
        }

        return stBuilder.toString();
    }

}
