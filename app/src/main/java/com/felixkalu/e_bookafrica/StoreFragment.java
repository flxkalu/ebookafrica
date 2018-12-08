package com.felixkalu.e_bookafrica;

import android.app.DownloadManager;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;

public class StoreFragment extends android.app.Fragment implements SearchView.OnQueryTextListener {

    private GridView gridView;
    private BookAdapter bookAdapter;
    private DownloadManager downloadManager;

    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_store, container, false);

        SearchView searchView = (SearchView) v.findViewById(R.id.searchView1);
        searchView.setOnQueryTextListener(this);

        //for progressBar
        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress_loader);

         //finding the listView
         gridView = (GridView) v.findViewById(R.id.books_list);
         final ArrayList<Books> booksList = new ArrayList<>();

                 //setting up the query we need to search the parse server
                 ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Books");
                 query.orderByAscending("Title");

                 query.findInBackground(new FindCallback<ParseObject>() {
                     @Override
                     public void done(List<ParseObject> books, ParseException e) {

                         //if the exception is null...
                         if (e == null) {
                             Log.i("findInBackground", "Retrieved " + books.size() + " books");
                             if (books.size() > 0) {
                                 for (ParseObject book : books) {
                                     Log.i("Author: ", book.get("Author").toString());

                                     String author = book.get("Author").toString();
                                     String title = book.get("Title").toString();
                                     String publisher = book.get("publisher").toString();
                                     String description = book.get("description").toString();
                                     String coverPictureLink = book.get("coverPictureLink").toString();
                                     String bookLink = book.get("ebookLink").toString();
                                     //to get the book Id we use getObjectId. refer to parse documentation for android
                                     String bookId = book.getObjectId().toString();

                                     Log.i("===BOOKID==== ", bookId);
                                     booksList.add(new Books(author, title, publisher, description, coverPictureLink, bookLink, bookId));
                                 }
                             }
                         }
                         try {
                             progressBar.setVisibility(View.GONE);
                             bookAdapter = new BookAdapter(getActivity(), booksList);
                             gridView.setAdapter(bookAdapter);

                             gridView.setTextFilterEnabled(true);
                         }
                         catch (Exception ex){
                             Log.i("EXCEPTION ERROR", ex.getMessage());
                         }
                     }
                 });

                 gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                     @Override
                     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                         BookDetails bookDetails = new BookDetails();
                         FragmentManager fm = getFragmentManager();
                         android.app.FragmentTransaction ft = fm.beginTransaction();
                         Bundle args = new Bundle();

                         //for sending the content of the clicked listview to the next Fragment where it is displayed in detail.
                         args.putString("author", booksList.get(position).getbAuthor());
                         args.putString("title", booksList.get(position).getbTitle());
                         args.putString("publisher", booksList.get(position).getbPublisher());
                         args.putString("description", booksList.get(position).getbDescription());
                         args.putString("coverPictureLink", booksList.get(position).getBookCoverLink());
                         args.putString("ebookLink", booksList.get(position).getBookLink());
                         //this line sends the book id that we would use to find all comments about  a particular book on the book details fragment
                         args.putString("bookId", booksList.get(position).getBookId());

                         bookDetails.setArguments(args);
                         ft.replace(R.id.frame, bookDetails);
                         ft.addToBackStack(null);
                         ft.commit();
                     }
                 });
        return v;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //this is for the searchView
    @Override
    public boolean onQueryTextChange(String newText) {
        Log.i("SEARCHVIEW: ", newText.toString());
        if (TextUtils.isEmpty(newText)) {
            bookAdapter.filter("");
            gridView.clearTextFilter();
        } else {
            bookAdapter.filter(newText);
        }
        return true;
    }

}

