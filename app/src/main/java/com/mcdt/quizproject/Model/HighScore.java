package com.mcdt.quizproject.Model;

import java.util.Arrays;
import java.util.List;

public class HighScore
{
    List<String> m_items = Arrays.asList(new String[2]);

    // items for high score can be of arbitrary nature,
    // for example nick/time and a game score
    public HighScore(String item1, String item2) {
        m_items.set(0, item1);
        m_items.set(1, item2);
    }

    public List<String> getItems() {
        return m_items;
    }
}