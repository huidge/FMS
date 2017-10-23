package clb.core.forecast.service.impl;

import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import clb.core.forecast.dto.FetQuestion;
import clb.core.forecast.dto.FetQuestionLine;
import clb.core.forecast.mapper.FetQuestionLineMapper;
import clb.core.forecast.mapper.FetQuestionMapper;
import clb.core.forecast.service.IFetQuestionLineService;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FetQuestionLineServiceImpl extends BaseServiceImpl<FetQuestionLine> implements IFetQuestionLineService{

	@Autowired
	private FetQuestionMapper questionMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private FetQuestionLineMapper questionLineMapper;
	
	
	@Override
	public List<FetQuestion> queryByCheckPeriod(IRequest iRequest,FetQuestion dto,HttpServletRequest request) {
		String basePath = request.getContextPath();
		//根据对账期间，渠道Id和版本查询数据
		List<FetQuestion> resData = new ArrayList<>();
		FetQuestion question = questionMapper.selectOne(dto);
		//没查到，直接返回
		if(question == null)return resData;
		//有头数据一定有行数据，不需要校验
		FetQuestionLine line = new FetQuestionLine();
		line.setQuestionId(question.getQuestionId());
		List<FetQuestionLine> data = questionLineMapper.selectData(line);
		//设置左右显示
		for(FetQuestionLine d:data){
			//当前用户，显示在右侧
			if(iRequest.getUserId().equals(d.getCreatedBy())){
				d.setIsRight(true);
			}else{
				d.setIsRight(false);
			}
			String content = StringUtils.replace(d.getContent(),"{root}",basePath);
			d.setContent(content);
		}
		question.setLines(data);
		resData.add(question);
		return resData;
	}

	@Override
	public List<FetQuestion> getData(IRequest iRequest,FetQuestionLine dto,HttpServletRequest request) {
		String basePath = request.getContextPath();
		List<FetQuestion> resData = new ArrayList<>();
		//根据问题Id查询数据
		List<FetQuestionLine> data = questionLineMapper.selectData(dto);
		FetQuestion question = new FetQuestion();
		question.setQuestionId(dto.getQuestionId());
		//有行数据一定有头数据，不需要校验
		question = questionMapper.selectByPrimaryKey(question);
		//设置左右显示
		for(FetQuestionLine d:data){
			//当前用户，显示在右侧
			if(iRequest.getUserId().equals(d.getCreatedBy())){
				d.setIsRight(true);
			}else{
				d.setIsRight(false);
			}
			String content = StringUtils.replace(d.getContent(),"{root}",basePath);
			d.setContent(content);
		}
		question.setLines(data);
		resData.add(question);
		return resData;
	}

	@Override
	public List<FetQuestion> queryByCheckPeriodWs(IRequest iRequest, FetQuestion dto, HttpServletRequest request) {
		String basePath = request.getContextPath();
		//根据对账期间，渠道Id和版本查询数据
		List<FetQuestion> resData = new ArrayList<>();
		FetQuestion question = questionMapper.selectOne(dto);
		//没查到，直接返回
		if(question == null)return resData;
		//有头数据一定有行数据，不需要校验
		FetQuestionLine line = new FetQuestionLine();
		line.setQuestionId(question.getQuestionId());
		List<FetQuestionLine> data = questionLineMapper.selectData(line);
		//设置左右显示
		for(FetQuestionLine d:data){
			//当前用户，显示在右侧
			if(iRequest.getUserId().equals(d.getCreatedBy())){
				d.setIsRight(true);
			}else{
				d.setIsRight(false);
			}
			String content = d.getContent();
			d.setAttaches(getFiles(d.getContent()));
			if(StringUtils.indexOf(content,"<br>") != -1){
				content = StringUtils.substring(content,0,StringUtils.indexOf(content,"<br>"));
			}
			d.setContent(content);
		}
		question.setUserId(iRequest.getUserId());
		question.setLines(data);
		resData.add(question);
		return resData;
	}
	
	/**
	 * 获取文件id和路径 
	 */
	public static List<SysFile> getFiles(String content){
		//获取函数正则
		String functionRex = "downloadFile\\(([0-9]+,)([A-Za-z0-9/'-]+)\\)";
    	//获取函数参数正则
		String paramRex = "\\(.*\\)";
		//获取左右括号正则
    	String bracketsRex = "(\\(|\\))";
    	Pattern function = Pattern.compile(functionRex);
    	Pattern param = Pattern.compile(paramRex);
    	Matcher func = function.matcher(content);
    	List<SysFile> files = new ArrayList<>();
    	while (func.find()) { 
    		String fun = func.group().replaceAll("'","");  
        	Matcher par = param.matcher(fun);    
    		while (par.find()) { 
    			String fileData = par.group().replaceAll(bracketsRex,"");  
    			Long fileId = Long.parseLong(fileData.split(",")[0]);
    			String filePath = fileData.split(",")[1];
    			SysFile file = new SysFile();
    			file.setFileId(fileId);
    			file.setFilePath(filePath);
    			files.add(file);
        	} 
    	}
    	//获取文件名位置正则
		String fileNamePositionRex = "><br>.*?<\\/div>";
    	//获取函数参数正则
		String fileNameRex = "(><br>|<\\/div>)";
		
		Pattern fileNamePositionPattern = Pattern.compile(fileNamePositionRex);
		Matcher fileNamePositionMatcher = fileNamePositionPattern.matcher(content);
		int i = 0;
    	while (fileNamePositionMatcher.find()) { 
    		String fileName = fileNamePositionMatcher.group().replaceAll(fileNameRex,""); 
    		files.get(i).setFileName(fileName);
    		i = i+1;
    	}
		return files; 
	}

}