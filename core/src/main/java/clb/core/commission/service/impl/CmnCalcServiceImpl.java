package clb.core.commission.service.impl;

import clb.core.commission.dto.CmnCalc;
import clb.core.commission.dto.CmnChannelCommission;
import clb.core.commission.mapper.CmnCalcMapper;
import clb.core.commission.service.ICmnCalcService;
import clb.core.commission.service.ICmnChannelCommissionService;
import clb.core.common.utils.CommonUtil;
import clb.core.production.dto.PrdItems;
import clb.core.production.dto.PrdPremiumAdjust;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.production.service.IPrdPremiumAdjustService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class CmnCalcServiceImpl extends BaseServiceImpl<CmnCalc> implements ICmnCalcService {

    @Autowired
    private CmnCalcMapper cmnCalcMapper;
    @Autowired
    private PrdItemsMapper prdItemsMapper;
    @Autowired
    private IPrdPremiumAdjustService prdPremiumAdjustService;

    @Autowired
    private ICmnChannelCommissionService channelCommissionService;

    @Override
    public ResponseData productionCalcPremium(IRequest requestContext,CmnCalc dto, int page, int pageSize) {

        ResponseData responseData = new ResponseData(true);

        try{
            if(dto.getAge()==null){
                responseData.setSuccess(false);
                responseData.setMessage("年龄必输!");
                return responseData;
            }
            if(dto.getCoverage()==null && !dto.getMinClass().equals("GD")){
                responseData.setSuccess(false);
                responseData.setMessage("保额必输!");
                return responseData;
            }
            if(dto.getCurrency()==null){
                responseData.setSuccess(false);
                responseData.setMessage("币种必输!");
                return responseData;
            }

            dto.setAgePremium("PREMIUM"+dto.getAge());
            //基准年缴保费
            Double permium = cmnCalcMapper.queryAgePremium(dto);
            if(permium==null){
                responseData.setSuccess(false);
                responseData.setMessage("很抱歉，由于产品资料不全，测算失败");
                return responseData;
            }

            List<CmnCalc> cmnCalcList = new ArrayList<CmnCalc>();
            if(dto.getMinClass()!=null && dto.getMinClass().equals("GD")){
                //保费
                dto.setStagePermium(permium);
                dto.setYearPermium(permium);
            }else{
                //基准保额
                Long standardCoverage = 1000L;
                //现投保额
                Long inputCoverage = dto.getCoverage();
                PrdItems prdItems = new PrdItems();
                prdItems.setItemId(dto.getItemId());
                List<PrdItems> prdItemsList = prdItemsMapper.wsSelectByParam(prdItems);


                //每期保费
                double permiumRate = 1;
                //缴费间隔
                Long payTime =12L;
                if(dto.getPaymentMethod().equals("AP")){
                    payTime = 12L;
                    if(prdItemsList.get(0).getOneyearRate()==null||prdItemsList.get(0).getOneyearRate()==0.0){
                        responseData.setSuccess(false);
                        responseData.setMessage("很抱歉，由于产品资料不全，测算失败");
                        return responseData;
                    }else{
                        permiumRate = prdItemsList.get(0).getOneyearRate();
                    }
                }else if(dto.getPaymentMethod().equals("FJ")){
                    payTime = 12L;
                    if(prdItemsList.get(0).getOneyearRate()==null||prdItemsList.get(0).getOneyearRate()==0.0){
                        responseData.setSuccess(false);
                        responseData.setMessage("很抱歉，由于产品资料不全，测算失败");
                        return responseData;
                    }else{
                        permiumRate = prdItemsList.get(0).getOneyearRate();
                    }
                }else if(dto.getPaymentMethod().equals("WP")){
                    payTime = 12L;
                    if(prdItemsList.get(0).getOneyearRate()==null||prdItemsList.get(0).getOneyearRate()==0.0){
                        responseData.setSuccess(false);
                        responseData.setMessage("很抱歉，由于产品资料不全，测算失败");
                        return responseData;
                    }else{
                        permiumRate = prdItemsList.get(0).getOneyearRate();
                    }
                }else if(dto.getPaymentMethod().equals("SAP")){
                    payTime = 6L;
                    if(prdItemsList.get(0).getHalfyearRate()==null||prdItemsList.get(0).getHalfyearRate()==0.0){
                        responseData.setSuccess(false);
                        responseData.setMessage("很抱歉，由于产品资料不全，测算失败");
                        return responseData;
                    }else{
                        permiumRate = prdItemsList.get(0).getHalfyearRate();
                    }
                }else if(dto.getPaymentMethod().equals("QP")){
                    payTime = 4L;
                    if(prdItemsList.get(0).getQuarterRate()==null||prdItemsList.get(0).getQuarterRate()==0.0){
                        responseData.setSuccess(false);
                        responseData.setMessage("很抱歉，由于产品资料不全，测算失败");
                        return responseData;
                    }else{
                        permiumRate = prdItemsList.get(0).getQuarterRate();
                    }
                }else if(dto.getPaymentMethod().equals("MP")){
                    payTime = 1L;
                    if(prdItemsList.get(0).getOnemonthRate()==null||prdItemsList.get(0).getOnemonthRate()==0.0){
                        responseData.setSuccess(false);
                        responseData.setMessage("很抱歉，由于产品资料不全，测算失败");
                        return responseData;
                    }else{
                        permiumRate = prdItemsList.get(0).getOnemonthRate();
                    }
                }
                //每期应缴保费=每期保费系数*基准年缴保费*现投保额/基准保额
                double stagePermium = permiumRate*permium*inputCoverage/standardCoverage;

                //保费缴纳调整
                PrdPremiumAdjust prdPremiumAdjust = new PrdPremiumAdjust();
                prdPremiumAdjust.setItemId(dto.getItemId());
                prdPremiumAdjust.setCurrency(dto.getCurrency());
                prdPremiumAdjust.setSublineItemName(dto.getProductionAgeLimit());
                prdPremiumAdjust.setInsuranceCoverage((double)dto.getCoverage());
                List<PrdPremiumAdjust> prdPremiumAdjustList = prdPremiumAdjustService.selectAllFields(requestContext, prdPremiumAdjust, page, pageSize);
                if(prdPremiumAdjustList.size()>0){
                    stagePermium = stagePermium - (inputCoverage/1000)*(prdPremiumAdjustList.get(0).getPremium()) ;
                }
                //年缴保费=每期应缴保费*12/缴费间隔
                double yearPermium = stagePermium*12/payTime;

                //保费
                dto.setStagePermium(stagePermium);
                dto.setYearPermium(yearPermium);
            }
            cmnCalcList.add(dto) ;


            // 如果缴费方式为“预缴”，则默认为“年缴”
            if ("FJ".equals(dto.getPaymentMethod())) {
                dto.setPaymentMethod("AP");
            }
            //佣金
            CmnChannelCommission cmnChannelCommission = new CmnChannelCommission();
            cmnChannelCommission.setItemId(dto.getItemId());
            cmnChannelCommission.setChannelId(dto.getChannelId());
            cmnChannelCommission.setContributionPeriod(dto.getProductionAgeLimit());
            cmnChannelCommission.setCurrency(dto.getCurrency());
            cmnChannelCommission.setPolicyholdersAge(dto.getAge());
            cmnChannelCommission.setPayMethod(dto.getPaymentMethod());

            List<CmnChannelCommission> channelCommissionList = channelCommissionService.selectShowAllFields(requestContext, cmnChannelCommission, page, pageSize);

            CmnChannelCommission maxChannelCommission = new CmnChannelCommission();
            maxChannelCommission.setTheFirstYear(new BigDecimal(0.00));
            for(CmnChannelCommission channelCommission:channelCommissionList){
                if(maxChannelCommission.getTheFirstYear().compareTo(channelCommission.getTheFirstYear()) == -1){
//                    maxChannelCommission = channelCommission;
                    CommonUtil.setValue(channelCommission,maxChannelCommission);
                }
            }
            cmnCalcList.get(0).setCnmChannelCommissionList(maxChannelCommission);

            return new ResponseData( cmnCalcList);
        }catch (Exception e){
        	CommonUtil.printStackTraceToStr(e);	
            responseData.setSuccess(false);
            responseData.setMessage("很抱歉，由于产品资料不全，测算失败");
            return responseData;
        }
    }

}