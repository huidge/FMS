package clb.core.system.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import com.hand.hap.core.ILanguageProvider;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.Language;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.dto.SysPreferences;
import com.hand.hap.system.service.ISysPreferencesService;

import clb.core.core.ClbBaseConstants;
/**
 * 系统首选项Controller.
 * @author wanjun.feng
 */
@Controller
@RequestMapping("/clb")
public class ClbSysPreferencesController extends BaseController {

    @Autowired
    private ISysPreferencesService sysPreferencesService;
    
    @Autowired
    private ILanguageProvider languageProvider;
    
    /**
     * 系统首选项界面
     * 
     * @param request HttpRequest
     * @return ResponseData 返回保存首选项集合，保存错误返回false
     * 
     */
    @RequestMapping(value = "/sys/um/sys_preferences.html")
    @ResponseBody
    public ModelAndView sysPreferences(final HttpServletRequest request) {
        ModelAndView mv = new ModelAndView(getViewPath() + "/sys/um/sys_preferences");
        List<Language> languages = languageProvider.getSupportedLanguages();
        mv.addObject("languages", languages);
        return mv;
    }

    /**
     * 系统首选项保存
     * 
     * @param request
     *            统一上下文
     * @param sysPreferences
     *            系统首选项信息集合
     * @return ResponseData 返回保存首选项集合，保存错误返回false
     * 
     */
    @RequestMapping(value = "/sys/preferences/savePreferences")
    @ResponseBody
    public ResponseData savePreferences(final HttpServletRequest request, final HttpServletResponse response, @RequestBody List<SysPreferences> sysPreferences, BindingResult result) {
        IRequest requestContext = createRequestContext(request);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        } else {
            for(SysPreferences preference: sysPreferences) {
                preference.setUserId(requestContext.getUserId());
                if(ClbBaseConstants.PREFERENCE_LOCALE.equalsIgnoreCase(preference.getPreferences())){
                    LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
                    if(localeResolver != null){
                        localeResolver.setLocale(request, response, StringUtils.parseLocaleString(preference.getPreferencesValue()));
                    }
                } else if(ClbBaseConstants.PREFERENCE_THEME.equalsIgnoreCase(preference.getPreferences())){
                    ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(request);
                    if (themeResolver != null) {
                        themeResolver.setThemeName(request, response, preference.getPreferencesValue());
                    }
                } else if(ClbBaseConstants.PREFERENCE_LOCALE2.equalsIgnoreCase(preference.getPreferences())){
                    WebUtils.setSessionAttribute(request, "locale2", preference.getPreferencesValue());
                }
            }
            List<SysPreferences> lists = sysPreferencesService.saveSysPreferences(createRequestContext(request),sysPreferences);
            return new ResponseData(lists);
        }
    }

    /**
     * 查询当前用户首选项集合
     * 
     * @param request
     * @param sysPreferences
     *            根据SysPreferences.accountId;SysPreferences.preferencesLevel查询条件
     *            查询当前首选项
     * @return responseData 响应数据
     */
    @RequestMapping(value = "/sys/preferences/queryPreferences")
    @ResponseBody
    public ResponseData queryPreferences(final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        SysPreferences preference = new SysPreferences();
        preference.setUserId(requestContext.getUserId());
        List<SysPreferences> lists = sysPreferencesService.querySysPreferences(requestContext, preference);
        return new ResponseData(lists);
    }
}