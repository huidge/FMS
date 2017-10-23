package clb.core.common.controllers;

import clb.core.attachment.controllers.FmsAttachmentController;
import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ICommonUploadService;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.attachment.exception.UniqueFileMutiException;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 谭志骞 on 2017-6-3.
 */

@Controller
public class WsDownFileController extends BaseController {


    @Autowired
    private FmsAttachmentController fmsAttachmentController;

    @Autowired
    private ICommonUploadService uploadService;
    @Autowired
    private ISysFileService fileService;

    /**
     * 校验文件是否存在
     * @param fileId:文件Id
     * @param token:文件token
     * @throws CommonException token值不正确
     */

    @Timed
    @HapInbound(apiName = "ClbWsCommonUpDown")
    @RequestMapping(value = "/api/commons/down", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public ResponseData vdFile(HttpServletRequest request, HttpServletResponse response, String fileId, String token)
            throws StoragePathNotExsitException, UniqueFileMutiException, JsonProcessingException, CommonException,FileReadIOException, TokenException {
        IRequest requestContext = createRequestContext(request);
        SysFile file = new SysFile();
        file.setFileId(Long.valueOf(fileId));
        file.set_token(token);
        try{
            TokenUtils.checkToken(request.getSession(false), file);
        }catch(TokenException e){
            throw new CommonException("COMMON","spe.tokenerror",null);
        }
        SysFile sysFile = fileService.selectByPrimaryKey(requestContext, Long.valueOf(fileId));
        if(sysFile == null){
            return new ResponseData(false);
        }else{
            fmsAttachmentController.detail(request,response,fileId,token);
            return new ResponseData(true);
        }
    }

}
