package com.example.m1032561.pomodoroprototype.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.m1032561.pomodoroprototype.R;
import com.example.m1032561.pomodoroprototype.model.LogModel;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by M1032467 on 1/10/2017.
 */

public class LogViewerAdapter extends RecyclerView.Adapter<LogViewerAdapter.LogViewHolder> {

    private List<LogModel> data;
    private OnDataClickListener mOnDataClickListener;

    public class LogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView note;
        TextView timeSpent;
        TextView date;
        TextView time;
        CardView cardView;

        LogViewHolder(View itemView) {
            super(itemView);
            note = (TextView) itemView.findViewById(R.id.note);
            timeSpent = (TextView) itemView.findViewById(R.id.time_spent);
            date = (TextView) itemView.findViewById(R.id.note_date);
            time = (TextView) itemView.findViewById(R.id.note_time);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
        }
    }

    public LogViewerAdapter(List<LogModel> data, OnDataClickListener listener) {
        mOnDataClickListener = listener;
        this.data = data;
    }

    @Override
    public LogViewerAdapter.LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_log_viewer, parent, false);

        final LogViewHolder myViewHolder = new LogViewHolder(view);
        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = myViewHolder.getAdapterPosition();
                LogModel results = data.get(position);
                mOnDataClickListener.onDataClick(results);
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(LogViewerAdapter.LogViewHolder holder, int position) {
        TextView note = holder.note;
        TextView timeSpent = holder.timeSpent;
        TextView date = holder.date;
        TextView time = holder.time;
        CardView cardView = holder.cardView;

        SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yy");
        SimpleDateFormat time_format = new SimpleDateFormat("HH:mm");
        note.setText(data.get(position).getNote());
        timeSpent.setText(data.get(position).getEllapsedDur());
        date.setText(date_format.format(data.get(position).getCreated_at()));
        time.setText(time_format.format(data.get(position).getCreated_at()));
        if (Integer.parseInt(data.get(position).getEllapsedDur()) <=
                Integer.parseInt(data.get(position).getActualDur())) {
            cardView.setCardBackgroundColor(cardView.getContext().getResources().getColor(R.color.colorPrimaryDark));
        } else {
            cardView.setCardBackgroundColor(R.color.colorPrimaryDark);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnDataClickListener {
        void onDataClick(LogModel imageURL);
    }
}
