package com.cheersmind.cheersgenie.features_v2.entity;

import com.cheersmind.cheersgenie.main.ioc.InjectMap;

import java.io.Serializable;
import java.util.List;

/**
 * 大学的毕业信息
 */
public class CollegeGraduationInfo implements Serializable {

    //ID
    @InjectMap(name = "university_id")
    private String university_id;

    //5年薪资
    @InjectMap(name = "five_year_salary")
    private long five_year_salary;

    //相关信息比率
    @InjectMap(name = "overall")
    private List<CollegeInfoRatio> infoRatio;

    //总体就业率
    @InjectMap(name = "settled")
    private List<CollegeEmploymentRatio> employmentRatio;

    public String getUniversity_id() {
        return university_id;
    }

    public void setUniversity_id(String university_id) {
        this.university_id = university_id;
    }

    public long getFive_year_salary() {
        return five_year_salary;
    }

    public void setFive_year_salary(long five_year_salary) {
        this.five_year_salary = five_year_salary;
    }

    public List<CollegeInfoRatio> getInfoRatio() {
        return infoRatio;
    }

    public void setInfoRatio(List<CollegeInfoRatio> infoRatio) {
        this.infoRatio = infoRatio;
    }

    public List<CollegeEmploymentRatio> getEmploymentRatio() {
        return employmentRatio;
    }

    public void setEmploymentRatio(List<CollegeEmploymentRatio> employmentRatio) {
        this.employmentRatio = employmentRatio;
    }
}
