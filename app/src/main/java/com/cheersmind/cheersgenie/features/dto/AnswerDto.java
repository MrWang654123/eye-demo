package com.cheersmind.cheersgenie.features.dto;

/**
 * 答案dto
 */
public class AnswerDto {

    //问题ID
    private String question_id;

    //选项ID
    private String option_id;

    //孩子因子ID
    private String child_factor_id;

    //选项文本
    private String option_text;

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    public String getChild_factor_id() {
        return child_factor_id;
    }

    public void setChild_factor_id(String child_factor_id) {
        this.child_factor_id = child_factor_id;
    }

    public String getOption_text() {
        return option_text;
    }

    public void setOption_text(String option_text) {
        this.option_text = option_text;
    }
}
