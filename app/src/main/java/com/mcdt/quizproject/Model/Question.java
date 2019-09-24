package com.mcdt.quizproject.Model;

import java.util.List;

public class Question
{
    private String m_question;
    private List<String> m_answers;
    private String m_category;
    private String m_difficulty;

    public Question(final String question, final List<String> answers,
                    final String category, final String difficulty) {
        m_question = question;
        m_answers = answers;
        m_category = category;
        m_difficulty = difficulty;
    }

    public String getQuestion() {
        return m_question;
    }

    public List<String> getAnswers() {
        return m_answers;
    }

    public String getCategory() {
        return m_category;
    }

    public String getDifficulty() {
        return m_difficulty;
    }
}
