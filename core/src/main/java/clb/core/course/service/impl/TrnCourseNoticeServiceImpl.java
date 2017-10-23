package clb.core.course.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.course.dto.TrnCourseNotice;
import clb.core.course.mapper.TrnCourseNoticeMapper;
import clb.core.course.service.ITrnCourseNoticeService;

@Service
@Transactional
public class TrnCourseNoticeServiceImpl extends BaseServiceImpl<TrnCourseNotice> implements ITrnCourseNoticeService{
	@Autowired
    private TrnCourseNoticeMapper TrnCourseNoticeMapper;
	
	@Override
	public List<TrnCourseNotice> selectAllField(IRequest requestContext, TrnCourseNotice TrnCourseNotice, int page,
			int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<TrnCourseNotice> list = TrnCourseNoticeMapper.selectAllField(TrnCourseNotice);
		return list;
		//return null;
	}

	@Override
	public List<TrnCourseNotice> selectAll(IRequest requestContext, TrnCourseNotice TrnCourseNotice) {
		List<TrnCourseNotice> list = TrnCourseNoticeMapper.selectAllField(TrnCourseNotice);
		return list;
		//return null;
	}

}