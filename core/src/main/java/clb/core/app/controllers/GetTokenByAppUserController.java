package clb.core.app.controllers;

import clb.core.app.dto.AppToken;
import clb.core.app.dto.AppUser;
import clb.core.app.service.IAppUserService;
import clb.core.sys.controllers.ClbBaseController;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 谭志骞 on 2017-10-16.
 */
@Controller
public class GetTokenByAppUserController extends ClbBaseController {

    @Autowired
    private IAppUserService iAppUserService;

    /****
     * app用户获取
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsAppUserGetTokenPublic")
    @RequestMapping(value = "/api/public/app/oauth/token", method = RequestMethod.POST, produces = { "application/json" })
    @ResponseBody
    public ResponseData getTokenByAppUser(@RequestBody AppUser dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        ResponseData rd = new ResponseData();
        HttpResponse<JsonNode> response = null;
        if(dto!=null){
            try {
                //获取项目跟路径
                String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
                AppUser appUser = iAppUserService.queryAppUser(requestContext,dto.getAppUserId(),dto.getAppUsername(),dto.getAppPassword());
                if(appUser!=null){
                    //http://localhost:8080/
                    response=Unirest
                            .post(basePath+"oauth/token?client_id=client2&client_secret=secret&username=appadmin&password=appadmin&grant_type=password")
                            .asJson();
                    List<AppToken> appToken =new ArrayList<>();
                    AppToken appToken1 = new AppToken();
                    appToken1.setAccess_token(response.getBody().getObject().get("access_token")+"");
                    appToken1.setExpires_in(response.getBody().getObject().get("expires_in")+"");
                    appToken1.setJti(response.getBody().getObject().get("jti")+"");
                    appToken1.setRefresh_token(response.getBody().getObject().get("refresh_token")+"");
                    appToken1.setScope(response.getBody().getObject().get("scope")+"");
                    appToken1.setToken_type(response.getBody().getObject().get("token_type")+"");
                    appToken.add(appToken1);
                    rd.setRows(appToken);
                    rd.setSuccess(true);
                }else{
                    rd.setSuccess(false);
                    rd.setMessage("请求Token失败，请检查用户是否注册");
                    //response =
                    //return  response;
                }
            } catch (Exception e) {
                rd.setMessage("请求Token失败，请检查用户是否注册");
                rd.setSuccess(false);
                e.printStackTrace();
            }
        }
        return  rd;
    }

}
