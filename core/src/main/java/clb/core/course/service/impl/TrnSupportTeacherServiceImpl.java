package clb.core.course.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.course.dto.TrnSupportTeacher;
import clb.core.course.mapper.TrnSupportTeacherMapper;
import clb.core.course.service.ITrnSupportTeacherService;

@Service
@Transactional
public class TrnSupportTeacherServiceImpl extends BaseServiceImpl<TrnSupportTeacher> implements ITrnSupportTeacherService{
	@Autowired
    private TrnSupportTeacherMapper trnSupportTeacherMapper;
	
	@Override
	public List<TrnSupportTeacher> selectAllField(IRequest requestContext, TrnSupportTeacher trnSupportTeacher,
			int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<TrnSupportTeacher> list = trnSupportTeacherMapper.selectAllField(trnSupportTeacher);
		return list;
		//return null;
	}
	
	@Override
	public List<TrnSupportTeacher> selectLecturer(IRequest requestContext, TrnSupportTeacher trnSupportTeacher,
			int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<TrnSupportTeacher> list = trnSupportTeacherMapper.selectLecturer(trnSupportTeacher);
		return list;
		//return null;
	}

}