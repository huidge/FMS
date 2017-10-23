package clb.core.course.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.common.service.ISequenceService;
import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnCourseEvaluate;
import clb.core.course.dto.TrnCourseFile;
import clb.core.course.dto.TrnCourseStudent;
import clb.core.course.mapper.TrnCourseEvaluateMapper;
import clb.core.course.mapper.TrnCourseMapper;
import clb.core.course.service.ITrnCourseFileService;
import clb.core.course.service.ITrnCourseService;
import clb.core.course.service.ITrnCourseStudentService;
import clb.core.notification.mapper.NtnNotificationTemplateMapper;
import clb.core.sys.service.ISysSendSMSService;
import clb.core.system.service.IClbCodeService;
import clb.core.system.service.IClbUserService;

@Service
@Transactional
public class TrnCourseServiceImpl extends BaseServiceImpl<TrnCourse> implements ITrnCourseService{
	@Autowired
    private TrnCourseMapper trnCourseMapper;
	@Autowired
    private ISequenceService sequenceService;
	@Autowired
    private ITrnCourseFileService trnCourseFileService;
	@Autowired
    private ISysFileService fileService;
	@Autowired
	private IClbCodeService clbCodeService;
	@Autowired
    private IClbUserService clbUserService;
	@Autowired
	private ITrnCourseStudentService trnCourseStudentService;
	@Autowired
	private TrnCourseEvaluateMapper trnCourseEvaluateMapper;
	@Autowired
	private CnlChannelContractMapper cnlChannelContractMapper;
	@Autowired
    private ISysSendSMSService sysSendSMSService;
	@Autowired
    private NtnNotificationTemplateMapper ntnNotificationTemplateMapper;
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public TrnCourse createCourse(IRequest request, TrnCourse obj) {
		if (obj == null) {
            return null;
        }
		if (obj.getCourseNum() == null || obj.getCourseNum() == "") {
			obj.setCourseNum(sequenceService.getSequence("COURSE"));
        }
		trnCourseMapper.insertSelective(obj);
		
	        return obj;
	}

	@Override
	public List<TrnCourse> selectAllField(IRequest requestContext, TrnCourse trnCourse, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<TrnCourse> list = trnCourseMapper.selectAllField(trnCourse);
		if(trnCourse.getCourseId()!=null){
			for(TrnCourse course:list){
				/*if(course.getTopic().equals("dfgdf")){
					System.out.println(course);
				}*/
				List<TrnCourseFile> filelist= trnCourseFileService.selectBycourseId(requestContext, trnCourse.getCourseId(), page, 10000);
				List<SysFile> sysFiles = new ArrayList<>();
				for (TrnCourseFile courseFile : filelist) {
					Long fileId = courseFile.getFileId();
					SysFile sysFile = fileService.selectByPrimaryKey(requestContext, fileId);
					sysFiles.add(sysFile);
				}
				course.setSysFiles(sysFiles);
			}
		}
		return list;
		//return null;
	}

	@Override
	public TrnCourse selectOne(IRequest requestContext, TrnCourse trnCourse, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		TrnCourse TrnCourse = trnCourseMapper.selectOne(trnCourse);
		return TrnCourse;
	}

	@Override
	public List<TrnCourse> selectId(IRequest requestContext, TrnCourse trnCourse) {
		List<TrnCourse> list = trnCourseMapper.selectAllField(trnCourse);
		/*if(trnCourse.getCourseId()!=null){
			for(TrnCourse course:list){
				List<TrnCourseFile> filelist= trnCourseFileService.selectBycourseId(requestContext, trnCourse.getCourseId(), 1, 100);
				List<SysFile> sysFiles = new ArrayList<>();
				for (TrnCourseFile courseFile : filelist) {
					Long fileId = courseFile.getFileId();
					SysFile sysFile = fileService.selectByPrimaryKey(requestContext, fileId);
					sysFiles.add(sysFile);
				}
				course.setSysFiles(sysFiles);
			}
		}*/
		return list;
		//return null;
	}

	@Override
	public List<TrnCourse> wsSelectAllField(IRequest requestContext, TrnCourse trnCourse, int page, int pagesize) {
		List<TrnCourse> list=new ArrayList<TrnCourse>();
		//判断是否签约渠道，如果不是 ，则不显示直播密码 ，不允许课程资料下载、培训资料 下载 
		//-------渠道信息是否存在合约信息
		boolean isSignChannel=true;
		CnlChannelContract cnlChannelContract=new CnlChannelContract();
		if(requestContext.getAttribute("channelId")!=null){
			cnlChannelContract.setChannelId(requestContext.getAttribute("channelId"));
		}
		if(CollectionUtils.isEmpty(cnlChannelContractMapper.select(cnlChannelContract))){
			isSignChannel=false;
		}
		
		PageHelper.startPage(page, pagesize);
		if(trnCourse.getCreatedBy()==null){
			//查询全部
			trnCourse.setCreatedBy(requestContext.getUserId());
			list = trnCourseMapper.wsSelectAllField(trnCourse);
		}else{
			//查询报名
			list = trnCourseMapper.wsSelectEnrollCourse(trnCourse);
		}
		for(TrnCourse course:list){
			//只有签约的能跳转到精品视频的链接
			if(!isSignChannel){
				course.setBoutiqueUrl(null);
			}
			
			//
			//是否已经报名
			//根据用户查渠道
	    	TrnCourseStudent cs=new TrnCourseStudent();
			cs.setCourseId(course.getCourseId());
			cs.setCreatedBy(requestContext.getUserId());
			List<TrnCourseStudent>studentList=trnCourseStudentService.selectEnrollByParams(requestContext,cs);
			course.setStudents(studentList);
			
	    	if(CollectionUtils.isEmpty(studentList)){
	    		course.setEnrollFlag("N");
	    		course.setPassword(null);
	    	}else{
	    		//判断是否已经付款
	    		String enRollFlag="Y";
	    		for(TrnCourseStudent stu:studentList){
	    			if(!stu.getPayStatus().equals("PAID")){
	    				enRollFlag="P";
	    			}
	    		}
	    		if(enRollFlag.equals("P")){
	    			course.setPassword(null);
	    		}
	    		course.setEnrollFlag(enRollFlag);
	    		
	    		//是否已经评价
	    		TrnCourseEvaluate trnCourseEvaluate=new TrnCourseEvaluate();
	    		trnCourseEvaluate.setCourseId(course.getCourseId());
	    		trnCourseEvaluate.setCreatedBy(requestContext.getUserId());
	    		if(CollectionUtils.isEmpty(trnCourseEvaluateMapper.selectEvaluateByParams(trnCourseEvaluate))){
	    			course.setEvaluateFlag("N");
	    		}else{
	    			course.setEvaluateFlag("Y");
	    		}
	    	}
	    	
		}
		if(trnCourse.getCourseId()!=null){
			for(TrnCourse course:list){
				String trainingMethodName= clbCodeService.getCodeMeaningByValue(requestContext, "COURSE.TRAINING_METHOD",course.getTrainingMethod() );
				course.setTrainingMethodName(trainingMethodName);
				List<TrnCourseFile> filelist= trnCourseFileService.selectBycourseId(requestContext, trnCourse.getCourseId(), page, 100);
				List<SysFile> sysFiles = new ArrayList<>();
				for (TrnCourseFile courseFile : filelist) {
					Long fileId = courseFile.getFileId();
					SysFile sysFile = fileService.selectByPrimaryKey(requestContext, fileId);
					if(!isSignChannel){
						sysFile.setFilePath(null);
						sysFile.setFileId(null);
	    			}
					sysFiles.add(sysFile);
				}
				course.setSysFiles(sysFiles);
			}
		}
		return list;
	}


	@Override
	public List<TrnCourse> updateStatus(IRequest requestContext, TrnCourse trnCourse) {
		List<TrnCourse> list = trnCourseMapper.selectAllField(trnCourse);
		/*if(trnCourse.getCourseId()!=null){
			for(TrnCourse course:list){
				if(course.getTopic().equals("dfgdf")){
					System.out.println(course);
				}
				List<TrnCourseFile> filelist= trnCourseFileService.selectBycourseId(requestContext, trnCourse.getCourseId(), page, 10000);
				List<SysFile> sysFiles = new ArrayList<>();
				for (TrnCourseFile courseFile : filelist) {
					Long fileId = courseFile.getFileId();
					SysFile sysFile = fileService.selectByPrimaryKey(requestContext, fileId);
					sysFiles.add(sysFile);
				}
				course.setSysFiles(sysFiles);
			}
		}*/
		return list;
		//return null;
		//return null;
	}

	/**
	 * APP近期课程、课程列表接口
	 * @param requestContext
	 * @param trnCourse
	 * @param page
	 * @param pagesize
	 * @return
	 */
	@Override
	public List<TrnCourse> queryAppCourseList(IRequest requestContext, TrnCourse trnCourse ,int page, int pagesize){
		List<TrnCourse> list = new ArrayList<TrnCourse>();

		if ("recent".equals(trnCourse.getAppShowType())) {
			list = trnCourseMapper.wsSelectRecentCourse(trnCourse);
		} else {
			// 返回列表
			list = trnCourseMapper.selectAllField(trnCourse);
		}

		return list;
	}

	/**
	 * APP近期课程、课程列表接口(数据整理后）
	 * @param requestContext
	 * @param trnCourse
	 * @param page
	 * @param pagesize
	 * @return
	 */
	@Override
	public List<TrnCourse> queryAppCL(IRequest requestContext, TrnCourse trnCourse ,int page, int pagesize){
		PageHelper.startPage(page, pagesize);
		List<TrnCourse> list = new ArrayList<TrnCourse>();

		if ("recent".equals(trnCourse.getAppShowType())) {
			list = trnCourseMapper.wsSelectRecentCourseByApp(trnCourse);
		} else {
			// 返回列表
			list = trnCourseMapper.selectAllFieldByApp(trnCourse);
		}

		return list;
	}

}