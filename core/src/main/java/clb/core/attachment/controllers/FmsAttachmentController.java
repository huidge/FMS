/*
 * #{copyright}#
 */
package clb.core.attachment.controllers;

import clb.core.common.utils.SpringConfigTool;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.service.IProfileService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * 附件管理器.
 *
 * @author Rex.Hua
 */
@Controller
public class FmsAttachmentController extends BaseController {

    /**
     * 提示信息名.
     */
    private static final String MESSAGE_NAME = "message";

    /**
     * 提示只能上传一个文件
     */
    private static final String MESG_UNIQUE = "Unique";
    /**
     * 提示成功.
     */
    private static final String MESG_SUCCESS = "success";
    /**
     * 文件不存在提示.
     */
    private static final String FILE_NOT_EXSIT = "file_not_exsit";
    /**
     * 提示信息 name.
     **/
    private static final String INFO_NAME = "info";
    /**
     * 附件上传存储目录未分配.
     **/
    private static final String TYPEORKEY_EMPTY = "TYPEORKEY_EMPTY";
    /**
     * sourceType 错误.
     */
    private static final String TYPE_ERROR = "SOURCETYPE_ERROR";
    /**
     * 数据库 错误.
     */
    private static final String DATABASE_ERROR = "DATABASE_ERROR";
    /**
     * 图片mime前缀.
     */
    private static final String IMAGE_MIME_PREFIX = "image";
    /**
     * file对象名.
     */
    private static final String FILE_NAME = "file";
    /**
     * buffer 大小.
     */
    private static final Integer BUFFER_SIZE = 1024;

    /**
     * 图片压缩大小.
     */
    private static final Float COMPRESS_SIZE = 40f;

    /**
     * 进制单位.
     */
    private static final Float BYTE_TO_KB = 1024f;
    /**
     * 文件下载默认编码.
     */
    private static final String ENC = "UTF-8";

    /**
     * 日志记录.
     **/
    private static Logger logger = LoggerFactory.getLogger(com.hand.hap.attachment.controllers.AttachmentController.class);

    @Autowired
    private IAttachCategoryService categoryService;
    @Autowired
    private ISysFileService fileService;
    @Autowired
    private IProfileService profileService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;
    /**
     * 具体查看某个附件.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param fileId   文件id
     * @throws FileReadIOException 文件读取IO异常
     */
    @RequestMapping(value = "fms/sys/attach/file/detail")
    public void detail(HttpServletRequest request, HttpServletResponse response, String fileId, String token)
            throws FileReadIOException, TokenException {
        IRequest requestContext = createRequestContext(request);
        SysFile sysFile = fileService.selectByPrimaryKey(requestContext, Long.valueOf(fileId));
        sysFile.set_token(token);
        TokenUtils.checkToken(request.getSession(false), sysFile);
        try {
            if (sysFile != null && StringUtils.isNotBlank(sysFile.getFilePath())) {
                // Modified by Rex.Hua@20170516
                IRequest irequest = RequestHelper.newEmptyRequest();
                profileService = (IProfileService) SpringConfigTool.getBean("profileServiceImpl");
                String endpoint=profileService.getProfileValue(irequest, "ENDPOINT");
                String accesskeyid=profileService.getProfileValue(irequest, "ACCESSKEYID");
                String accesskeysecret=profileService.getProfileValue(irequest, "ACCESSKEYSECRET");
                String bucketnamePic=profileService.getProfileValue(irequest, "BUCKETNAME_PIC");
                String bucketnameUnPic=profileService.getProfileValue(irequest, "BUCKETNAME_UNPIC");

                ClientConfiguration config = new ClientConfiguration();
                config.setCrcCheckEnabled(false);
                OSSClient client = new OSSClient(endpoint, accesskeyid, accesskeysecret,config);

                File file = new File(sysFile.getFilePath());
                if("image/jpeg".equals(sysFile.getFileType())){
                    client.getObject(new GetObjectRequest(bucketnamePic, sysFile.getFilePath()), file);
                }else if("image/jpg".equals(sysFile.getFileType())){
                    client.getObject(new GetObjectRequest(bucketnamePic, sysFile.getFilePath()), file);
                }else if("image/png".equals(sysFile.getFileType())){
                    client.getObject(new GetObjectRequest(bucketnamePic, sysFile.getFilePath()), file);
                }else{
                    client.getObject(new GetObjectRequest(bucketnameUnPic, sysFile.getFilePath()), file);
                }
                // End@20170516
                if (file.exists()) {
                    // 下载
//                    if (StringUtils.isNotBlank(sysFile.getFileType())
//                            && sysFile.getFileType().startsWith(IMAGE_MIME_PREFIX)) {
                	
                	
                    response.addHeader("Content-Disposition",
                            "attachment;filename=\"" + URLEncoder.encode(sysFile.getFileName(), ENC) + "\"");
//                    }
                    response.setContentType(sysFile.getFileType() + ";charset=" + ENC);
                    response.setHeader("Accept-Ranges", "bytes");

                    int fileLength = (int) file.length();
                    response.setContentLength(fileLength);
                    if (fileLength > 0) {
                        writeFileToResp(response, file);
                    }
                } else {
                    response.getWriter().write(FILE_NOT_EXSIT);
                }
            } else {
                response.getWriter().write(FILE_NOT_EXSIT);
            }
        } catch (IOException e) {
            throw new FileReadIOException();
        }

    }

    /**
     * 将文件对象的流写入Responsne对象.
     *
     * @param response HttpServletResponse
     * @param file     File
     * @throws FileNotFoundException 找不到文件异常
     * @throws IOException           IO异常
     */
    private void writeFileToResp(HttpServletResponse response, File file) throws FileNotFoundException, IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        try (InputStream inStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {
            int readLength;
            while (((readLength = inStream.read(buf)) != -1)) {
                outputStream.write(buf, 0, readLength);
            }
            outputStream.flush();

        }
    }
}
