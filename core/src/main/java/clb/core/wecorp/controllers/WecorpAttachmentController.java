package clb.core.wecorp.controllers;

import clb.core.attachment.UpConstants;
import clb.core.common.utils.SpringConfigTool;
import clb.core.sys.controllers.ClbBaseController;
import clb.core.wecorp.dto.WecorpAttachment;
import clb.core.wecorp.service.IWecorpAccountMenuService;
import clb.core.wecorp.service.IWecorpAttachmentService;
import clb.core.wecorp.utils.Constant;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.attachment.exception.UniqueFileMutiException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.IProfileService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/7/10.
 */
@Controller
public class WecorpAttachmentController extends ClbBaseController {

    @Autowired
    private IWecorpAttachmentService wecorpAttachmentService;
    @Autowired
    private IProfileService profileService;

    @RequestMapping(value = "/api/public/common/woa/upload")
    public String uploadLogo(HttpServletRequest request,@RequestParam("file") MultipartFile[] files,
                             @RequestParam("groupId") String groupId) throws StoragePathNotExsitException, UniqueFileMutiException, IOException, FileUploadException {


        IRequest iRequest=createRequestContext(request);
        try {
            for (MultipartFile f : files) {
                SimpleDateFormat parser = new SimpleDateFormat("yyyyMMdd");
                String timeStamp = parser.format(new Date());
//                File f = new File(controller.getFileDir(request, fileName) + controller.newName(fileName));
//                item.write(f);

                // Add by Rex.Hua@21070516
                // Desc: 上传至阿里云OSS
                IRequest irequest = RequestHelper.newEmptyRequest();
                irequest.setUserId(-1L);
                irequest.setRoleId(-1L);
                profileService = (IProfileService) SpringConfigTool.getBean("profileServiceImpl");
                String endpoint = profileService.getProfileValue(irequest, "ENDPOINT");
                String accesskeyid = profileService.getProfileValue(irequest, "ACCESSKEYID");
                String accesskeysecret = profileService.getProfileValue(irequest, "ACCESSKEYSECRET");
                String bucketnamePic = profileService.getProfileValue(irequest, "BUCKETNAME_PIC");
                String bucketnameUnPic = profileService.getProfileValue(irequest, "BUCKETNAME_UNPIC");

                try {

                    ClientConfiguration config = new ClientConfiguration();
                    config.setCrcCheckEnabled(false);
                    OSSClient client = new OSSClient(endpoint, accesskeyid, accesskeysecret,config);

                    String fileName = f.getOriginalFilename();
                    if ("png".equals(fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase())) {
                        client.putObject(bucketnamePic, "image/" + timeStamp + "/" + fileName, f.getInputStream());
                    } else if ("jpg".equals(fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase())) {
                        client.putObject(bucketnamePic, "image/" + timeStamp + "/" + fileName, f.getInputStream());
                    } else if ("jpeg".equals(fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase())) {
                        client.putObject(bucketnamePic, "image/" + timeStamp + "/" + fileName, f.getInputStream());
                    }
                    client.shutdown();

                    WecorpAttachment attachment=new WecorpAttachment();
                    String id= UUID.randomUUID().toString();
                    attachment.setId(id);
                    attachment.setName(fileName);
                    attachment.setStorePath("http://" + bucketnamePic + "." + endpoint + "/image/" + timeStamp + "/" + fileName);
                    attachment.setType(Constant.ATTACHE_IMAGE);
                    if(!Constant.UN_GROUP_ID.equals(groupId)){
                        attachment.setGroupId(groupId);
                    }
                    wecorpAttachmentService.insertSelective(iRequest, attachment);

                } catch (Exception e) {

                    throw new RuntimeException(e);
                }
                // End@21070516
            }

            }catch(Exception e){
            return "error";

            }




        return "<script>window.parent.showUploadSucessLogo()</script>";
    }
}
