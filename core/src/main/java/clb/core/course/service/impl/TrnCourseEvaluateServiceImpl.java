package clb.core.course.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.course.dto.TrnCourseEvaluate;
import clb.core.course.mapper.TrnCourseEvaluateMapper;
import clb.core.course.service.ITrnCourseEvaluateService;

@Service
@Transactional
public class TrnCourseEvaluateServiceImpl extends BaseServiceImpl<TrnCourseEvaluate> implements ITrnCourseEvaluateService{
	@Autowired
    private TrnCourseEvaluateMapper trnCourseEvaluateMapper;

	@Override
	public List<TrnCourseEvaluate> selectAllField(IRequest requestContext, TrnCourseEvaluate trnCourseEvaluate,
			int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<TrnCourseEvaluate> selectAllField = trnCourseEvaluateMapper.selectAllField(trnCourseEvaluate);
		List<TrnCourseEvaluate> list = new ArrayList<>();
		/*if((trnCourseEvaluate.getContentFlag()).equals("true")){
			for (TrnCourseEvaluate trnCourseEvaluate1 : selectAllField) {
				if(trnCourseEvaluate1.getEvaluateContent() !=null && !"".equals(trnCourseEvaluate1.getEvaluateContent())){
					list.add(trnCourseEvaluate1);
				}
			}
		}*/
		return selectAllField;
	}
	

}