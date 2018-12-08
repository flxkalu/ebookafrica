package com.felixkalu.e_bookafrica;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comments> {

    private Context bContext;
    private ArrayList<Comments> commentsList = new ArrayList<>(); //Stores the actual bookList

    public CommentAdapter(@NonNull Context context, ArrayList<Comments> list) {
        super(context, 0, list);
        bContext = context;
        this.commentsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View commentList = convertView;

        if(commentList == null)
            commentList = LayoutInflater.from(bContext).inflate(R.layout.comment_list, parent, false);

        Comments currentBookComments = commentsList.get(position);

        TextView author = (TextView) commentList.findViewById(R.id.authorTextView);
        author.setText(currentBookComments.getAuthor());

        TextView comment = (TextView) commentList.findViewById(R.id.commentTextView);
        comment.setText(currentBookComments.getComment());

        return commentList;
    }
}
