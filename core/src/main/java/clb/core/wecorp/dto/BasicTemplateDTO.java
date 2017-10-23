package clb.core.wecorp.dto;

import java.io.Serializable;

/**
 * Created by zyc on 2017/8/3.
 */
public class BasicTemplateDTO implements Serializable {

    private String value;

    private String color;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
