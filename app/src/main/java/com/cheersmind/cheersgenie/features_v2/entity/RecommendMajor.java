package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 推荐专业
 */
public class RecommendMajor extends DataSupport implements Serializable {

    @InjectMap(name = "major_name")
    private String major_name;

    //中文名称
    @InjectMap(name = "major_code")
    private String major_code;

    //来源 1 高考学科推荐 2 MBTI性格测试 3 职业兴趣 4 工作价值观
    @InjectMap(name = "from_types")
    private String from_types;

    //要求的学科组合，举例： 1004||1004,1005||1005|1006 这个代表有三种组合要求，用||分隔，每种组合里面用逗号分隔的代表要同时满足，用|分隔的代表符合一个即可
    @InjectMap(name = "require_subjects")
    private String require_subjects;

    //有学科要求的学校数量 1||2||4 这个和required_subjects 一一对应，顺序也一样
    @InjectMap(name = "require_university_num")
    private String require_university_num;

    //是否选中
    private boolean isSelected;


    public String getMajor_name() {
        return major_name;
    }

    public void setMajor_name(String major_name) {
        this.major_name = major_name;
    }

    public String getMajor_code() {
        return major_code;
    }

    public void setMajor_code(String major_code) {
        this.major_code = major_code;
    }

    public String getFrom_types() {
        return from_types;
    }

    public void setFrom_types(String from_types) {
        this.from_types = from_types;
    }

    public String getRequire_subjects() {
        return require_subjects;
    }

    public void setRequire_subjects(String require_subjects) {
        this.require_subjects = require_subjects;
    }

    public String getRequire_university_num() {
        return require_university_num;
    }

    public void setRequire_university_num(String require_university_num) {
        this.require_university_num = require_university_num;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals(Object obj) {
//        return super.equals(obj) || this.major_code.equals(((RecommendMajor)obj).major_code);
        if(this == obj) {
            return true;
        }
        if(null == obj) {
            return false;
        }
        if(this.getClass() != obj.getClass()) {
            return false;
        }

        RecommendMajor major = (RecommendMajor) obj;
        return this.major_code.equals(major.major_code);
    }

    @Override
    public int hashCode() {
        int result = major_name != null ? major_name.hashCode() : 0;
        result = 31 * result + (major_code != null ? major_code.hashCode() : 0);
        result = 31 * result + (from_types != null ? from_types.hashCode() : 0);
        result = 31 * result + (require_subjects != null ? require_subjects.hashCode() : 0);
        result = 31 * result + (require_university_num != null ? require_university_num.hashCode() : 0);
        result = 31 * result + (isSelected ? 1 : 0);
        return result;
    }

}
