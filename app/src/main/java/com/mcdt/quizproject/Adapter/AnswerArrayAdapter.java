package com.mcdt.quizproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mcdt.quizproject.Model.HighScore;
import com.mcdt.quizproject.Model.Question;
import com.mcdt.quizproject.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AnswerArrayAdapter extends ArrayAdapter<String>
{
    private Context m_context;
    private List<String> m_answers;

    public AnswerArrayAdapter(@NonNull Context context, ArrayList<String> list) {
        super(context, 0 , list);
        m_context = context;
        m_answers = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        final String answer = m_answers.get(position);

        if (listItem == null) {
            int layoutId = 0;
            layoutId = R.layout.list_item_answer;
            listItem = LayoutInflater.from(m_context).inflate(layoutId, parent,false);
        }

        final TextView textViewListItemAnswer = listItem.findViewById(R.id.textViewListItemAnswer);
        textViewListItemAnswer.setText(answer);

        return listItem;
    }
}
