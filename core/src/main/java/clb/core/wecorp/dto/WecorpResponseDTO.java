package clb.core.wecorp.dto;

/**
 * Created by zyc on 2017/7/20.
 */
public class WecorpResponseDTO {
    private String type;
    private String content;
    private String mediaId;
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    @Override
    public String toString() {
        return "WecorpResponseDTO{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", mediaId='" + mediaId + '\'' +
                '}';
    }
}
