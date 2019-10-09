package com.mcdt.quizproject.Model;

import java.util.Arrays;
import java.util.List;

public class HighScore
{
    private List<String> m_items = Arrays.asList(new String[3]);

    // items for high score can be of arbitrary nature,
    // for example nick, time and a game score
    public HighScore(final String item1, final String item2, final String item3) {
        m_items.set(0, item1);
        m_items.set(1, item2);
        m_items.set(2, item3);
    }

    public List<String> getItems() {
        return m_items;
    }

    public void setItemValue(final int index, final String value) {
        // assert valid range for index
        assert 0 <= index && index <= 2;
        m_items.set(index, value);
    }
}