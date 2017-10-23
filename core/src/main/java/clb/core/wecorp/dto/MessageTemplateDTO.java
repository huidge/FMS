package clb.core.wecorp.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by zyc on 2017/8/3.
 */
public class MessageTemplateDTO implements Serializable {

    private String touser;

    private String template_id;

    private String url;

    private String topcolor;

    private Map<String,BasicTemplateDTO> data;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTopcolor() {
        return topcolor;
    }

    public void setTopcolor(String topcolor) {
        this.topcolor = topcolor;
    }

    public Map<String, BasicTemplateDTO> getData() {
        return data;
    }

    public void setData(Map<String, BasicTemplateDTO> data) {
        this.data = data;
    }
}
