package com.example.tictactoe.ui;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tictactoe.R;
import com.example.tictactoe.model.User;

import java.util.List;

public class MyUserRecyclerViewAdapter extends RecyclerView.Adapter<MyUserRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;

    public MyUserRecyclerViewAdapter(List<User> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        int pos = position + 1;

        holder.tvPosition.setText(pos + "°");
        holder.tvPoint.setText(String.valueOf(mValues.get(position).getPoints()));
        holder.tvName.setText(mValues.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public User mItem;
        public final TextView tvPosition;
        public final TextView tvPoint;
        public final TextView tvName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvPosition = view.findViewById(R.id.textViewPosition);
            tvPoint = view.findViewById(R.id.textViewPoint);
            tvName = view.findViewById(R.id.textViewName);
        }

        @Override
        public String toString() {
            return super.toString() + " '"  +tvName.getText() + "'";
        }
    }
}
