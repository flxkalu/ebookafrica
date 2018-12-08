package com.felixkalu.e_bookafrica;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.folioreader.FolioReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyEbooksFragment extends android.app.Fragment {

    //for the download progress bar.
    private ProgressDialog pDialog;

    public MyEbooksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.my_ebooks_fragment, container, false);

        getActivity().setTitle("My Ebooks");

        ArrayList<String> books = getBookFileNamesFromInternalStorage();

        ListView bookListView = (ListView) v.findViewById(R.id.bookListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, books);

        bookListView.setAdapter(arrayAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OpenPdfFragment fragment = new OpenPdfFragment();
                String bookFileName = parent.getItemAtPosition(position).toString();

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                //to send book name to the next fragment where the book would be opened
                Bundle args = new Bundle();
                args.putString("bookTitle", bookFileName);
                fragment.setArguments(args);

                //This block decides if it should open pdf or epub files considering that both readers are different APIs
                if(bookFileName.endsWith(".pdf")) {
                    fragmentTransaction.replace(R.id.frame, fragment, "openPdfFragment");
                    //This line of code takes us back to the MyEbooksFragment when the back button is pressed instead of closing the app.
                    //When this line is removed, after reading a book and the back button is pressed, it closes the entire application.
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if(bookFileName.endsWith(".epub")) {
                    FolioReader folioReader = FolioReader.getInstance(getActivity().getApplicationContext());
                    Log.i("PATHS: " , getActivity().getFilesDir().getAbsolutePath());
                    folioReader.openBook(getActivity().getFilesDir().getAbsolutePath()+"/"+bookFileName);
                }
            }
        });
        return v;
    }

    //this is used to get list of files in the assets folder. This is not used in this app but kept for reference purposes.
    public List getBookFileNames() {
        final AssetManager assetManager = getActivity().getAssets();
        ArrayList<String> books = new ArrayList<>();

          getBookFileNamesFromInternalStorage();

        try {
            // for assets folder add empty string
            String[] filelist = assetManager.list("");

            // for assets/subFolderInAssets add only subfolder name
            String[] filelistInSubfolder = assetManager.list("books");

            if (filelistInSubfolder == null) {
                // dir does not exist or is not a directory
                Log.i("ERROR: ", "There is no such folder!");
            } else {
                for (int i = 0; i < filelistInSubfolder.length; i++) {
                    // Get filename of file or directory
                    books.add(filelistInSubfolder[i]);
                }
                Log.i("FILES: ", books.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    //this is used to get list of files in the device internal storage folder.
    public ArrayList<String> getBookFileNamesFromInternalStorage() {
        ArrayList<String> fileList = new ArrayList<>();
        try {
            File[] files = getActivity().getFilesDir().listFiles();
            for (File file : files) {
                fileList.add(file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileList;
    }
}
