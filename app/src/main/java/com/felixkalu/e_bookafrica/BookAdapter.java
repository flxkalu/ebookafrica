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
import java.util.Locale;

public class BookAdapter extends ArrayAdapter<Books>{

    private Context bContext;
    private ArrayList<Books> booksList = new ArrayList<>(); //Stores the actual booklist
    private ArrayList<Books> copybookList; //this one is useful for the searchView process.

    public BookAdapter(@NonNull Context context, ArrayList<Books> list) {
        super(context, 0, list);
        bContext = context;
        this.booksList = list;
        this.copybookList = new ArrayList<Books>();
        this.copybookList.addAll(booksList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;

        if(listItem == null)
            listItem = LayoutInflater.from(bContext).inflate(R.layout.list_item, parent, false);

        Books currentBook = booksList.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.imageView_poster);
        Picasso.get().load(currentBook.getBookCoverLink()).resize(350, 400).into(image);

        TextView title = (TextView) listItem.findViewById(R.id.textView_name);
        title.setText(currentBook.getbTitle());

        TextView author = (TextView) listItem.findViewById(R.id.textView);
        author.setText(currentBook.getbAuthor());

        return listItem;
    }

    //filter
    //this filter is what does the magic with the searchView
    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        booksList.clear();
        if (charText.length()==0){
            booksList.addAll(copybookList);
        }
        else {
            for (Books book : copybookList){
                //This tells the searchView to search with book title or book Author
                if (book.getbTitle().toLowerCase(Locale.getDefault()).contains(charText)
                        || book.getbAuthor().toLowerCase(Locale.getDefault()).contains(charText)){
                    booksList.add(book);
                }
            }
        }
        notifyDataSetChanged();
    }
}
