package clb.core.commission.service.impl;
/**
 * Created by wanjun.feng on 2017-09-11.
 */

import clb.core.commission.dto.CmnChannelRatioDetail;
import clb.core.commission.mapper.CmnChannelRatioDetailMapper;
import clb.core.commission.service.ICmnChannelRatioDetailService;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CmnChannelRatioDetailServiceImpl extends BaseServiceImpl<CmnChannelRatioDetail> implements ICmnChannelRatioDetailService {

    @Autowired
    private CmnChannelRatioDetailMapper cmnChannelRatioDetailMapper;

    @Override
    public List<CmnChannelRatioDetail> selectChannelRatioDetails(IRequest requestContext, CmnChannelRatioDetail cmnChannelRatioDetail, int page,
            int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<CmnChannelRatioDetail> list = new ArrayList<CmnChannelRatioDetail>();
        list = cmnChannelRatioDetailMapper.selectChannelRatioDetails(cmnChannelRatioDetail);
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseData batchSubmit(IRequest requestContext,
            List<CmnChannelRatioDetail> cmnChannelRatioDetailList) {
        try {
            for (CmnChannelRatioDetail cmnChannelRatioDetail : cmnChannelRatioDetailList) {
                CmnChannelRatioDetail _cmnChannelRatioDetail = new CmnChannelRatioDetail();
                _cmnChannelRatioDetail.setRatioId(cmnChannelRatioDetail.getRatioId());
                _cmnChannelRatioDetail.setBigClass(cmnChannelRatioDetail.getBigClass());
                _cmnChannelRatioDetail.setMidClass(cmnChannelRatioDetail.getMidClass());
                _cmnChannelRatioDetail.setMinClass(cmnChannelRatioDetail.getMinClass());
                _cmnChannelRatioDetail.setItemId(cmnChannelRatioDetail.getItemId());
                _cmnChannelRatioDetail.setSublineItemId(cmnChannelRatioDetail.getSublineItemId());
                List<CmnChannelRatioDetail> _cmnChannelRatioDetailList = cmnChannelRatioDetailMapper.selectChannelRatioDetailsByNull(_cmnChannelRatioDetail);
                if (cmnChannelRatioDetail.get__status().equals("update")) {
                    if (_cmnChannelRatioDetailList != null) {
                        if (_cmnChannelRatioDetailList.size() == 1) {
                            if (!_cmnChannelRatioDetailList.get(0).getRatioLineId().equals(cmnChannelRatioDetail.getRatioLineId())) {
                                ResponseData response = new ResponseData(false);
                                response.setMessage("不能设置相同的渠道分成！");
                                return response;
                            }
                        } else if (_cmnChannelRatioDetailList.size() > 1) {
                            ResponseData response = new ResponseData(false);
                            response.setMessage("不能设置相同的渠道分成！");
                            return response;
                        }
                    }
                    cmnChannelRatioDetail = self().updateByPrimaryKey(requestContext, cmnChannelRatioDetail);
                } else if (cmnChannelRatioDetail.get__status().equals("add")) {
                    if (_cmnChannelRatioDetailList != null && _cmnChannelRatioDetailList.size() > 0) {
                        ResponseData response = new ResponseData(false);
                        response.setMessage("不能设置相同的渠道分成！");
                        return response;
                    }
                    cmnChannelRatioDetail = self().insertSelective(requestContext, cmnChannelRatioDetail);
                }
            }
        } catch(Exception e) {
            ResponseData response = new ResponseData(false);
            response.setMessage(e.getMessage());
            return response;
        }
        return new ResponseData(cmnChannelRatioDetailList);
    }
}