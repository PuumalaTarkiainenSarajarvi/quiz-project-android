package com.mcdt.quizproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mcdt.quizproject.Model.HighScore;
import com.mcdt.quizproject.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HighScoreArrayAdapter extends ArrayAdapter<HighScore>
{
    private Context m_context;
    private List<HighScore> m_highScores;

    public HighScoreArrayAdapter(@NonNull Context context, ArrayList<HighScore> list) {
        super(context, 0 , list);
        m_context = context;
        m_highScores = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        final HighScore highScore = m_highScores.get(position);

        if(listItem == null) {
            int layoutId = 0;
            layoutId = R.layout.list_item;
            listItem = LayoutInflater.from(m_context).inflate(layoutId, parent,false);
        }

        final TextView textViewListItem1 = (TextView)listItem.findViewById(R.id.textViewListItem1);
        textViewListItem1.setText(highScore.getItems().get(0));

        final TextView textViewListItem2 = (TextView)listItem.findViewById(R.id.textViewListItem2);
        textViewListItem2.setText(highScore.getItems().get(1));

        return listItem;
    }
}