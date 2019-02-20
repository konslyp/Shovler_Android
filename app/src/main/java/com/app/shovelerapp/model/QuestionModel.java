package com.app.shovelerapp.model;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by supriya.n on 30-09-2016.
 */
public class QuestionModel {

    /**
     * question : How much you satisfied
     * idQuestion : 321
     * answerType : 1
     * answerTypeOptions : ["Very Satisfied","Satisfied","Neither Satisfied Nor Dissatisfied","Dissatisfied","Very Dissatisfied"]
     */

    private String question;
    private int idQuestion;
    private int answerType;
    private List<String> answerTypeOptions;

    public static QuestionModel objectFromData(String str) {

        return new Gson().fromJson(str, QuestionModel.class);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public int getAnswerType() {
        return answerType;
    }

    public void setAnswerType(int answerType) {
        this.answerType = answerType;
    }

    public List<String> getAnswerTypeOptions() {
        return answerTypeOptions;
    }

    public void setAnswerTypeOptions(List<String> answerTypeOptions) {
        this.answerTypeOptions = answerTypeOptions;
    }
}
