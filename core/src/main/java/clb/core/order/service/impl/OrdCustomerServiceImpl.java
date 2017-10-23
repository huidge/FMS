package clb.core.order.service.impl;

import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.service.ICtmCustomerService;
import clb.core.order.dto.*;
import clb.core.order.mapper.OrdCustomerMapper;
import clb.core.order.service.*;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdCustomerServiceImpl extends BaseServiceImpl<OrdCustomer> implements IOrdCustomerService{

    @Autowired
    private OrdCustomerMapper ordCustomerMapper;

    @Autowired
    private ICtmCustomerService ctmCustomerService;

    /**
     * 查询
     * @param request
     * @param ordCustomer
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<OrdCustomer> queryOrdCustomer(IRequest request, OrdCustomer ordCustomer, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return ordCustomerMapper.queryOrdCustomer(ordCustomer);
    }

    /**
     * 接口查询投保人，受保人
     * daiqian.shi@hand-china.com
     *
     * @param request
     * @param ordCustomer
     * @return
     */
    @Override
    public List<OrdCustomer> queryWsOrdCustomer(IRequest request, OrdCustomer ordCustomer) {
        return ordCustomerMapper.queryOrdCustomer(ordCustomer);
    }

    /**
     * 批量更新或者新增客户
     *
     * @param request
     * @param ordCustomers
     * @return
     */
    @Override
    public List<OrdCustomer> batchSubmit(IRequest request, List<OrdCustomer> ordCustomers) {
        if (null != ordCustomers && !ordCustomers.isEmpty()){
            for (OrdCustomer ordCustomer: ordCustomers) {

                if (ordCustomer.getOrdCustomerId() == null) {
                    ordCustomer.set__status("add");
                } else {
                    ordCustomer.set__status("update");
                }

                if (ordCustomer.getOrderIds() != null) {
                    List<Long> orderIds = ordCustomer.getOrderIds();
                    for (int i=0; i < orderIds.size(); i++) {
                        ordCustomer.setOrderId(orderIds.get(i));
                        if(null != ordCustomer.get__status() && !"".equals(ordCustomer.get__status())){
                            switch (ordCustomer.get__status()){
                                case DTOStatus.ADD:
                                    this.insertSelective(request,ordCustomer);
                                    break;
                                case DTOStatus.UPDATE:
                                    this.updateByPrimaryKeySelective(request,ordCustomer);
                                    break;
                            }
                        }
                    }
                } else {
                    if(null != ordCustomer.get__status() && !"".equals(ordCustomer.get__status())) {
                        switch (ordCustomer.get__status()) {
                            case DTOStatus.ADD:
                                this.insertSelective(request, ordCustomer);
                                break;
                            case DTOStatus.UPDATE:
                                this.updateByPrimaryKeySelective(request, ordCustomer);
                                break;
                        }
                        if(null != ordCustomer.getCoverType() && !"".equals(ordCustomer.getCoverType())) {
                            CtmCustomer ctmCustomer = new CtmCustomer();
                            ctmCustomer.setCustomerId(ordCustomer.getCustomerId());
                            ctmCustomer = ctmCustomerService.selectByPrimaryKey(request,ctmCustomer);
                            Long objectVersionNumber = ctmCustomer.getObjectVersionNumber();
                            if ("F".equals(ordCustomer.getCoverType())) {
                                try {
                                    BeanUtils.copyProperties(ctmCustomer, ordCustomer);
                                    ctmCustomer.setObjectVersionNumber(objectVersionNumber);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if ("B".equals(ordCustomer.getCoverType())) {
                                ctmCustomer.setBirthDate(ordCustomer.getBirthDate());
                                ctmCustomer.setPhoneCode(ordCustomer.getPhoneCode());
                                ctmCustomer.setPhone(ordCustomer.getPhone());
                                ctmCustomer.setEmail(ordCustomer.getEmail());
                                ctmCustomer.setIdentityNumber(ordCustomer.getIdentityNumber());
                            }
                            ctmCustomer.set__status("update");
                            ctmCustomerService.updateByPrimaryKey(request,ctmCustomer);

                        }
                    }
                }

            }
        }
        return ordCustomers;
    }
}