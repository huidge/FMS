package clb.core.course.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 谈晟 cheng.tan@hand-china.com
 * @description
 * @time 2017/10/16
 */
@Table(name = "fms_trn_course")
public class AppTrnCourse extends BaseDTO {

    @NotEmpty
    private String topic;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date courseDate;

    private String address;

    @Transient
    private String filePath;

    @Transient
    private String studentNum;

    private String courseIntroduction;

    @NotEmpty
    private String lecturer;

    private String lecturerIntroduction;

    private String url;

    private String password;

    @NotEmpty
    private String trainingMethod;

    private BigDecimal coursePrice;

    private BigDecimal appPrice;

    private String boutiqueVideo;

    private String boutiqueUrl;

    public AppTrnCourse(String topic, Date courseDate, String address, String filePath, String studentNum, String courseIntroduction, String lecturer, String lecturerIntroduction, String url, String password, String trainingMethod, BigDecimal coursePrice, BigDecimal appPrice, String boutiqueVideo, String boutiqueUrl) {
        this.topic = topic;
        this.courseDate = courseDate;
        this.address = address;
        this.filePath = filePath;
        this.studentNum = studentNum;
        this.courseIntroduction = courseIntroduction;
        this.lecturer = lecturer;
        this.lecturerIntroduction = lecturerIntroduction;
        this.url = url;
        this.password = password;
        this.trainingMethod = trainingMethod;
        this.coursePrice = coursePrice;
        this.appPrice = appPrice;
        this.boutiqueVideo = boutiqueVideo;
        this.boutiqueUrl = boutiqueUrl;
    }

    public AppTrnCourse() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Date getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(Date courseDate) {
        this.courseDate = courseDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getCourseIntroduction() {
        return courseIntroduction;
    }

    public void setCourseIntroduction(String courseIntroduction) {
        this.courseIntroduction = courseIntroduction;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getLecturerIntroduction() {
        return lecturerIntroduction;
    }

    public void setLecturerIntroduction(String lecturerIntroduction) {
        this.lecturerIntroduction = lecturerIntroduction;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTrainingMethod() {
        return trainingMethod;
    }

    public void setTrainingMethod(String trainingMethod) {
        this.trainingMethod = trainingMethod;
    }

    public BigDecimal getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(BigDecimal coursePrice) {
        this.coursePrice = coursePrice;
    }

    public BigDecimal getAppPrice() {
        return appPrice;
    }

    public void setAppPrice(BigDecimal appPrice) {
        this.appPrice = appPrice;
    }

    public String getBoutiqueVideo() {
        return boutiqueVideo;
    }

    public void setBoutiqueVideo(String boutiqueVideo) {
        this.boutiqueVideo = boutiqueVideo;
    }

    public String getBoutiqueUrl() {
        return boutiqueUrl;
    }

    public void setBoutiqueUrl(String boutiqueUrl) {
        this.boutiqueUrl = boutiqueUrl;
    }
}
