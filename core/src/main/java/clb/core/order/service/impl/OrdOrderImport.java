package clb.core.order.service.impl;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.common.utils.ImportUtil;
import clb.core.common.utils.SerializeUtil;
import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.mapper.CtmCustomerMapper;
import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.service.IImportTempService;
import clb.core.fnd.service.impl.AbstractImportExecute;
import clb.core.fnd.utils.Constants;
import clb.core.fnd.utils.ValidationTableException;
import clb.core.forecast.cache.ImportMessageCache;
import clb.core.order.dto.*;
import clb.core.order.mapper.OrdOrderMapper;
import clb.core.order.service.IOrdOrderService;
import clb.core.production.dto.*;
import clb.core.production.mapper.*;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.mapper.SpeSupplierMapper;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbCodeService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.mapper.CodeValueMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author
 * @version 1.0
 * @name OrdOrderImport
 * @description 订单导入
 */
@Component
public class OrdOrderImport extends AbstractImportExecute {

    @Autowired
    private IImportTempService importTempService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IOrdOrderService ordOrderService;

    @Autowired
    private OrdOrderMapper ordOrderMapper;

    @Autowired
    private CodeValueMapper codeValueMapper;

    @Autowired
    private IClbCodeService clbCodeService;

    @Autowired
    private CnlChannelMapper cnlChannelMapper;

    @Autowired
    private SpeSupplierMapper speSupplierMapper;

    @Autowired
    private PrdItemsMapper prdItemsMapper;

    @Autowired
    private PrdItemSublineMapper prdItemSublineMapper;

    @Autowired
    private PrdItemPaymodeMapper prdItemPaymodeMapper;

    @Autowired
    private ImportMessageCache importMessageCache;

    @Autowired
    private CtmCustomerMapper ctmCustomerMapper;

    @Autowired
    private PrdItemSecurityPlanMapper prdItemSecurityPlanMapper;

    @Autowired
    private PrdItemSelfpayMapper prdItemSelfpayMapper;

    @Autowired
    private CnlChannelContractMapper cnlChannelContractMapper;

    @Autowired
    private ClbUserMapper clbUserMapper;

    /**
     * @Description: 数据导入
     */
    @Override
    public void execute(Map<String, Object> args) throws ValidationTableException, Exception {

        UUID uuid = UUID.randomUUID();
        String key = uuid.toString();

        Long importBatchId = (Long) args.get("importBatchId");
        IRequest request = (IRequest) args.get("request");

        String[] locales = request.getLocale().split("_");
        Locale locale = new Locale(locales[0], locales[1]);

        int countImportBatchId = 0;
        //校验该批次数据是否已经导入过
        if (null != importBatchId) {
            //countImportBatchId = supplierAllotRatioService.selectCountImportBatchIdByAttrIbute(importBatchId);
        }

        if (countImportBatchId > 0) {
            throw new ValidationTableException(Constants.BATCH_REPEAT, null);
        }
        validateData(importBatchId, request, key);

        List<OrdOrder> ordOrders = new ArrayList<OrdOrder>();//将临时表的数据转换成订单数据
        List<OrdCustomer> ordCustomers = new ArrayList<OrdCustomer>();//将临时表的数据转换成订单客户数据
        List<OrdBeneficiary> ordBeneficiaries = new ArrayList<OrdBeneficiary>();//将临时表的数据转换成受益人数据
        List<OrdTradeRoute> ordTradeRoutes = new ArrayList<OrdTradeRoute>();//将临时表的数据转换成交易路线数据
        List<OrdTradeRouteShow> ordTradeRouteShows = new ArrayList<OrdTradeRouteShow>();//将临时表的数据转换成交易路线数据
        List<OrdAddition> ordAdditions = new ArrayList<OrdAddition>();//将临时表的数据转换成订单附加险数据
        List<OrdCommission> ordCommissions = new ArrayList<OrdCommission>();//将临时表的数据转换成订单佣金数据

        if (importTempService.selectErrorCount(importBatchId) == 0) {//验证没有错误
            byte[] ordOrdersByte = importMessageCache.getValue(key + "ordOrders");
            if (ordOrdersByte != null) {
                ordOrders = (List<OrdOrder>) SerializeUtil.unserialize(ordOrdersByte);
            }
            byte[] ordCustomersByte = importMessageCache.getValue(key + "ordCustomers");
            if (ordCustomersByte != null) {
                ordCustomers = (List<OrdCustomer>) SerializeUtil.unserialize(ordCustomersByte);
            }
            byte[] ordBeneficiariesByte = importMessageCache.getValue(key + "ordBeneficiaries");
            if (ordBeneficiariesByte != null) {
                ordBeneficiaries = (List<OrdBeneficiary>) SerializeUtil.unserialize(ordBeneficiariesByte);
            }
            byte[] ordTradeRoutesByte = importMessageCache.getValue(key + "ordTradeRoutes");
            if (ordTradeRoutesByte != null) {
                ordTradeRoutes = (List<OrdTradeRoute>) SerializeUtil.unserialize(ordTradeRoutesByte);
            }
            byte[] ordTradeRouteShowsByte = importMessageCache.getValue(key + "ordTradeRouteShows");
            if (ordTradeRouteShowsByte != null) {
                ordTradeRouteShows = (List<OrdTradeRouteShow>) SerializeUtil.unserialize(ordTradeRouteShowsByte);
            }
            byte[] ordAdditionsByte = importMessageCache.getValue(key + "ordAdditions");
            if (ordAdditionsByte != null) {
                ordAdditions = (List<OrdAddition>) SerializeUtil.unserialize(ordAdditionsByte);
            }
            byte[] ordCommissionsByte = importMessageCache.getValue(key + "ordCommissions");
            if (ordCommissionsByte != null) {
                ordCommissions = (List<OrdCommission>) SerializeUtil.unserialize(ordCommissionsByte);
            }
            ordOrderService.importData(request, ordOrders, ordCustomers, ordBeneficiaries, ordTradeRoutes, ordTradeRouteShows, ordAdditions, ordCommissions);
        }
        StringBuffer errorMsg = new StringBuffer("");
    }

    /**
     * @return
     * @throws IntrospectionException
     * @Description: 数据验证
     * @author
     */
    public void validateData(Long importBatchId, IRequest request, String key) throws ValidationTableException, Exception {
        String[] locales = request.getLocale().split("_");
        Locale locale = new Locale(locales[0], locales[1]);

        PropertyDescriptor attributeTitle = null;
        PropertyDescriptor attributeValue = null;
        List<ImportTemp> importTemps = new ArrayList<ImportTemp>();//根据流水号查得的所有临时表数据


        List<OrdOrder> ordOrders = new ArrayList<OrdOrder>();//将临时表的数据转换成订单数据
        List<OrdCustomer> ordCustomers = new ArrayList<OrdCustomer>();//将临时表的数据转换成订单客户数据
        List<OrdBeneficiary> ordBeneficiaries = new ArrayList<OrdBeneficiary>();//将临时表的数据转换成受益人数据
        List<OrdTradeRoute> ordTradeRoutes = new ArrayList<OrdTradeRoute>();//将临时表的数据转换成交易路线数据
        List<OrdTradeRouteShow> ordTradeRouteShows = new ArrayList<OrdTradeRouteShow>();//将临时表的数据转换成交易路线数据
        List<OrdAddition> ordAdditions = new ArrayList<OrdAddition>();//将临时表的数据转换成订单附加险数据
        List<OrdCommission> ordCommissions = new ArrayList<OrdCommission>();//将临时表的数据转换成订单佣金数据

        List<String> uniOrdOrderList = new ArrayList<String>();   // 订单编号验证
        List<String> uniOrdCustomerList = new ArrayList<String>();   // 订单编号验证
        List<String> uniOrdTradeRouteList = new ArrayList<String>();   // 订单编号验证
        List<String> orderNumberList = new ArrayList<String>();   // 所有订单编号

        if (null != importBatchId) {
            //查出所有当前批次的数据
            importTemps = importTempService.selectImportData(importBatchId, 1, Constants.MAX_NUMBER, request);
        }
        List<String> sheets = new ArrayList<String>();//sheet页操作
        ImportTemp importTemp = new ImportTemp();//循环对象
        //对当前批次数据做迭代循环校验
        if (null != importTemps && importTemps.size() > 0) {
            Iterator<ImportTemp> iterator = importTemps.iterator();
            ImportTemp importTempTitle = null;
            StringBuffer errorTitleMessage = new StringBuffer("");//检查标题行是否有错
            //防止标题错误重复添加
            boolean isAppendErrorTitle = false;
            while (iterator.hasNext()) {
                importTemp = iterator.next();
                StringBuffer errorMessage = new StringBuffer("");
                if (!sheets.contains(importTemp.getSheet())) {//标题行
                    sheets.add(importTemp.getSheet());
                    if (!"".equals(errorTitleMessage.toString())) {//行标题有错误
                        importTempTitle.setImportMessage(errorTitleMessage.toString());
                        importTempService.updateError(importTempTitle, request);
                    }
                    errorTitleMessage = new StringBuffer("");
                    isAppendErrorTitle = false;
                    importTempTitle = importTemp;
                    //标题行重复性验证
                    Set<String> titleSet = new HashSet<String>();
                    for (int i = 1; i <= Constants.MAX_ATTR; i++) {
                        //标题字段
                        attributeTitle = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTempTitle.getClass());
                        Method methodRead = attributeTitle.getReadMethod();
                        String objTitle = (String) methodRead.invoke(importTempTitle);
                        if (null != objTitle && !"".equals(objTitle.trim())) {
                            if (titleSet.contains(objTitle)) {
                                errorTitleMessage.append(messageSource.getMessage(Constants.TITLE_EXCEPTION, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            } else {
                                titleSet.add(objTitle);
                            }
                        }
                    }
                } else if (importTemp.getLineNumber() != 1) {   //第一行为标题行
                    OrdOrder ordOrder = new OrdOrder();
                    ordOrder.setApplicationStatus("NOT_TRANSCRIBED");
                    OrdCustomer applicant = new OrdCustomer();
                    OrdCustomer insurant = new OrdCustomer();
                    OrdBeneficiary ordBeneficiary = new OrdBeneficiary();
                    OrdTradeRoute ordTradeRoute1 = new OrdTradeRoute();
                    OrdTradeRouteShow ordTradeRouteShow = new OrdTradeRouteShow();
                    Class routeCla = (Class) ordTradeRouteShow.getClass();
                    Field[] fs = routeCla.getDeclaredFields();
                    OrdAddition ordAddition = new OrdAddition();
                    OrdCommission ordCommission = new OrdCommission();
                    ordCommission.setManualFlag("Y");
                    Long seqNum = 0L;
                    PrdItems prdItems = new PrdItems();
                    String dealPath = null;

                    BigDecimal yearAmount = null;

                    //对所有属性循环
                    for (int i = 1; i <= Constants.MAX_ATTR; i++) {
                        //标题字段
                        attributeTitle = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTempTitle.getClass());
                        Method methodTitle = attributeTitle.getReadMethod();
                        String objTitle = (String) methodTitle.invoke(importTempTitle);
                        //当前数据
                        attributeValue = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTemp.getClass());
                        Method methodValue = attributeValue.getReadMethod();
                        String objValue = (String) methodValue.invoke(importTemp);

                        // 以下为数据校验
                        // 以下按标题做判断对应标题做对应的验证,若没有对应到标题则在最后添加错误信息
                        if (null == objTitle || "".equals(objTitle.trim())) {//标题为空时
                            if (null == objValue || "".equals(objValue.trim())) {//行内数据也是空，跳出循环，即不允许出现空的列，否则后面数据当无效处理
                                break;
                            } else {//标题为空但行内有值，表示数据多余或标题缺失
                                if (!isAppendErrorTitle) {
                                    isAppendErrorTitle = true;
                                    errorTitleMessage.append(Constants.ERROR_TITLE).append(";");
                                }
                            }
                        }
                        // 订单编号验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("订单编号")) {
                            if (null != objValue && !"".equals(objValue.trim())) {

                                orderNumberList.add(objValue);

                                OrdOrder ordOrder1 = new OrdOrder();
                                ordOrder1.setOrderNumber(objValue);
                                List<OrdOrder> ordOrders1= ordOrderMapper.select(ordOrder1);

                                if(CollectionUtils.isNotEmpty(ordOrders1)){
                                    ordOrder.setOrderNumber(objValue);
                                    errorMessage.append(messageSource.getMessage("订单编号在系统中已存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setOrderNumber(objValue);
                                }

                            } else {
                                ordOrder.setOrderNumber("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }
                        // 合同编号验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("合同编号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {

                                ordOrder.setPolicyNumber(objValue);

                            }
                        }
                        // 状态验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("状态")) {

                            if (null != objValue && !"".equals(objValue.trim())) {

                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "ORD.ORDER_STATUS", objValue);

                                if(valueCode==null){
                                    ordOrder.setStatus("");
                                    errorMessage.append(messageSource.getMessage("状态在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setStatus(valueCode);
                                }

                            } else {
                                ordOrder.setStatus("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 提交时间验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("提交时间")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    ordOrder.setSubmitDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        ordOrder.setSubmitDate(date);
                                    } catch (Exception ee) {
                                        ordOrder.setSubmitDate(null);
                                        errorMessage.append(messageSource.getMessage("提交时间格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }

                            } else {
                                ordOrder.setSubmitDate(new Date());
                            }
                        }

                        // 预约签单时间验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("预约签单时间")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    ordOrder.setReserveDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        ordOrder.setReserveDate(date);
                                    } catch (Exception ee) {
                                        ordOrder.setReserveDate(null);
                                        errorMessage.append(messageSource.getMessage("预约签单时间格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            } else {
                                ordOrder.setReserveDate(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 渠道验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("渠道")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                CnlChannel cnlChannel = new CnlChannel();
                                cnlChannel.setChannelName(objValue);
                                List<CnlChannel> cnlChannels = cnlChannelMapper.select(cnlChannel);
                                if(CollectionUtils.isEmpty(cnlChannels)){
                                    ordOrder.setChannelId(null);
                                    errorMessage.append(messageSource.getMessage("渠道在系统中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setChannelId(cnlChannels.get(0).getChannelId());
                                }

                            } else {
                                ordOrder.setChannelId(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 渠道公司验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("渠道公司")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                CnlChannel cnlChannel = new CnlChannel();
                                cnlChannel.setChannelName(objValue);
                                List<CnlChannel> cnlChannels = cnlChannelMapper.select(cnlChannel);
                                if(CollectionUtils.isEmpty(cnlChannels)){
                                    ordOrder.setCompanyChannelId(null);
                                    errorMessage.append(messageSource.getMessage("渠道公司在系统中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setCompanyChannelId(cnlChannels.get(0).getChannelId());
                                }

                            }
                        }

                        // 所属公司验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("所属公司")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SpeSupplier speSupplier = new SpeSupplier();
                                speSupplier.setName(objValue);
                                List<SpeSupplier> speSuppliers = speSupplierMapper.select(speSupplier);
                                if(CollectionUtils.isEmpty(speSuppliers)){
                                    ordOrder.setOwnSupplierId(null);
                                    errorMessage.append(messageSource.getMessage("所属公司在系统中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setOwnSupplierId(speSuppliers.get(0).getSupplierId());
                                }

                            } else {
                                ordOrder.setOwnSupplierId(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 业务行政验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("业务行政")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ClbUser clbUser = new ClbUser();
                                clbUser.setEmployeeName(objValue);

                                List<ClbUser> clbUsers = ordOrderMapper.queryUser(clbUser);

                                if(CollectionUtils.isEmpty(clbUsers)){
                                    ordOrder.setContractRoleId(null);
                                    errorMessage.append(messageSource.getMessage("业务行政在系统中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setContractRoleId(clbUsers.get(0).getUserId());
                                }

                            } else {
                                ordOrder.setContractRoleId(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }
                        // 产品公司验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("产品公司")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SpeSupplier speSupplier = new SpeSupplier();
                                speSupplier.setName(objValue);
                                List<SpeSupplier> speSuppliers = speSupplierMapper.select(speSupplier);
                                if(CollectionUtils.isEmpty(speSuppliers)){
                                    ordOrder.setProductSupplierId(null);
                                    errorMessage.append(messageSource.getMessage("产品公司在系统中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setProductSupplierId(speSuppliers.get(0).getSupplierId());
                                }

                            } else {
                                ordOrder.setProductSupplierId(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 产品验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("产品")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                PrdItems prdItems1 = new PrdItems();
                                prdItems1.setItemName(objValue);
                                List<PrdItems> prdItemss = prdItemsMapper.selectByItemName(prdItems1);
                                if(CollectionUtils.isEmpty(prdItemss)){
                                    ordOrder.setItemId(null);
                                    ordOrder.setItemName(objValue);
                                    errorMessage.append(messageSource.getMessage("产品在系统中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setItemId(prdItemss.get(0).getItemId());
                                    ordOrder.setItemName(objValue);
                                    prdItems = prdItemss.get(0);
                                }

                            } else {
                                ordOrder.setItemId(null);
                                ordOrder.setItemName(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 年期验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("年期")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                PrdItemSubline prdItemSubline = new PrdItemSubline();
                                prdItemSubline.setSublineItemName(objValue);
                                prdItemSubline.setItemId(ordOrder.getItemId());
                                List<PrdItemSubline> prdItemSublines = prdItemSublineMapper.select(prdItemSubline);
                                if(CollectionUtils.isEmpty(prdItemSublines)){
                                    ordOrder.setSublineId(null);
                                    errorMessage.append(messageSource.getMessage("年期在产品的子产品中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setSublineId(prdItemSublines.get(0).getSublineId());
                                }

                            } else {
                                ordOrder.setSublineId(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 币种验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("币种")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                PrdItemPaymode prdItemPaymode = new PrdItemPaymode();
                                prdItemPaymode.setCurrencyCode(objValue);
                                prdItemPaymode.setItemId(ordOrder.getItemId());
                                List<PrdItemPaymode> prdItemPaymodes = prdItemPaymodeMapper.select(prdItemPaymode);
                                if(CollectionUtils.isEmpty(prdItemPaymodes)){
                                    ordOrder.setCurrency("");
                                    errorMessage.append(messageSource.getMessage("币种在产品的币种中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setCurrency(prdItemPaymodes.get(0).getCurrencyCode());
                                }

                            } else {
                                ordOrder.setCurrency("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 缴费方式验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("缴费方式")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "CMN.PAY_METHOD", objValue);

                                if(valueCode==null){
                                    errorMessage.append(messageSource.getMessage("缴费方式在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }

                                if ("Y".equals(prdItems.getFullyear()) && "WP".equals(valueCode) ) {
                                    ordOrder.setPayMethod(valueCode);
                                } else if ("Y".equals(prdItems.getOneyear()) && "AP".equals(valueCode) ) {
                                    ordOrder.setPayMethod(valueCode);
                                } else if ("Y".equals(prdItems.getHalfyear()) && "SAP".equals(valueCode) ) {
                                    ordOrder.setPayMethod(valueCode);
                                } else if ("Y".equals(prdItems.getQuarter()) && "QP".equals(valueCode) ) {
                                    ordOrder.setPayMethod(valueCode);
                                } else if ("Y".equals(prdItems.getOnemonth()) && "MP".equals(valueCode) ) {
                                    ordOrder.setPayMethod(valueCode);
                                } else if ("Y".equals(prdItems.getPrepayFlag()) && "FJ".equals(valueCode) ) {
                                    ordOrder.setPayMethod(valueCode);
                                } else {
                                    ordOrder.setPayMethod("");
                                    errorMessage.append(messageSource.getMessage("缴费方式在产品的缴费方式中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }

                            } else {
                                ordOrder.setPayMethod("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 保费验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("保费")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                try {
                                    ordOrder.setYearPayAmount(new BigDecimal(objValue));
                                } catch(Exception e) {
                                    errorMessage.append("金额数据格式异常;");
                                }
                            } else {
                                ordOrder.setYearPayAmount(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 保额验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("保额")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                try {
                                    ordOrder.setPolicyAmount(new BigDecimal(objValue));
                                } catch(Exception e) {
                                    errorMessage.append("保额数据格式异常;");
                                }
                            }
                        }

                        // 保单回溯日验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("保单回溯日")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    ordOrder.setBackDate(date);
                                    ordOrder.setBackFlag("Y");
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        ordOrder.setBackDate(date);
                                        ordOrder.setBackFlag("Y");
                                    } catch (Exception ee) {
                                        ordOrder.setBackDate(null);
                                        ordOrder.setBackFlag("");
                                        errorMessage.append(messageSource.getMessage("保单回溯日格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }

                            }
                        }

                        // 预约到达时间验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("预约到达时间")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    ordOrder.setReserveArrivalDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        ordOrder.setReserveArrivalDate(date);
                                    } catch (Exception ee) {
                                        ordOrder.setReserveArrivalDate(null);
                                        errorMessage.append(messageSource.getMessage("预约到达时间格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        // 预约供应商验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("预约供应商")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SpeSupplier speSupplier = new SpeSupplier();
                                speSupplier.setName(objValue);
                                List<SpeSupplier> speSuppliers = speSupplierMapper.select(speSupplier);
                                if(CollectionUtils.isEmpty(speSuppliers)){
                                    ordOrder.setReserveSupplierId(null);
                                    errorMessage.append(messageSource.getMessage("预约供应商在系统中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setReserveSupplierId(speSuppliers.get(0).getSupplierId());
                                }

                            }
                        }

                        // 签单供应商验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("签单供应商")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SpeSupplier speSupplier = new SpeSupplier();
                                speSupplier.setName(objValue);
                                List<SpeSupplier> speSuppliers = speSupplierMapper.select(speSupplier);
                                if(CollectionUtils.isEmpty(speSuppliers)){
                                    ordOrder.setSignSupplierId(null);
                                    errorMessage.append(messageSource.getMessage("签单供应商在系统中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setSignSupplierId(speSuppliers.get(0).getSupplierId());
                                }

                            }
                        }

                        // 签单经理TR验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("签单经理TR")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordOrder.setSignPerson(objValue);
                            }
                        }

                        // 签单地址验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("签单地址")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordOrder.setSignAddress(objValue);
                            }
                        }

                        // 见面地址验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("见面地址")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordOrder.setMeetAddress(objValue);
                            }
                        }

                        // 联络人验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("联络人")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordOrder.setContactPerson(objValue);
                            }
                        }

                        // 联络电话验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("联络电话")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordOrder.setContactPhone(objValue);
                            }
                        }

                        // 赴港联系人验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("赴港联系人")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordOrder.setHkContactPerson(objValue);
                            }
                        }

                        // 赴港联系电话验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("赴港联系电话")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String[] phone = objValue.split("-");
                                if (phone.length >= 2) {
                                    ordOrder.setHkContactPhoneCode(phone[0]);
                                    ordOrder.setHkContactPhone(phone[1]);
                                } else if (phone.length == 1) {
                                    ordOrder.setHkContactPhoneCode("");
                                    ordOrder.setHkContactPhone(phone[0]);
                                }
                            }
                        }

                        // 首期供款方式验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("首期供款方式")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String firstCode = "" ;
                                String flag = "Y";
                                String[] first = objValue.split("，");
                                for (String s:first) {
                                    String valueCode=clbCodeService.getCodeValueByMeaning(request, "ORD.FIRST_PAYMENT_METHOD", s);
                                    if(valueCode==null){
                                        flag = "N";
                                    } else {
                                        if (StringUtils.isEmpty(firstCode)) {
                                            firstCode = valueCode;
                                        } else {
                                            firstCode = firstCode + "," + valueCode;
                                        }
                                    }
                                }

                                if ("N".equals(flag)) {
                                    ordOrder.setFirstPaymentMethod("");
                                    errorMessage.append(messageSource.getMessage("首期供款方式在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                } else {
                                    ordOrder.setFirstPaymentMethod(firstCode);
                                }
                            }
                        }

                        // 续期供款方式验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("续期供款方式")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String renewalCode = "" ;
                                String flag = "Y";
                                String[] renewal = objValue.split("，");
                                for (String s:renewal) {
                                    String valueCode=clbCodeService.getCodeValueByMeaning(request, "ORD.RENEWAL_PAYMENT_METHOD", s);
                                    if(valueCode==null){
                                        flag = "N";
                                    } else {
                                        if (StringUtils.isEmpty(renewalCode)) {
                                            renewalCode = valueCode;
                                        } else {
                                            renewalCode = renewalCode + "," + valueCode;
                                        }
                                    }
                                }

                                if ("N".equals(flag)) {
                                    ordOrder.setRenewalPaymentMethod("");
                                    errorMessage.append(messageSource.getMessage("续期供款方式在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                } else {
                                    ordOrder.setRenewalPaymentMethod(renewalCode);
                                }
                            }
                        }

                        // 批核日验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("批核日")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    ordOrder.setApproveDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        ordOrder.setApproveDate(date);
                                    } catch (Exception ee) {
                                        ordOrder.setApproveDate(null);
                                        errorMessage.append(messageSource.getMessage("批核日格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        // 保单生效日验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("保单生效日")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    ordOrder.setEffectiveDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        ordOrder.setEffectiveDate(date);
                                    } catch (Exception ee) {
                                        ordOrder.setEffectiveDate(null);
                                        errorMessage.append(messageSource.getMessage("保单生效日格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        // 首期保费日验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("首期保费日")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    ordOrder.setFirstPayDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        ordOrder.setFirstPayDate(date);
                                    } catch (Exception ee) {
                                        ordOrder.setFirstPayDate(null);
                                        errorMessage.append(messageSource.getMessage("首期保费日格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        // 预计冷静期验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("预计冷静期")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    ordOrder.setExpectCoolDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        ordOrder.setExpectCoolDate(date);
                                    } catch (Exception ee) {
                                        ordOrder.setExpectCoolDate(null);
                                        errorMessage.append(messageSource.getMessage("预计冷静期格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        // 下期保费金额验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("下期保费金额")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                try {
                                    ordOrder.setNextPolicyAmount(new BigDecimal(objValue));
                                } catch(Exception e) {
                                    errorMessage.append("下期保费金额数据格式异常;");
                                }
                            }
                        }

                        // 下期保费到期日验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("下期保费到期日")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    ordOrder.setNextPolicyDueDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        ordOrder.setNextPolicyDueDate(date);
                                    } catch (Exception ee) {
                                        ordOrder.setNextPolicyDueDate(null);
                                        errorMessage.append(messageSource.getMessage("下期保费到期日格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        // 已递交DDA验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("已递交DDA")) {

                            if (null != objValue && !"".equals(objValue.trim())) {

                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "SYS.YES_NO", objValue);

                                if(valueCode==null){
                                    ordOrder.setDdaFlag("");
                                    errorMessage.append(messageSource.getMessage("已递交DDA在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setDdaFlag(valueCode);
                                }

                            }
                        }

                        // DDA提交日期验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("DDA提交日期")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    ordOrder.setDdaSubmitDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        ordOrder.setDdaSubmitDate(date);
                                    } catch (Exception ee) {
                                        ordOrder.setDdaSubmitDate(null);
                                        errorMessage.append(messageSource.getMessage("DDA提交日期格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        // DDA生效日期验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("DDA生效日期")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    ordOrder.setDdaEffectiveDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        ordOrder.setDdaEffectiveDate(date);
                                    } catch (Exception ee) {
                                        ordOrder.setDdaEffectiveDate(null);
                                        errorMessage.append(messageSource.getMessage("DDA生效日期格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        // 回馈余额验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("回馈余额")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordOrder.setFeedbackBalance(new BigDecimal(objValue));
                            }
                        }

                        // 账户余额验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("账户余额")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordOrder.setAccountBalance(new BigDecimal(objValue));
                            }
                        }

                        // 缴费编码验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("缴费编码")) {

                            if (null != objValue && !"".equals(objValue.trim())) {

                                ordOrder.setPayNumber(objValue);

                            }
                        }

                        // 客户编号验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("客户编号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {

                                ordOrder.setCustomerNumber(objValue);

                            }
                        }

                        // 计划书文件名验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("计划书文件名")) {
                            if (null != objValue && !"".equals(objValue.trim())) {

                            }
                        }

                        // 保障地区验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("保障地区")) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordOrder.setSecurityRegion(objValue);
                            }
                        }
                        // 保障级别验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("保障级别")) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordOrder.setSecurityLevel(objValue);
                            }
                        }
                        // 自付选项验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("自付选项")) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                objValue = objValue.replaceAll("\"", "");
                                ordOrder.setSelfpay(objValue);
                            }
                        }

                        // 介绍人验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("介绍人")) {
                            if (null != objValue && !"".equals(objValue.trim())) {

                                ClbUser clbUser = new ClbUser();
                                clbUser.setUserType("CHANNEL");
                                clbUser.setRelatedPartyName(objValue);
                                List<ClbUser> clbUsers = clbUserMapper.selectAllFields(clbUser);
                                if (CollectionUtils.isNotEmpty(clbUsers)) {
                                    ordOrder.setIntroducer(clbUsers.get(0).getUserId().toString());
                                } else {
                                    errorMessage.append("介绍人系统值不存在;");
                                    continue;
                                }
                            }
                        }

                        // 介绍费%验证
                        else if (importTemp.getSheet().equals("订单预约数据")
                                && objTitle.trim().equals("介绍费%")) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordOrder.setIntroduceBenefit(objValue);
                            }
                        }

                        // 订单编号验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("订单编号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (orderNumberList.contains(objValue)) {
                                    applicant.setOrderNumber(objValue);
                                    insurant.setOrderNumber(objValue);
                                } else {
                                    applicant.setOrderNumber("");
                                    insurant.setOrderNumber("");
                                    errorMessage.append(messageSource.getMessage("订单编号在订单预约数据中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                applicant.setOrderNumber("");
                                insurant.setOrderNumber("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 投保人与受保人关系验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人与受保人关系")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "ORD.RELATIONSHIP", objValue);

                                if(valueCode==null){
                                    ordOrder.setSameFlag("");
                                    errorMessage.append(messageSource.getMessage("投保人与受保人关系在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordOrder.setSameFlag(valueCode);
                                }
                            } else {
                                ordOrder.setSameFlag("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 投保人-姓名验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-姓名")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setChineseName(objValue);
                            } else {
                                applicant.setChineseName("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 投保人-姓名拼音验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-姓名拼音")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setEnglishName(objValue.toUpperCase());
                            }
                        }

                        // 投保人-国籍验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-国籍")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.NATION", objValue);

                                if(valueCode==null){
                                    applicant.setNationality("");
                                    errorMessage.append(messageSource.getMessage("投保人-国籍在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setNationality(valueCode);
                                }
                            }
                        }

                        // 投保人-教育程度验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-教育程度")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.EDUCATION", objValue);

                                if(valueCode==null){
                                    applicant.setEducation("");
                                    errorMessage.append(messageSource.getMessage("投保人-教育程度在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setEducation(valueCode);
                                }
                            }
                        }

                        // 投保人-出生日期验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-出生日期")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    applicant.setBirthDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        applicant.setBirthDate(date);
                                    } catch (Exception ee) {
                                        applicant.setBirthDate(null);
                                        errorMessage.append(messageSource.getMessage("投保人-出生日期格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        // 投保人-性别验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-性别")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "HR.EMPLOYEE_GENDER", objValue);

                                if(valueCode==null){
                                    applicant.setSex("");
                                    errorMessage.append(messageSource.getMessage("投保人-性别在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setSex(valueCode);
                                }
                            }
                        }

                        // 投保人-身份证号验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-身份证号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                CtmCustomer ctmCustomer = new CtmCustomer();
                                ctmCustomer.setIdentityNumber(objValue);
                                List<CtmCustomer> ctmCustomers = ctmCustomerMapper.select(ctmCustomer);
                                if(CollectionUtils.isEmpty(ctmCustomers)){
                                    applicant.setIdentityNumber(objValue);
                                    ordOrder.setApplicantIdentityNumber(objValue);
                                    ordOrder.setApplicantCustomerId(null);
//                                    errorMessage.append("投保人-身份证号在系统中不存在", new Object[]{
//                                            objTitle.trim()}, locale)).append(";");
//                                    continue;
                                }else {
                                    applicant.setIdentityNumber(objValue);
                                    ordOrder.setApplicantIdentityNumber(objValue);
                                    ordOrder.setApplicantCustomerId(ctmCustomers.get(0).getCustomerId());
                                }

                            } else {
                                applicant.setIdentityNumber("");
                                ordOrder.setApplicantIdentityNumber("");
                                ordOrder.setApplicantCustomerId(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 投保人-其他证件类型验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-其他证件类型")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "CTM.CERTIFICATE_TYPE", objValue);

                                if(valueCode==null){
                                    applicant.setCertificateType("");
                                    errorMessage.append(messageSource.getMessage("投保人-其他证件类型在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setCertificateType(valueCode);
                                }
                            }
                        }

                        // 投保人-其他证件号验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-其他证件号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setCertificateNumber(objValue);
                            }
                        }

                        // 投保人-其他证件有效期验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-其他证件有效期")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setCertificateEffectiveDate(objValue);
                            }
                        }

                        // 投保人-公司名称验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-公司名称")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setCompanyName(objValue);
                            }
                        }
                        // 投保人-公司业务性质验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-公司业务性质")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setIndustry(objValue);
                            }
                        }
                        // 投保人-职务验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-职务")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setJob(objValue);
                            }
                        }
                        // 投保人-职业验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-职业")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setPosition(objValue);
                            }
                        }
                        // 投保人-每月收入(港币)验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-每月收入(港币)")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setIncome(Long.valueOf(objValue));
                            }
                        }
                        // 投保人-手机号验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-手机号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String[] phone = objValue.split("-");
                                if (phone.length >= 2) {
                                    applicant.setPhoneCode(phone[0]);
                                    applicant.setPhone(phone[1]);
                                } else if (phone.length == 1) {
                                    applicant.setPhoneCode("");
                                    applicant.setPhone(phone[0]);
                                }
                            }
                        }
                        // 投保人-电子邮箱验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-电子邮箱")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setEmail(objValue);
                            }
                        }
                        // 投保人-公司地址验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-公司地址")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setCompanyAddress(objValue);
                            }
                        }
                        // 投保人-是否为美国公民验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-是否为美国公民")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "SYS.YES_NO", objValue);

                                if(valueCode==null){
                                    applicant.setAmericanCitizenFlag("");
                                    errorMessage.append(messageSource.getMessage("投保人-是否为美国公民在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setAmericanCitizenFlag(valueCode);
                                }
                            }
                        }
                        // 投保人-婚姻状况验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-婚姻状况")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "CTM.MARITAL_STATUS", objValue);

                                if(valueCode==null){
                                    applicant.setMarriageStatus("");
                                    errorMessage.append(messageSource.getMessage("投保人-婚姻状况在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setMarriageStatus(valueCode);
                                }
                            }
                        }
                        // 投保人-身高(CM)验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-身高(CM)")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (objValue.matches("^[0-9]*$")) {
                                    try {
                                        applicant.setHeight(Long.valueOf(objValue));
                                    } catch(Exception e) {
                                        errorMessage.append("投保人-身高(CM)数据格式异常：必须为整数数值;");
                                    }
                                } else {
                                    errorMessage.append("投保人-身高(CM)数据格式异常：必须为整数数值;");
                                }
                            }
                        }
                        // 投保人-体重(KG)验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-体重(KG)")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (objValue.matches("^[0-9]*$")) {
                                    try {
                                        applicant.setWeight(Long.valueOf(objValue));
                                    } catch(Exception e) {
                                        errorMessage.append("投保人-体重(KG)数据格式异常：必须为正整数数值;");
                                    }
                                } else {
                                    errorMessage.append("投保人-体重(KG)数据格式异常：必须为正整数数值;");
                                }
                            }
                        }
                        // 投保人-身份证地址国家验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-身份证地址国家")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.NATION", objValue);

                                if(valueCode==null){
                                    applicant.setIdentityNation("");
                                    errorMessage.append(messageSource.getMessage("投保人-身份证地址国家在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setIdentityNation(valueCode);
                                }
                            }
                        }
                        // 投保人-身份证地址省验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-身份证地址省")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.PROVICE", objValue);

                                if(valueCode==null){
                                    applicant.setIdentityProvince("");
                                    errorMessage.append(messageSource.getMessage("投保人-身份证地址省在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setIdentityProvince(valueCode);
                                }
                            }
                        }
                        // 投保人-身份证地址市验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-身份证地址市")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.CITY", objValue);

                                if(valueCode==null){
                                    applicant.setIdentityCity("");
                                    errorMessage.append(messageSource.getMessage("投保人-身份证地址市在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setIdentityCity(valueCode);
                                }
                            }
                        }
                        // 投保人-身份证地址验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-身份证地址")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setIdentityAddress(objValue);
                            }
                        }
                        // 投保人-是否以身份证地址作为地址证明验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-是否以身份证地址作为地址证明")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "SYS.YES_NO", objValue);

                                if(valueCode==null){
                                    applicant.setIdentityFlag("");
                                    errorMessage.append(messageSource.getMessage("投保人-是否以身份证地址作为地址证明在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setIdentityFlag(valueCode);
                                }
                            }
                        }
                        // 投保人-通讯地址国家验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-通讯地址国家")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.NATION", objValue);

                                if(valueCode==null){
                                    applicant.setPostNation("");
                                    errorMessage.append(messageSource.getMessage("投保人-通讯地址国家在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setPostNation(valueCode);
                                }
                            }
                        }
                        // 投保人-通讯地址省验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-通讯地址省")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.PROVICE", objValue);

                                if(valueCode==null){
                                    applicant.setPostProvince("");
                                    errorMessage.append(messageSource.getMessage("投保人-通讯地址省在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setPostProvince(valueCode);
                                }
                            }
                        }
                        // 投保人-通讯地址市验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-通讯地址市")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.CITY", objValue);

                                if(valueCode==null){
                                    applicant.setPostCity("");
                                    errorMessage.append(messageSource.getMessage("投保人-通讯地址市在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setPostCity(valueCode);
                                }
                            }
                        }
                        // 投保人-通讯地址详细验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-通讯地址详细")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setPostAddress(objValue);
                            }
                        }

                        // 投保人-保费资金来源验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-保费资金来源")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setPremiumSource(objValue);
                            }
                        }

                        // 投保人-平均月支出（港币）验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-平均月支出（港币）")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setAmountPerMonth(Double.valueOf(objValue));
                            }
                        }
                        // 投保人-持有流动资产（港币）验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-持有流动资产（港币）")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setCurrentAssets(Double.valueOf(objValue));
                            }
                        }
                        // 投保人-不动资产总值（港币）验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-不动资产总值（港币）")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setFixedAssets(Double.valueOf(objValue));
                            }
                        }
                        // 投保人-保费占个人年收入比例验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-保费占个人年收入比例")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setPremiumRate(objValue);
                            }
                        }
                        // 投保人-个人负债（港币）验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-个人负债（港币）")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setLiabilities(Double.valueOf(objValue));
                            }
                        }

                        // 投保人-是否被保险公司拒保，增加保费验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-是否被保险公司拒保，增加保费")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "SYS.YES_NO", objValue);

                                if(valueCode==null){
                                    applicant.setBadFlag("");
                                    errorMessage.append(messageSource.getMessage("投保人-是否被保险公司拒保，增加保费验证在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setBadFlag(valueCode);
                                }
                            }
                        }

                        // 投保人-是否因伤病健康理由申请社会福利或保险赔偿
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-是否因伤病健康理由申请社会福利或保险赔偿")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "SYS.YES_NO", objValue);

                                if(valueCode==null){
                                    applicant.setCompensateFlag("");
                                    errorMessage.append(messageSource.getMessage("投保人-是否因伤病健康理由申请社会福利或保险赔偿在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    applicant.setCompensateFlag(valueCode);
                                }
                            }
                        }

                        // 投保人-承包保险公司验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-承包保险公司")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if ("Y".equals(applicant.getBadFlag()) || "Y".equals(applicant.getCompensateFlag())) {
                                    applicant.setBadInsuranceName(objValue);
                                }
                            }
                        }
                        // 投保人-保险种类验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-保险种类")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if ("Y".equals(applicant.getBadFlag()) || "Y".equals(applicant.getCompensateFlag())) {
                                    applicant.setBadInsuranceType(objValue);
                                }
                            }
                        }
                        // 投保人-保单生效日验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-保单生效日")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if ("Y".equals(applicant.getBadFlag()) || "Y".equals(applicant.getCompensateFlag())) {
                                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                    SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                    try {
                                        Date date=sdf.parse(objValue);
                                        applicant.setBadEffactiveDate(date);
                                    } catch (Exception e) {
                                        try {
                                            Date date=sdf1.parse(objValue);
                                            applicant.setBadEffactiveDate(date);
                                        } catch (Exception ee) {
                                            applicant.setBadEffactiveDate(null);
                                            errorMessage.append(messageSource.getMessage("投保人-保单生效日格式错误", new Object[]{
                                                    objTitle.trim()}, locale)).append(";");
                                        }
                                    }
                                }
                            }
                        }
                        // 投保人-是否抽烟验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-是否抽烟")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setSmokeDesc(objValue);
                                applicant.setSmokeFlag("Y");
                            }
                        }
                        // 投保人-是否喝酒验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-是否喝酒")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setDrinkDesc(objValue);
                                applicant.setDrinkFlag("Y");
                            }
                        }
                        // 投保人-是否药物成瘾验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-是否药物成瘾")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setDrugDesc(objValue);
                                applicant.setDrugFlag("Y");
                            }
                        }
                        // 投保人-危险性活动验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-危险性活动")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setDangerousDesc(objValue);
                                applicant.setDangerousFlag("Y");
                            }
                        }
                        // 投保人-居住国外逗留验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-居住国外逗留")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setAbroadDesc(objValue);
                                applicant.setAbroadFlag("Y");
                            }
                        }
                        // 投保人-身体残疾验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-身体残疾")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setDisabilityDesc(objValue);
                                applicant.setDisabilityFlag("Y");
                            }
                        }
                        // 投保人-精神病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-精神病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setSpiritDesc(objValue);
                                applicant.setSpiritFlag("Y");
                            }
                        }
                        // 投保人-内分泌失调验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-内分泌失调")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setEndocrineDesc(objValue);
                                applicant.setEndocrineFlag("Y");
                            }
                        }
                        // 投保人-眼耳鼻喉验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-眼耳鼻喉")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setFaceDesc(objValue);
                                applicant.setFaceFlag("Y");
                            }
                        }
                        // 投保人-呼吸系统疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-呼吸系统疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setRespirationDesc(objValue);
                                applicant.setRespirationFlag("Y");
                            }
                        }
                        // 投保人-三高验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-三高")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setThreeDesc(objValue);
                                applicant.setThreeFlag("Y");
                            }
                        }
                        // 投保人-循环系统疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-循环系统疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setCycleDesc(objValue);
                                applicant.setCycleFlag("Y");
                            }
                        }
                        // 投保人-消化系统疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-消化系统疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setDigestionDesc(objValue);
                                applicant.setDigestionFlag("Y");
                            }
                        }
                        // 投保人-肝脏疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-肝脏疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setLiverDesc(objValue);
                                applicant.setLiverFlag("Y");
                            }
                        }
                        // 投保人-生殖系统疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-生殖系统疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setReproductionDesc(objValue);
                                applicant.setReproductionFlag("Y");
                            }
                        }
                        // 投保人-骨骼疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-骨骼疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setJointDesc(objValue);
                                applicant.setJointFlag("Y");
                            }
                        }
                        // 投保人-癌症肿瘤验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-癌症肿瘤")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setTumorDesc(objValue);
                                applicant.setTumorFlag("Y");
                            }
                        }
                        // 投保人-血液疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-血液疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setBloodDesc(objValue);
                                applicant.setBloodFlag("Y");
                            }
                        }
                        // 投保人-血液疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-血液疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setBloodDesc(objValue);
                                applicant.setBloodFlag("Y");
                            }
                        }
                        // 投保人-艾滋病辅导或治疗验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-艾滋病辅导或治疗")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setAidsDesc(objValue);
                                applicant.setAidsFlag("Y");
                            }
                        }
                        // 投保人-艾滋病体测验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-艾滋病体测")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setAidsTestDesc(objValue);
                                applicant.setAidsTestFlag("Y");
                            }
                        }
                        // 投保人-3月内不正常生理状况验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-3月内不正常生理状况")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setSkinDesc(objValue);
                                applicant.setSkinFlag("Y");
                            }
                        }
                        // 投保人-未提及的其他疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-未提及的其他疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setOtherDesc(objValue);
                                applicant.setOtherFlag("Y");
                            }
                        }
                        // 投保人-正在接受的治疗验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-正在接受的治疗")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setOtherTreatDesc(objValue);
                                applicant.setOtherTreatFlag("Y");
                            }
                        }
                        // 投保人-5年内接受过的检查验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-5年内接受过的检查")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setExaminationDesc(objValue);
                                applicant.setExaminationFlag("Y");
                            }
                        }
                        // 投保人-直系亲属遗传病史验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-直系亲属遗传病史")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setHereditaryDesc(objValue);
                                applicant.setHereditaryFlag("Y");
                            }
                        }
                        // 投保人-是否怀孕验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-是否怀孕")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setPregnancyDesc(objValue);
                                applicant.setPregnancyFlag("Y");
                            }
                        }
                        // 投保人-唐氏综合症测试验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-唐氏综合症测试")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setDownTestDesc(objValue);
                                applicant.setDownTestFlag("Y");
                            }
                        }
                        // 投保人-因妇科问题看医生验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-因妇科问题看医生")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setGynecologyDesc(objValue);
                                applicant.setGynecologyFlag("Y");
                            }
                        }
                        // 投保人-10年内的怀孕并发症验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-10年内的怀孕并发症")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setComplicationDesc(objValue);
                                applicant.setComplicationFlag("Y");
                            }
                        }
                        // 投保人-是否接受过子宫乳房检查验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("投保人-是否接受过子宫乳房检查")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                applicant.setGynecologyOthDesc(objValue);
                                applicant.setGynecologyOthFlag("Y");
                            }
                        }

                        // 受保人-姓名验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-姓名")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setChineseName(objValue);
                            } else {
                                insurant.setChineseName("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 受保人-姓名拼音验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-姓名拼音")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setEnglishName(objValue.toUpperCase());
                            }
                        }

                        // 受保人-国籍验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-国籍")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.NATION", objValue);

                                if(valueCode==null){
                                    insurant.setNationality("");
                                    errorMessage.append(messageSource.getMessage("受保人-国籍在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setNationality(valueCode);
                                }
                            }
                        }

                        // 受保人-教育程度验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-教育程度")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.EDUCATION", objValue);

                                if(valueCode==null){
                                    insurant.setEducation("");
                                    errorMessage.append(messageSource.getMessage("受保人-教育程度在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setEducation(valueCode);
                                }
                            }
                        }

                        // 受保人-出生日期验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-出生日期")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                try {
                                    Date date=sdf.parse(objValue);
                                    insurant.setBirthDate(date);
                                } catch (Exception e) {
                                    try {
                                        Date date=sdf1.parse(objValue);
                                        insurant.setBirthDate(date);
                                    } catch (Exception ee) {
                                        insurant.setBirthDate(null);
                                        errorMessage.append(messageSource.getMessage("受保人-出生日期格式错误", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        // 受保人-性别验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-性别")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "HR.EMPLOYEE_GENDER", objValue);

                                if(valueCode==null){
                                    insurant.setSex("");
                                    errorMessage.append(messageSource.getMessage("受保人-性别在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setSex(valueCode);
                                }
                            }
                        }

                        // 受保人-身份证号验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-身份证号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                CtmCustomer ctmCustomer = new CtmCustomer();
                                ctmCustomer.setIdentityNumber(objValue);
                                List<CtmCustomer> ctmCustomers = ctmCustomerMapper.select(ctmCustomer);
                                if(CollectionUtils.isEmpty(ctmCustomers)){
                                    insurant.setIdentityNumber(objValue);
                                    ordOrder.setInsurantIdentityNumber(objValue);
                                    ordOrder.setInsurantCustomerId(null);
//                                    errorMessage.append("受保人-身份证号在系统中不存在", new Object[]{
//                                            objTitle.trim()}, locale)).append(";");
//                                    continue;
                                }else {
                                    insurant.setIdentityNumber(objValue);
                                    ordOrder.setInsurantIdentityNumber(objValue);
                                    ordOrder.setInsurantCustomerId(ctmCustomers.get(0).getCustomerId());
                                }

                            } else {
                                insurant.setIdentityNumber("");
                                ordOrder.setInsurantIdentityNumber("");
                                ordOrder.setInsurantCustomerId(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 受保人-其他证件类型验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-其他证件类型")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "CTM.CERTIFICATE_TYPE", objValue);

                                if(valueCode==null){
                                    insurant.setCertificateType("");
                                    errorMessage.append(messageSource.getMessage("受保人-其他证件类型在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setCertificateType(valueCode);
                                }
                            }
                        }

                        // 受保人-其他证件号验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-其他证件号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setCertificateNumber(objValue);
                            }
                        }

                        // 受保人-其他证件有效期验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-其他证件有效期")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setCertificateEffectiveDate(objValue);
                            }
                        }

                        // 受保人-公司名称验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-公司名称")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setCompanyName(objValue);
                            }
                        }
                        // 受保人-公司业务性质验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-公司业务性质")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setIndustry(objValue);
                            }
                        }
                        // 受保人-职务验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-职务")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setJob(objValue);
                            }
                        }
                        // 受保人-职业验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-职业")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setPosition(objValue);
                            }
                        }
                        // 受保人-每月收入(港币)验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-每月收入(港币)")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setIncome(Long.valueOf(objValue));
                            }
                        }
                        // 受保人-手机号验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-手机号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String[] phone = objValue.split("-");
                                if (phone.length >= 2) {
                                    insurant.setPhoneCode(phone[0]);
                                    insurant.setPhone(phone[1]);
                                } else if (phone.length == 1) {
                                    insurant.setPhoneCode("");
                                    insurant.setPhone(phone[0]);
                                }
                            }
                        }
                        // 受保人-电子邮箱验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-电子邮箱")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setEmail(objValue);
                            }
                        }
                        // 受保人-公司地址验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-公司地址")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setCompanyAddress(objValue);
                            }
                        }
                        // 受保人-是否为美国公民验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-是否为美国公民")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "SYS.YES_NO", objValue);

                                if(valueCode==null){
                                    insurant.setAmericanCitizenFlag("");
                                    errorMessage.append(messageSource.getMessage("受保人-是否为美国公民在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setAmericanCitizenFlag(valueCode);
                                }
                            }
                        }
                        // 受保人-婚姻状况验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-婚姻状况")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "CTM.MARITAL_STATUS", objValue);

                                if(valueCode==null){
                                    insurant.setMarriageStatus("");
                                    errorMessage.append(messageSource.getMessage("受保人-婚姻状况在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setMarriageStatus(valueCode);
                                }
                            }
                        }
                        // 受保人-身高(CM)验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-身高(CM)")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setHeight(Long.valueOf(objValue));
                            }
                        }
                        // 受保人-体重(KG)验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-体重(KG)")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setWeight(Long.valueOf(objValue));
                            }
                        }
                        // 受保人-身份证地址国家验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-身份证地址国家")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.NATION", objValue);

                                if(valueCode==null){
                                    insurant.setIdentityNation("");
                                    errorMessage.append(messageSource.getMessage("受保人-身份证地址国家在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setIdentityNation(valueCode);
                                }
                            }
                        }
                        // 受保人-身份证地址省验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-身份证地址省")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.PROVICE", objValue);

                                if(valueCode==null){
                                    insurant.setIdentityProvince("");
                                    errorMessage.append(messageSource.getMessage("受保人-身份证地址省在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setIdentityProvince(valueCode);
                                }
                            }
                        }
                        // 受保人-身份证地址市验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-身份证地址市")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.CITY", objValue);

                                if(valueCode==null){
                                    insurant.setIdentityCity("");
                                    errorMessage.append(messageSource.getMessage("受保人-身份证地址市在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setIdentityCity(valueCode);
                                }
                            }
                        }
                        // 受保人-身份证地址验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-身份证地址")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setIdentityAddress(objValue);
                            }
                        }
                        // 受保人-是否以身份证地址作为地址证明验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-是否以身份证地址作为地址证明")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "SYS.YES_NO", objValue);

                                if(valueCode==null){
                                    insurant.setIdentityFlag("");
                                    errorMessage.append(messageSource.getMessage("受保人-是否以身份证地址作为地址证明在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setIdentityFlag(valueCode);
                                }
                            }
                        }
                        // 受保人-通讯地址国家验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-通讯地址国家")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.NATION", objValue);

                                if(valueCode==null){
                                    insurant.setPostNation("");
                                    errorMessage.append(messageSource.getMessage("受保人-通讯地址国家在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setPostNation(valueCode);
                                }
                            }
                        }
                        // 受保人-通讯地址省验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-通讯地址省")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.PROVICE", objValue);

                                if(valueCode==null){
                                    insurant.setPostProvince("");
                                    errorMessage.append(messageSource.getMessage("受保人-通讯地址省在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setPostProvince(valueCode);
                                }
                            }
                        }
                        // 受保人-通讯地址市验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-通讯地址市")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.CITY", objValue);

                                if(valueCode==null){
                                    insurant.setPostCity("");
                                    errorMessage.append(messageSource.getMessage("受保人-通讯地址市在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setPostCity(valueCode);
                                }
                            }
                        }
                        // 受保人-通讯地址详细验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-通讯地址详细")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setPostAddress(objValue);
                            }
                        }

                        // 受保人-保费资金来源验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-保费资金来源")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setPremiumSource(objValue);
                            }
                        }

                        // 受保人-平均月支出（港币）验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-平均月支出（港币）")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setAmountPerMonth(Double.valueOf(objValue));
                            }
                        }
                        // 受保人-持有流动资产（港币）验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-持有流动资产（港币）")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setCurrentAssets(Double.valueOf(objValue));
                            }
                        }
                        // 受保人-不动资产总值（港币）验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-不动资产总值（港币）")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setFixedAssets(Double.valueOf(objValue));
                            }
                        }
                        // 受保人-保费占个人年收入比例验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-保费占个人年收入比例")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setPremiumRate(StringUtils.replace(objValue,"%",""));
                            }
                        }
                        // 受保人-个人负债（港币）验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-个人负债（港币）")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setLiabilities(Double.valueOf(objValue));
                            }
                        }

                        // 受保人-是否被保险公司拒保，增加保费验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-是否被保险公司拒保，增加保费")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "SYS.YES_NO", objValue);

                                if(valueCode==null){
                                    insurant.setBadFlag("");
                                    errorMessage.append(messageSource.getMessage("受保人-是否被保险公司拒保，增加保费验证在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setBadFlag(valueCode);
                                }
                            }
                        }

                        // 受保人-是否因伤病健康理由申请社会福利或保险赔偿
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-是否因伤病健康理由申请社会福利或保险赔偿")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "SYS.YES_NO", objValue);

                                if(valueCode==null){
                                    insurant.setCompensateFlag("");
                                    errorMessage.append(messageSource.getMessage("受保人-是否因伤病健康理由申请社会福利或保险赔偿在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    insurant.setCompensateFlag(valueCode);
                                }
                            }
                        }

                        // 受保人-承包保险公司验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-承包保险公司")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if ("Y".equals(insurant.getBadFlag()) || "Y".equals(insurant.getCompensateFlag())) {
                                    insurant.setBadInsuranceName(objValue);
                                }
                            }
                        }
                        // 受保人-保险种类验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-保险种类")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if ("Y".equals(insurant.getBadFlag()) || "Y".equals(insurant.getCompensateFlag())) {
                                    insurant.setBadInsuranceType(objValue);
                                }
                            }
                        }
                        // 受保人-保单生效日验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-保单生效日")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if ("Y".equals(insurant.getBadFlag()) || "Y".equals(insurant.getCompensateFlag())) {
                                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                                    SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟
                                    try {
                                        Date date=sdf.parse(objValue);
                                        insurant.setBadEffactiveDate(date);
                                    } catch (Exception e) {
                                        try {
                                            Date date=sdf1.parse(objValue);
                                            insurant.setBadEffactiveDate(date);
                                        } catch (Exception ee) {
                                            insurant.setBadEffactiveDate(null);
                                            errorMessage.append(messageSource.getMessage("受保人-保单生效日格式错误", new Object[]{
                                                    objTitle.trim()}, locale)).append(";");
                                        }
                                    }
                                }
                            }
                        }
                        // 受保人-是否抽烟验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-是否抽烟")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setSmokeDesc(objValue);
                                insurant.setSmokeFlag("Y");
                            }
                        }
                        // 受保人-是否喝酒验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-是否喝酒")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setDrinkDesc(objValue);
                                insurant.setDrinkFlag("Y");
                            }
                        }
                        // 受保人-是否药物成瘾验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-是否药物成瘾")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setDrugDesc(objValue);
                                insurant.setDrugFlag("Y");
                            }
                        }
                        // 受保人-危险性活动验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-危险性活动")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setDangerousDesc(objValue);
                                insurant.setDangerousFlag("Y");
                            }
                        }
                        // 受保人-居住国外逗留验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-居住国外逗留")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setAbroadDesc(objValue);
                                insurant.setAbroadFlag("Y");
                            }
                        }
                        // 受保人-身体残疾验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-身体残疾")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setDisabilityDesc(objValue);
                                insurant.setDisabilityFlag("Y");
                            }
                        }
                        // 受保人-精神病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-精神病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setSpiritDesc(objValue);
                                insurant.setSpiritFlag("Y");
                            }
                        }
                        // 受保人-内分泌失调验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-内分泌失调")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setEndocrineDesc(objValue);
                                insurant.setEndocrineFlag("Y");
                            }
                        }
                        // 受保人-眼耳鼻喉验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-眼耳鼻喉")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setFaceDesc(objValue);
                                insurant.setFaceFlag("Y");
                            }
                        }
                        // 受保人-呼吸系统疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-呼吸系统疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setRespirationDesc(objValue);
                                insurant.setRespirationFlag("Y");
                            }
                        }
                        // 受保人-三高验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-三高")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setThreeDesc(objValue);
                                insurant.setThreeFlag("Y");
                            }
                        }
                        // 受保人-循环系统疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-循环系统疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setCycleDesc(objValue);
                                insurant.setCycleFlag("Y");
                            }
                        }
                        // 受保人-消化系统疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-消化系统疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setDigestionDesc(objValue);
                                insurant.setDigestionFlag("Y");
                            }
                        }
                        // 受保人-肝脏疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-肝脏疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setLiverDesc(objValue);
                                insurant.setLiverFlag("Y");
                            }
                        }
                        // 受保人-生殖系统疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-生殖系统疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setReproductionDesc(objValue);
                                insurant.setReproductionFlag("Y");
                            }
                        }
                        // 受保人-骨骼疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-骨骼疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setJointDesc(objValue);
                                insurant.setJointFlag("Y");
                            }
                        }
                        // 受保人-癌症肿瘤验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-癌症肿瘤")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setTumorDesc(objValue);
                                insurant.setTumorFlag("Y");
                            }
                        }
                        // 受保人-血液疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-血液疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setBloodDesc(objValue);
                                insurant.setBloodFlag("Y");
                            }
                        }
                        // 受保人-血液疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-血液疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setBloodDesc(objValue);
                                insurant.setBloodFlag("Y");
                            }
                        }
                        // 受保人-艾滋病辅导或治疗验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-艾滋病辅导或治疗")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setAidsDesc(objValue);
                                insurant.setAidsFlag("Y");
                            }
                        }
                        // 受保人-艾滋病体测验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-艾滋病体测")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setAidsTestDesc(objValue);
                                insurant.setAidsTestFlag("Y");
                            }
                        }
                        // 受保人-3月内不正常生理状况验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-3月内不正常生理状况")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setSkinDesc(objValue);
                                insurant.setSkinFlag("Y");
                            }
                        }
                        // 受保人-未提及的其他疾病验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-未提及的其他疾病")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setOtherDesc(objValue);
                                insurant.setOtherFlag("Y");
                            }
                        }
                        // 受保人-正在接受的治疗验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-正在接受的治疗")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setOtherTreatDesc(objValue);
                                insurant.setOtherTreatFlag("Y");
                            }
                        }
                        // 受保人-5年内接受过的检查验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-5年内接受过的检查")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setExaminationDesc(objValue);
                                insurant.setExaminationFlag("Y");
                            }
                        }
                        // 受保人-直系亲属遗传病史验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-直系亲属遗传病史")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setHereditaryDesc(objValue);
                                insurant.setHereditaryFlag("Y");
                            }
                        }
                        // 受保人-是否怀孕验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-是否怀孕")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setPregnancyDesc(objValue);
                                insurant.setPregnancyFlag("Y");
                            }
                        }
                        // 受保人-唐氏综合症测试验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-唐氏综合症测试")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setDownTestDesc(objValue);
                                insurant.setDownTestFlag("Y");
                            }
                        }
                        // 受保人-因妇科问题看医生验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-因妇科问题看医生")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setGynecologyDesc(objValue);
                                insurant.setGynecologyFlag("Y");
                            }
                        }
                        // 受保人-10年内的怀孕并发症验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-10年内的怀孕并发症")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setComplicationDesc(objValue);
                                insurant.setComplicationFlag("Y");
                            }
                        }
                        // 受保人-是否接受过子宫乳房检查验证
                        else if (importTemp.getSheet().equals("投保人和受保人")
                                && objTitle.trim().equals("受保人-是否接受过子宫乳房检查")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                insurant.setGynecologyOthDesc(objValue);
                                insurant.setGynecologyOthFlag("Y");
                            }
                        }

                        // 订单编号验证
                        else if (importTemp.getSheet().equals("受益人")
                                && objTitle.trim().equals("订单编号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (orderNumberList.contains(objValue)) {
                                    ordBeneficiary.setOrderNumber(objValue);
                                } else {
                                    ordBeneficiary.setOrderNumber("");
                                    errorMessage.append(messageSource.getMessage("订单编号在订单预约数据中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                ordBeneficiary.setOrderNumber("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }

                        }

                        // 受益人名验证
                        else if (importTemp.getSheet().equals("受益人")
                                && objTitle.trim().equals("受益人名")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordBeneficiary.setChineseName(objValue);
                            } else {
                                ordBeneficiary.setChineseName("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 年龄验证
                        else if (importTemp.getSheet().equals("受益人")
                                && objTitle.trim().equals("年龄")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (objValue.matches("^[0-9]*$")) {
                                    try {
                                        ordBeneficiary.setAge(Long.valueOf(objValue));
                                    } catch(Exception e) {
                                        errorMessage.append("年龄数据格式异常：必须为正整数数值;");
                                    }
                                } else {
                                    errorMessage.append("年龄数据格式异常：必须为正整数数值;");
                                }
                            }
                        }

                        // 身份证号验证
                        else if (importTemp.getSheet().equals("受益人")
                                && objTitle.trim().equals("身份证号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordBeneficiary.setIdentityNumber(objValue);
                            }
                        }
                        // 与受保人关系验证
                        else if (importTemp.getSheet().equals("受益人")
                                && objTitle.trim().equals("与受保人关系")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordBeneficiary.setRelationship(objValue);
                            }
                        }
                        // 百分比验证
                        else if (importTemp.getSheet().equals("受益人")
                                && objTitle.trim().equals("百分比")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                try {
                                    ordBeneficiary.setRate(new BigDecimal(objValue));
                                } catch(Exception e) {
                                    errorMessage.append("受益人-百分比数据格式异常：必须为1-100之间的数值;");
                                }
                            } else {
                                ordBeneficiary.setRate(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 受益人英文名验证
                        else if (importTemp.getSheet().equals("受益人")
                                && objTitle.trim().equals("受益人英文名")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordBeneficiary.setEnglishName(objValue.toUpperCase());
                            }
                        }

                        // 订单编号验证
                        else if (importTemp.getSheet().equals("交易路线")
                                && objTitle.trim().equals("订单编号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (orderNumberList.contains(objValue)) {
                                    ordTradeRoute1.setOrderNumber(objValue);
                                    ordTradeRouteShow.setOrderNumber(objValue);
                                } else {
                                    ordTradeRoute1.setOrderNumber(objValue);
                                    ordTradeRouteShow.setOrderNumber("");
                                    errorMessage.append(messageSource.getMessage("订单编号在订单预约数据中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                ordTradeRoute1.setOrderNumber("");
                                ordTradeRouteShow.setOrderNumber("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }

                        }

                        // 渠道交易路线验证
                        else if (importTemp.getSheet().equals("交易路线")
                                && objTitle.trim().equals("渠道交易路线")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String[] channels = objValue.split("-");
                                String flag = "Y";
                                int length = channels.length;
                                int channelSeq = 0;

                                ordTradeRouteShow.setChannelCount(Long.valueOf(channels.length));

                                for (int j=0;j<channels.length;j++) {
//                                for (String s:channels) {
                                    String s = channels[j];
                                    CnlChannel cnlChannel = new CnlChannel();
                                    cnlChannel.setChannelName(s);
                                    List<CnlChannel> cnlChannels = cnlChannelMapper.select(cnlChannel);
                                    if(CollectionUtils.isEmpty(cnlChannels)){
                                        flag = "N";
                                        errorMessage.append(messageSource.getMessage("交易路线中渠道不存在" + objValue, new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                    } else {
                                        seqNum = seqNum + 1L;
                                        channelSeq = channelSeq + 1;
                                        String ps = null;
                                        Long channelContractId = null;
                                        if (j + 1 < channels.length) {
                                            ps =  channels[j+1];
                                        }

                                        if (ps != null) {
                                            CnlChannelContract cnlChannelContract = new CnlChannelContract();
                                            cnlChannelContract.setChannelId(cnlChannels.get(0).getChannelId());
                                            cnlChannelContract.setPartyType("CHANNEL");
                                            cnlChannelContract.setPartyName(ps);
                                            cnlChannelContract.setProductDivision("BX");
                                            List<CnlChannelContract> contracts = cnlChannelContractMapper.queryContract(cnlChannelContract);
                                            if (CollectionUtils.isEmpty(contracts)) {
                                                channelContractId = null;
                                                flag = "N";
                                                errorMessage.append(messageSource.getMessage("交易路线中合约不存在" + objValue, new Object[]{
                                                        objTitle.trim()}, locale)).append(";");
                                            } else {
                                                channelContractId = contracts.get(0).getChannelContractId();
                                            }
                                        }

                                        Method method = ImportUtil.getMethod(routeCla, "channelId" + channelSeq ,"set",Long.class);
                                        if(method != null){
                                            try {
                                                method.invoke(ordTradeRouteShow,cnlChannels.get(0).getChannelId());

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        method = ImportUtil.getMethod(routeCla, "channelName" + channelSeq ,"set",String.class);
                                        if(method != null){
                                            try {
                                                method.invoke(ordTradeRouteShow,s);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        OrdTradeRoute ordTradeRoute = new OrdTradeRoute();
                                        ordTradeRoute.setOrderNumber(ordTradeRoute1.getOrderNumber());
                                        ordTradeRoute1.setCompanyName(s);
                                        ordTradeRoute.setCompanyName(s);
                                        ordTradeRoute.setSeqNum(seqNum);
                                        ordTradeRoute.setChannelCount(Long.valueOf(channels.length));
                                        ordTradeRoute.setChannelLevel(Long.valueOf(length));
                                        ordTradeRoute.setCompanyId(cnlChannels.get(0).getChannelId());
                                        ordTradeRoute.setCompanyType("CHANNEL");
                                        ordTradeRoute.setSupplierType("");
                                        ordTradeRoute.setChannelContractId(channelContractId);
                                        length = length - 1;
                                        ordTradeRoute.setAttribute1(importBatchId.toString());
                                        ordTradeRoute.set__status("add");
                                        if (StringUtils.isEmpty(dealPath)) {
                                            dealPath = "C" + cnlChannels.get(0).getChannelId();
                                        } else {
                                            dealPath = dealPath + '.' + "C" + cnlChannels.get(0).getChannelId();
                                        }
                                        ordTradeRoutes.add(ordTradeRoute);
                                    }
                                }
                            }
                        }

                        // 供应商交易路线验证
                        else if (importTemp.getSheet().equals("交易路线")
                                && objTitle.trim().equals("供应商交易路线")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String[] suppliers = objValue.split("-");
                                String flag = "Y";
                                int k = 0;
                                int supplierSeq = 0;
                                int length = suppliers.length;

                                for (String s:suppliers) {
                                    SpeSupplier speSupplier = new SpeSupplier();
                                    speSupplier.setName(s);
                                    List<SpeSupplier> speSuppliers = speSupplierMapper.select(speSupplier);
                                    if(CollectionUtils.isEmpty(speSuppliers)){
                                        flag = "N";
                                    } else {
                                        k = k + 1;
                                        if (!ordTradeRoute1.getCompanyName().equals(s) || k == length) {
                                            if (k == 1) {
                                                for (OrdTradeRoute o : ordTradeRoutes) {
                                                    if (o.getOrderNumber().equals(ordTradeRoute1.getOrderNumber()) && seqNum.equals(o.getSeqNum())
                                                            && "CHANNEL".equals(o.getCompanyType()) ) {
                                                        CnlChannelContract cnlChannelContract = new CnlChannelContract();
                                                        cnlChannelContract.setChannelId(o.getCompanyId());
                                                        cnlChannelContract.setPartyType("SUPPLIER");
                                                        cnlChannelContract.setPartyName(s);
                                                        cnlChannelContract.setProductDivision("BX");
                                                        List<CnlChannelContract> contracts = cnlChannelContractMapper.queryContract(cnlChannelContract);
                                                        if (CollectionUtils.isEmpty(contracts)) {
                                                            flag = "N";
                                                            errorMessage.append(messageSource.getMessage("交易路线中合约不存在" + o.getCompanyName(), new Object[]{
                                                                    objTitle.trim()}, locale)).append(";");
                                                        } else {
                                                            o.setChannelContractId(contracts.get(0).getChannelContractId());

                                                            for (OrdOrder order:ordOrders) {
                                                                if (order.getOrderNumber().equals(ordTradeRoute1.getOrderNumber()) ) {
                                                                    order.setChannelContractId(contracts.get(0).getChannelContractId());
                                                                }
                                                            }

                                                        }
                                                    }
                                                }
                                            }
                                            seqNum = seqNum + 1L;
                                            supplierSeq = supplierSeq + 1;

                                            Method method = ImportUtil.getMethod(routeCla, "supplierId" + supplierSeq ,"set",Long.class);
                                            if(method != null){
                                                try {
                                                    method.invoke(ordTradeRouteShow,speSuppliers.get(0).getSupplierId());

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                            method = ImportUtil.getMethod(routeCla, "supplierName" + supplierSeq ,"set",String.class);
                                            if(method != null){
                                                try {
                                                    method.invoke(ordTradeRouteShow,s);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                            OrdTradeRoute ordTradeRoute = new OrdTradeRoute();
                                            ordTradeRoute.setOrderNumber(ordTradeRoute1.getOrderNumber());
                                            ordTradeRoute1.setCompanyName(s);
                                            ordTradeRoute.setCompanyName(s);
                                            ordTradeRoute.setSeqNum(seqNum);
                                            ordTradeRoute.setChannelCount(null);
                                            ordTradeRoute.setChannelLevel(null);
                                            ordTradeRoute.setCompanyId(speSuppliers.get(0).getSupplierId());
                                            ordTradeRoute.setCompanyType("SUPPLIER");
                                            if (k == 1) {
                                                ordTradeRoute.setSupplierType("OWN");
                                            } else if (k == 2) {
                                                ordTradeRoute.setSupplierType("RESERVE");
                                            } else if (length > 4 && k > 2 && k < length - 1) {
                                                ordTradeRoute.setSupplierType("MIDDLE");
                                            } else if (k == length - 1) {
                                                ordTradeRoute.setSupplierType("SIGN");
                                            } else if (k == length) {
                                                ordTradeRoute.setSupplierType("INSURANCE");
                                            }
                                            ordTradeRoute.setAttribute1(importBatchId.toString());
                                            ordTradeRoute.set__status("add");

                                            if (StringUtils.isEmpty(dealPath)) {
                                                dealPath = "S" + speSuppliers.get(0).getSupplierId();
                                            } else {
                                                dealPath = dealPath + '.' + "S" + speSuppliers.get(0).getSupplierId();
                                            }

                                            ordTradeRoutes.add(ordTradeRoute);
                                        }
                                    }
                                }
                                ordTradeRouteShow.setSupplierCount(Long.valueOf(supplierSeq));

                                if (StringUtils.isNotEmpty(dealPath)) {
                                    for (OrdOrder order:ordOrders) {
                                        if (order.getOrderNumber().equals(ordTradeRoute1.getOrderNumber()) ) {
                                            order.setDealPath(dealPath);
                                        }
                                    }
                                }
                            }
                        }

                        // 订单编号验证
                        else if (importTemp.getSheet().equals("订单附加险")
                                && objTitle.trim().equals("订单编号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (orderNumberList.contains(objValue)) {
                                    ordAddition.setOrderNumber(objValue);
                                } else {
                                    ordAddition.setOrderNumber("");
                                    errorMessage.append(messageSource.getMessage("订单编号在订单预约数据中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                ordAddition.setOrderNumber("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }

                        }
                        // 附加险产品名验证
                        else if (importTemp.getSheet().equals("订单附加险")
                                && objTitle.trim().equals("附加险产品名")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                PrdItems prdItems1 = new PrdItems();
                                prdItems1.setItemName(objValue);
                                List<PrdItems> prdItemss = prdItemsMapper.selectByItemName(prdItems1);
                                if(CollectionUtils.isEmpty(prdItemss)){
                                    ordAddition.setItemId(null);
                                    ordAddition.setItemName(objValue);
                                    errorMessage.append(messageSource.getMessage("附加险产品名在系统中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordAddition.setItemId(prdItemss.get(0).getItemId());
                                    ordAddition.setItemName(objValue);
                                    prdItems = prdItemss.get(0);
                                }

                            } else {
                                ordAddition.setItemId(null);
                                ordAddition.setItemName(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 年期验证
                        else if (importTemp.getSheet().equals("订单附加险")
                                && objTitle.trim().equals("年期")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                PrdItemSubline prdItemSubline = new PrdItemSubline();
                                prdItemSubline.setSublineItemName(objValue);
                                prdItemSubline.setItemId(ordAddition.getItemId());
                                List<PrdItemSubline> prdItemSublines = prdItemSublineMapper.select(prdItemSubline);
                                if(CollectionUtils.isEmpty(prdItemSublines)){
                                    ordAddition.setSublineId(null);
                                    errorMessage.append(messageSource.getMessage("年期在产品的子产品中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordAddition.setSublineId(prdItemSublines.get(0).getSublineId());
                                }

                            } else {
                                ordAddition.setSublineId(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }


                        // 缴费方式验证
                        else if (importTemp.getSheet().equals("订单附加险")
                                && objTitle.trim().equals("缴费方式")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "CMN.PAY_METHOD", objValue);

                                if(valueCode==null){
                                    errorMessage.append(messageSource.getMessage("缴费方式在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }

                                if ("Y".equals(prdItems.getFullyear()) && "WP".equals(valueCode) ) {
                                    ordAddition.setPayMethod(valueCode);
                                } else if ("Y".equals(prdItems.getOneyear()) && "AP".equals(valueCode) ) {
                                    ordAddition.setPayMethod(valueCode);
                                } else if ("Y".equals(prdItems.getHalfyear()) && "SAP".equals(valueCode) ) {
                                    ordAddition.setPayMethod(valueCode);
                                } else if ("Y".equals(prdItems.getQuarter()) && "QP".equals(valueCode) ) {
                                    ordAddition.setPayMethod(valueCode);
                                } else if ("Y".equals(prdItems.getOnemonth()) && "MP".equals(valueCode) ) {
                                    ordAddition.setPayMethod(valueCode);
                                } else if ("Y".equals(prdItems.getPrepayFlag()) && "FJ".equals(valueCode) ) {
                                    ordOrder.setPayMethod(valueCode);
                                } else {
                                    ordAddition.setPayMethod("");
                                    errorMessage.append(messageSource.getMessage("缴费方式在产品的缴费方式中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }

                            } else {
                                ordAddition.setPayMethod("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 年缴保费验证
                        else if (importTemp.getSheet().equals("订单附加险")
                                && objTitle.trim().equals("年缴保费")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordAddition.setYearPayAmount(new BigDecimal(objValue));
                            } else {
                                ordAddition.setYearPayAmount(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 保额验证
                        else if (importTemp.getSheet().equals("订单附加险")
                                && objTitle.trim().equals("保额")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordAddition.setPolicyAmount(new BigDecimal(objValue));
                            }
                        }

                        // 保障等级验证
                        else if (importTemp.getSheet().equals("订单附加险")
                                && objTitle.trim().equals("保障等级")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                PrdItemSecurityPlan prdItemSecurityPlan = new PrdItemSecurityPlan();
                                prdItemSecurityPlan.setItemId(ordAddition.getItemId());
                                prdItemSecurityPlan.setSecurityLevel(objValue);
                                List<PrdItemSecurityPlan> prdItemSecurityPlans = prdItemSecurityPlanMapper.select(prdItemSecurityPlan);

                                if(CollectionUtils.isEmpty(prdItemSecurityPlans)){
                                    ordAddition.setSecurityLevel(null);
                                    errorMessage.append(messageSource.getMessage("保障等级在产品中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordAddition.setSecurityLevel(objValue);
                                }
                            }
                        }

                        // 保障地区验证
                        else if (importTemp.getSheet().equals("订单附加险")
                                && objTitle.trim().equals("保障地区")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                PrdItemSecurityPlan prdItemSecurityPlan = new PrdItemSecurityPlan();
                                prdItemSecurityPlan.setItemId(ordAddition.getItemId());
                                prdItemSecurityPlan.setSecurityRegion(objValue);
                                List<PrdItemSecurityPlan> prdItemSecurityPlans = prdItemSecurityPlanMapper.select(prdItemSecurityPlan);

                                if(CollectionUtils.isEmpty(prdItemSecurityPlans)){
                                    ordAddition.setSecurityRegion(null);
                                    errorMessage.append(messageSource.getMessage("保障地区在产品中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordAddition.setSecurityRegion(objValue);
                                }
                            }
                        }

                        // 自付选项验证
                        else if (importTemp.getSheet().equals("订单附加险")
                                && objTitle.trim().equals("自付选项")) {

                            if (null != objValue && !"".equals(objValue.trim())) {

                                objValue = objValue.replaceAll("\"", "");

                                PrdItemSelfpay prdItemSelfpay = new PrdItemSelfpay();
                                prdItemSelfpay.setItemId(ordAddition.getItemId());
                                prdItemSelfpay.setSelfpay(objValue);
                                List<PrdItemSelfpay> prdItemSelfpays = prdItemSelfpayMapper.select(prdItemSelfpay);

                                if(CollectionUtils.isEmpty(prdItemSelfpays)){
                                    ordAddition.setSelfpay(null);
                                    errorMessage.append(messageSource.getMessage("自付选项在产品中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordAddition.setSelfpay(objValue);
                                }
                            }
                        }

                        // 状态验证
                        else if (importTemp.getSheet().equals("订单附加险")
                                && objTitle.trim().equals("状态")) {

                            if (null != objValue && !"".equals(objValue.trim())) {

                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "ORD.ORDER_STATUS", objValue);

                                if(valueCode==null){
                                    ordAddition.setStatus("");
                                    errorMessage.append(messageSource.getMessage("状态在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordAddition.setStatus(valueCode);
                                }

                            } else {
                                ordAddition.setStatus("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }
                        }

                        // 订单编号验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("订单编号")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (orderNumberList.contains(objValue)) {
                                    ordCommission.setOrderNumber(objValue);
                                } else {
                                    ordCommission.setOrderNumber("");
                                    errorMessage.append(messageSource.getMessage("订单编号在订单预约数据中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                ordCommission.setOrderNumber("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }

                        }

                        // 主体类型验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("主体类型")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode=clbCodeService.getCodeValueByMeaning(request, "CNL.PARTY_TYPE", objValue);

                                if(valueCode==null){
                                    ordCommission.setCompanyType("");
                                    errorMessage.append(messageSource.getMessage("状态在系统值列表中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordCommission.setCompanyType(valueCode);
                                }
                            } else {
                                ordCommission.setCompanyType("");
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }

                        }

                        // 主体验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("主体")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                Long companyId = null;
                                Long seqNum1 = null;
                                Long channelContractId = null;
                                for (OrdTradeRoute o:ordTradeRoutes) {
                                    if (o.getOrderNumber().equals(ordCommission.getOrderNumber()) && o.getCompanyType().equals(ordCommission.getCompanyType())
                                            && objValue.equals(o.getCompanyName()) && !"INSURANCE".equals(o.getSupplierType())) {
                                        companyId = o.getCompanyId();
                                        seqNum1 = o.getSeqNum();
                                        channelContractId = o.getChannelContractId();
                                    }
                                }

                                if(companyId==null){
                                    ordCommission.setCompanyId(null);
                                    ordCommission.setSeqNum(null);
                                    ordCommission.setChannelContractId(null);
                                    errorMessage.append(messageSource.getMessage("主体在交易路线中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordCommission.setCompanyId(companyId);
                                    ordCommission.setSeqNum(seqNum1);
                                    ordCommission.setChannelContractId(channelContractId);
                                }
                            } else {
                                ordCommission.setCompanyId(null);
                                ordCommission.setSeqNum(null);
                                ordCommission.setChannelContractId(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }

                        }

                        // 产品名验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("产品名")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                Long itemId = null;

                                for (OrdOrder o:ordOrders) {
                                    if (o.getOrderNumber().equals(ordCommission.getOrderNumber()) && o.getItemName().equals(objValue)) {
                                        itemId = o.getItemId();
                                        yearAmount = o.getYearPayAmount();
                                    }
                                }

                                for (OrdAddition o:ordAdditions) {
                                    if (o.getOrderNumber().equals(ordCommission.getOrderNumber()) && o.getItemName().equals(objValue)) {
                                        itemId = o.getItemId();
                                        yearAmount = o.getYearPayAmount();
                                    }
                                }

                                if(itemId==null){
                                    ordCommission.setItemId(null);
                                    errorMessage.append(messageSource.getMessage("产品在订单和附加险中不存在", new Object[]{
                                            objTitle.trim()}, locale)).append(";");
                                    continue;
                                }else {
                                    ordCommission.setItemId(itemId);
                                }
                            } else {
                                ordCommission.setItemId(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }

                        }

                        // 币种验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("币种")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordCommission.setCurrency(objValue);
                            } else {
                                ordCommission.setCurrency(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }

                        }

                        // 第1年佣金率验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("第1年佣金率")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordCommission.setTheFirstYear(new BigDecimal(objValue));
                                if (yearAmount != null) {
                                    ordCommission.setFirstYearAmount(yearAmount.multiply(ordCommission.getTheFirstYear()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                } else {
                                    ordCommission.setFirstYearAmount(null);
                                }
                            } else {
                                ordCommission.setTheFirstYear(null);
                                ordCommission.setFirstYearAmount(null);
                                errorMessage.append(messageSource.getMessage(Constants.FIELD_NO_EMPTY, new Object[] { objTitle.trim() },
                                        locale)).append(";");
                            }

                        }

                        // 第2年佣金率验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("第2年佣金率")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordCommission.setTheSecondYear(new BigDecimal(StringUtils.replace(objValue,"%","")));
                                if (yearAmount != null) {
                                    ordCommission.setSecondYearAmount(yearAmount.multiply(ordCommission.getTheSecondYear()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                } else {
                                    ordCommission.setSecondYearAmount(null);
                                }
                            } else {
                                ordCommission.setTheSecondYear(new BigDecimal(0));
                                ordCommission.setSecondYearAmount(null);
                            }

                        }
                        // 第3年佣金率验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("第3年佣金率")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordCommission.setTheThirdYear(new BigDecimal(StringUtils.replace(objValue,"%","")));
                                if (yearAmount != null) {
                                    ordCommission.setThirdYearAmount(yearAmount.multiply(ordCommission.getTheThirdYear()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                } else {
                                    ordCommission.setThirdYearAmount(null);
                                }
                            } else {
                                ordCommission.setTheThirdYear(new BigDecimal(0));
                                ordCommission.setThirdYearAmount(null);
                            }

                        }
                        // 第4年佣金率验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("第4年佣金率")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordCommission.setTheFourthYear(new BigDecimal(StringUtils.replace(objValue,"%","")));
                                if (yearAmount != null) {
                                    ordCommission.setFourthYearAmount(yearAmount.multiply(ordCommission.getTheFourthYear()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                } else {
                                    ordCommission.setFourthYearAmount(null);
                                }
                            } else {
                                ordCommission.setTheFourthYear(new BigDecimal(0));
                                ordCommission.setFourthYearAmount(null);
                            }

                        }
                        // 第5年佣金率验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("第5年佣金率")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordCommission.setTheFifthYear(new BigDecimal(StringUtils.replace(objValue,"%","")));
                                if (yearAmount != null) {
                                    ordCommission.setFifthYearAmount(yearAmount.multiply(ordCommission.getTheFifthYear()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                } else {
                                    ordCommission.setFifthYearAmount(null);
                                }
                            } else {
                                ordCommission.setTheFifthYear(new BigDecimal(0));
                                ordCommission.setFifthYearAmount(null);
                            }

                        }
                        // 第6年佣金率验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("第6年佣金率")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordCommission.setTheSixthYear(new BigDecimal(StringUtils.replace(objValue,"%","")));
                                if (yearAmount != null) {
                                    ordCommission.setSixthYearAmount(yearAmount.multiply(ordCommission.getTheSixthYear()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                } else {
                                    ordCommission.setSixthYearAmount(null);
                                }
                            } else {
                                ordCommission.setTheSixthYear(new BigDecimal(0));
                                ordCommission.setSixthYearAmount(null);
                            }

                        }
                        // 第7年佣金率验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("第7年佣金率")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordCommission.setTheSeventhYear(new BigDecimal(StringUtils.replace(objValue,"%","")));
                                if (yearAmount != null) {
                                    ordCommission.setSeventhYearAmount(yearAmount.multiply(ordCommission.getTheSeventhYear()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                } else {
                                    ordCommission.setSeventhYearAmount(null);
                                }
                            } else {
                                ordCommission.setTheSeventhYear(new BigDecimal(0));
                                ordCommission.setSeventhYearAmount(null);
                            }

                        }
                        // 第8年佣金率验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("第8年佣金率")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordCommission.setTheEightYear(new BigDecimal(StringUtils.replace(objValue,"%","")));
                                if (yearAmount != null) {
                                    ordCommission.setEightYearAmount(yearAmount.multiply(ordCommission.getTheEightYear()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                } else {
                                    ordCommission.setEightYearAmount(null);
                                }
                            } else {
                                ordCommission.setTheEightYear(new BigDecimal(0));
                                ordCommission.setEightYearAmount(null);
                            }

                        }
                        // 第9年佣金率验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("第9年佣金率")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordCommission.setTheNinthYear(new BigDecimal(StringUtils.replace(objValue,"%","")));
                                if (yearAmount != null) {
                                    ordCommission.setNinthYearAmount(yearAmount.multiply(ordCommission.getTheNinthYear()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                } else {
                                    ordCommission.setNinthYearAmount(null);
                                }
                            } else {
                                ordCommission.setTheNinthYear(new BigDecimal(0));
                                ordCommission.setNinthYearAmount(null);
                            }

                        }
                        // 第10年佣金率验证
                        else if (importTemp.getSheet().equals("订单佣金")
                                && objTitle.trim().equals("第10年佣金率")) {

                            if (null != objValue && !"".equals(objValue.trim())) {
                                ordCommission.setTheTenthYear(new BigDecimal(StringUtils.replace(objValue,"%","")));
                                if (yearAmount != null) {
                                    ordCommission.setTenthYearAmount(yearAmount.multiply(ordCommission.getTheTenthYear()).setScale(2,BigDecimal.ROUND_HALF_UP));
                                } else {
                                    ordCommission.setTenthYearAmount(null);
                                }
                            } else {
                                ordCommission.setTheTenthYear(new BigDecimal(0));
                                ordCommission.setTenthYearAmount(null);
                            }

                        }

                        else {//没有对应的标题，添加导入错误信息--只有第一行需要添加，后续行不需要重复添加
                            if (importTemp.getLineNumber() == 2) {
                                errorTitleMessage.append(messageSource.getMessage(Constants.TITLE_EXCEPTION, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                    }

                    if ("".equals(errorMessage.toString())) {//通过第二次验证,进行数据转换

                        // 校验‘订单编号’唯一性
                        if (StringUtils.isNotEmpty(ordOrder.getOrderNumber())) {
                            if (uniOrdOrderList.contains(ordOrder.getOrderNumber())) {
                                errorMessage.append("该订单编号已存在！").append(";");
                            } else {
                                uniOrdOrderList.add(ordOrder.getOrderNumber());
                            }

                            ordOrder.setAttribute1(importBatchId.toString());   //用Attribute1记录批次号
                            ordOrder.setOrderType("INSURANCE");
                            ordOrder.set__status("add");
                            ordOrders.add(ordOrder);
                        }

                        // 校验‘订单编号’唯一性
                        if (StringUtils.isNotEmpty(applicant.getOrderNumber())) {
                            if (uniOrdCustomerList.contains(applicant.getOrderNumber())) {
                                errorMessage.append("该订单编号已存在！").append(";");
                            } else {
                                uniOrdCustomerList.add(applicant.getOrderNumber());
                            }

                            applicant.setAttribute1(importBatchId.toString());   //用Attribute1记录批次号
                            applicant.setCustomerType("APPLICANT");
                            applicant.set__status("add");
                            ordCustomers.add(applicant);
                            insurant.setAttribute1(importBatchId.toString());   //用Attribute1记录批次号
                            insurant.setCustomerType("INSURANT");
                            insurant.set__status("add");
                            ordCustomers.add(insurant);

                            for (OrdOrder ordOrder1:ordOrders) {
                                if (applicant.getOrderNumber().equals(ordOrder1.getOrderNumber())) {
                                    ordOrder1.setApplicantCustomerId(ordOrder.getApplicantCustomerId());
                                    ordOrder1.setApplicantIdentityNumber(ordOrder.getApplicantIdentityNumber());
                                    ordOrder1.setInsurantCustomerId(ordOrder.getInsurantCustomerId());
                                    ordOrder1.setInsurantIdentityNumber(ordOrder.getInsurantIdentityNumber());
                                    ordOrder1.setSameFlag(ordOrder.getSameFlag());
                                }
                            }
                        }

                        if (StringUtils.isNotEmpty(ordBeneficiary.getOrderNumber())) {
                            ordBeneficiary.setAttribute1(importBatchId.toString());
                            ordBeneficiary.set__status("add");
                            ordBeneficiaries.add(ordBeneficiary);
                        }

                        // 校验‘订单编号’唯一性
                        if (StringUtils.isNotEmpty(ordTradeRoute1.getOrderNumber())) {
                            if (uniOrdTradeRouteList.contains(ordTradeRoute1.getOrderNumber())) {
                                errorMessage.append("该订单编号已存在！").append(";");
                            } else {
                                uniOrdTradeRouteList.add(ordTradeRoute1.getOrderNumber());
                            }
                        }

                        if (StringUtils.isNotEmpty(ordTradeRouteShow.getOrderNumber())) {
                            ordTradeRouteShow.setAttribute1(importBatchId.toString());
                            ordTradeRouteShow.set__status("add");
                            ordTradeRouteShows.add(ordTradeRouteShow);
                        }

                        if (StringUtils.isNotEmpty(ordAddition.getOrderNumber())) {
                            ordAddition.setAttribute1(importBatchId.toString());   //用Attribute1记录批次号
                            ordAddition.set__status("add");
                            ordAdditions.add(ordAddition);
                        }

                        if (StringUtils.isNotEmpty(ordCommission.getOrderNumber())) {
                            ordCommission.setAttribute1(importBatchId.toString());   //用Attribute1记录批次号
                            ordCommission.set__status("add");
                            ordCommissions.add(ordCommission);
                        }
                    }
                }

                //数据转换之后更新数据状态
                importTemp.setImportMessage(errorMessage.toString());//数据转换之后更新错误状态
                importTempService.updateError(importTemp, request);
            }

            if (!"".equals(errorTitleMessage.toString())) {//当前页行标题有错误
                importTempTitle.setImportMessage(errorTitleMessage.toString());
                importTempService.updateError(importTempTitle, request);
            }
        } else {
            throw new ValidationTableException(Constants.DATA_EMPTY, null);
        }

        if (CollectionUtils.isNotEmpty(ordOrders)) {
            byte[] ordOrdersByte = SerializeUtil.serialize(ordOrders);
            importMessageCache.setValue(key + "ordOrders",ordOrdersByte);
        }
        if (CollectionUtils.isNotEmpty(ordCustomers)) {
            byte[] ordCustomersByte = SerializeUtil.serialize(ordCustomers);
            importMessageCache.setValue(key + "ordCustomers",ordCustomersByte);
        }
        if (CollectionUtils.isNotEmpty(ordBeneficiaries)) {
            byte[] ordBeneficiariesByte = SerializeUtil.serialize(ordBeneficiaries);
            importMessageCache.setValue(key + "ordBeneficiaries",ordBeneficiariesByte);
        }
        if (CollectionUtils.isNotEmpty(ordTradeRoutes)) {
            byte[] ordTradeRoutesByte = SerializeUtil.serialize(ordTradeRoutes);
            importMessageCache.setValue(key + "ordTradeRoutes",ordTradeRoutesByte);
        }
        if (CollectionUtils.isNotEmpty(ordTradeRouteShows)) {
            byte[] ordTradeRouteShowsByte = SerializeUtil.serialize(ordTradeRouteShows);
            importMessageCache.setValue(key + "ordTradeRouteShows",ordTradeRouteShowsByte);
        }
        if (CollectionUtils.isNotEmpty(ordAdditions)) {
            byte[] ordAdditionsByte = SerializeUtil.serialize(ordAdditions);
            importMessageCache.setValue(key + "ordAdditions",ordAdditionsByte);
        }
        if (CollectionUtils.isNotEmpty(ordCommissions)) {
            byte[] ordCommissionsByte = SerializeUtil.serialize(ordCommissions);
            importMessageCache.setValue(key + "ordCommissions",ordCommissionsByte);
        }
    }
}
