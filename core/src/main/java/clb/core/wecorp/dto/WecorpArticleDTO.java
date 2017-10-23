package clb.core.wecorp.dto;



/**
 * Created by shanhd on 2016/10/21.
 */
public class WecorpArticleDTO {

    private WecorpArticle[] articles;
    private String flag;
    private String originGroupNumber;
    private boolean isGroup;

    public WecorpArticle[] getArticles() {
        return articles;
    }

    public void setArticles(WecorpArticle[] articles) {
        this.articles = articles;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getOriginGroupNumber() {
        return originGroupNumber;
    }

    public void setOriginGroupNumber(String originGroupNumber) {
        this.originGroupNumber = originGroupNumber;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }
}
