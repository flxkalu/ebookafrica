package com.felixkalu.e_bookafrica;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.folioreader.FolioReader;
import com.folioreader.ui.folio.activity.FolioActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static android.content.Context.ACTIVITY_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookDetails extends android.app.Fragment {


    //for the download progress bar.

    public static final int progress_bar_type = 0;

   // private static String file_url = "https://res.cloudinary.com/flxkalu/image/upload/v1534281156/1534281146036sample_rm4tl7.pdf";

    private ListView listView;
    private CommentAdapter commentAdapter;
    CountDownTimer countDownTimer;
    String fileName = ""; //this is the file name that the file is saved in when the ebookfile is downloaded

    //======================book fields=========
    String bAuthor;
    String bTitle;
    String bPublisher;
    String bDescription;
    String bCoverPictureLink;
    String ebookLink;
    String bookId;
    //================end of book fields=========

    public BookDetails() {
        // Required empty public constructor
    }

    //what the preview button should do when it is tapped
    public void previewButtonClick(View view) {
        // Log.i("Preview Button clicked", "!!!!!");
        final Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        final FolioReader folioReader = FolioReader.getInstance(getActivity().getApplicationContext());

        folioReader.openBook("file:///android_asset/books/Metamorphosis-jackson.epub");

        //this opens a book for a period of time for the user to preview it before it shuts down.
        countDownTimer = new CountDownTimer(20000 + 100, 1000) {
            //while it's ticking
            @Override
            public void onTick(long millisUntilFinished) {
                //the if statement starts the click on the app when the counter gets to 10 seconds
                if ((int) millisUntilFinished / 1000 <= 10) {
                    Log.i("Remaining: " + Long.toString(millisUntilFinished / 1000), " remaining");
                    //mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tick);
                    //mPlayer.start();
                }
                //updateTimer((int) millisUntilFinished / 1000);
            }
            //after the countdown is done, close the folioreader activity
            @Override
            public void onFinish() {
                Log.i("Closing ", "Time");
                startActivity(intent);
            }
        }.start();
    }


    public void rentButtonClick(View view) {
        Log.i("Rent Button ", "Clicked!!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_book_details, container, false);
        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        //preview button on BooksDetails fragment
        final Button previewButton = (Button) v.findViewById(R.id.previewBookButton);
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewButtonClick(previewButton);
            }
        });

        final Button buyButton = (Button) v.findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Buy Button", " Clicked!!");
                new DownloadFileFromURL().execute(ebookLink);
                //buyButtonClick(buyButton);
            }
        });

        final Button rentButton = (Button) v.findViewById(R.id.rentButton);
        rentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rentButtonClick(rentButton);
            }
        });

        bAuthor = getArguments().getString("author");
        bTitle = getArguments().getString("title");
        bPublisher = getArguments().getString("publisher");
        bDescription = getArguments().getString("description");
        bCoverPictureLink = getArguments().getString("coverPictureLink");
        ebookLink = getArguments().getString("ebookLink");
        bookId = getArguments().getString("bookId");

        //================This is for retrieving comments of books and details ============

        listView = (ListView) v.findViewById(R.id.commentListView);
        final ArrayList<Comments> commentList = new ArrayList<>();

        //setting up the query we need to search the parse server
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Comments");
        query.whereEqualTo("bookId", bookId);
        //query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> comments, ParseException e) {

                //if the exception is null...
                if (e == null) {
                    Log.i("findInBackground", "Retrieved " + comments.size() + " comments");
                    if (comments.size() > 0) {
                        for (ParseObject comment : comments) {
                            String author = comment.get("author").toString();
                            String theComment = comment.get("comment").toString();

                            Log.i("===COMMENTID==== ", comment.getObjectId());
                            commentList.add(new Comments(author, theComment));
                        }
                    }
                }
                try {
                    progressBar.setVisibility(View.GONE);

                    commentAdapter = new CommentAdapter(getActivity(), commentList);
                    listView.setAdapter(commentAdapter);

                    listView.setTextFilterEnabled(true);

                    ImageView coverPhoto = (ImageView) v.findViewById(R.id.coverPhoto);
                    Picasso.get().load(bCoverPictureLink).resize(255, 165).into(coverPhoto);

                    TextView detailsTextView = (TextView) v.findViewById(R.id.detailsTextView);
                    detailsTextView.setText(bTitle + " by " + bAuthor + " Published by " + bPublisher);

                    Log.i("DETAILS OF BOOK: ", bTitle + " " + bAuthor + " " + bCoverPictureLink);
                } catch (Exception eg) {
                    Log.i("INNER EXCEPTION ", "page " + eg.getMessage());
                }
            }
        });
        return v;
    }
    //=======================Book Download Task================

    public class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            getActivity().showDialog(progress_bar_type);
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                String storageDir = getActivity().getFilesDir().getAbsolutePath()+"/";

                Log.i("REAL PATH", storageDir);

                //creating the filename that this file would be saved in.
                String downloadedFileFormat = "";
                if(ebookLink.endsWith(".epub")||ebookLink.endsWith(".EPUB")){
                    downloadedFileFormat = ".epub";
                } else if(ebookLink.endsWith(".pdf")||ebookLink.endsWith(".PDF")){
                    downloadedFileFormat = ".pdf";
                }

                fileName = bTitle.concat(downloadedFileFormat);
                File downloadedFile = new File(storageDir+fileName.replaceAll("\\s+","_"));
                OutputStream output = new FileOutputStream(downloadedFile);

                byte data[] = new byte[1024];
                long total = 0;

                while((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" +(int)((total*100)/lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.i("Error: ", e.getMessage());
            }
            return null;
        }
        //to update the progressbar...
        protected void onProgressUpdate(String... progress) {
            MainActivity.pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        //to dismiss the progress bar when the download is complete and save somewhere
        @Override
        protected void onPostExecute(String file_url){
            getActivity().dismissDialog(progress_bar_type);

            String downloadedFilePath = getActivity().getFilesDir().getAbsolutePath() + "/"+ fileName;

                Log.i("PATH", "to downloadeded file " +downloadedFilePath);
                Toast.makeText(getActivity(), "Download complete, Check MyEbooks Link", Toast.LENGTH_LONG).show();
        }
    }

    //method for displaying the amount of internal memory left on the device on the Log.i.
    @TargetApi(18)
    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return formatSize(availableBlocks * blockSize);
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

}
