package clb.core.course.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.service.ISequenceService;
import clb.core.course.dto.TrnCourseFile;
import clb.core.course.mapper.TrnCourseFileMapper;
import clb.core.course.service.ITrnCourseFileService;

@Service
@Transactional
public class TrnCourseFileServiceImpl extends BaseServiceImpl<TrnCourseFile> implements ITrnCourseFileService{
	
	@Autowired
	private TrnCourseFileMapper trnCourseFileMapper;
	@Autowired
    private ISysFileService fileService;
	@Autowired
    private ISequenceService sequenceService;
	
	@Override
	public List<TrnCourseFile> selectBycourseId(IRequest requestContext,Long courseId, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		List<TrnCourseFile> list = trnCourseFileMapper.selectBycourseId(courseId);
		for (TrnCourseFile trnCourseFile : list) {
			if (trnCourseFile.getFileId() != null) {
                SysFile sysFile = fileService.selectByPrimaryKey(requestContext, trnCourseFile.getFileId());
                if (sysFile != null) {
                	trnCourseFile.set_token(sysFile.get_token());
                }else {
                	trnCourseFile.set_token(null);
                }

            }
		}
		return list;
		//return trnCourseFileMapper.selectBycourseId(courseId);
	}
	@Override
	public List<TrnCourseFile> selectByParams(IRequest requestContext, TrnCourseFile dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return trnCourseFileMapper.selectByParams(dto);
	}

	@Override
	public void updateDowloadTimes(IRequest requestContext, TrnCourseFile dto) {
		dto.setDownloadTimes((dto.getDownloadTimes()==null?0L:dto.getDownloadTimes())+1);
		updateByPrimaryKeySelective(requestContext, dto);
	}
	@Override
	public List<TrnCourseFile> selectAllField(IRequest requestContext, TrnCourseFile trnCourseFile, int page,
			int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<TrnCourseFile> list = trnCourseFileMapper.selectAllField(trnCourseFile);
		for (TrnCourseFile trnCourseFile1 : list) {
			if (trnCourseFile1.getFileId() != null) {
                SysFile sysFile = fileService.selectByPrimaryKey(requestContext, trnCourseFile1.getFileId());
                if (sysFile != null) {
                	trnCourseFile1.set_token(sysFile.get_token());
                }else {
                	trnCourseFile1.set_token(null);
                }
            }
		}
		return list;
		//return null;
	}
	
	/*@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public TrnCourseFile createCourseFile(IRequest requestContext, TrnCourseFile obj) {
		if (obj == null) {
            return null;
        }
		if (obj.getFileNum() == null || obj.getFileNum() == "") {
			obj.setFileNum(sequenceService.getSequence("FILENUM"));
        }
		trnCourseFileMapper.insertSelective(obj);
		
	        return obj;
		//return null;
	}*/
}