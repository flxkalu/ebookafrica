package com.felixkalu.e_bookafrica;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpenPdfFragment extends android.app.Fragment {

    public OpenPdfFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_open_pdf, container, false);

        String bookTitle = getArguments().getString("bookTitle");

        getActivity().setTitle(bookTitle);

        PDFView pdfView = (PDFView)v.findViewById(R.id.pdfView);
        //pdfView.fromSource(getActivity().getFilesDir().getAbsolutePath()+"/"+bookTitle).load();
        pdfView.fromFile(new File(getActivity().getFilesDir().getAbsolutePath() + "/" + bookTitle)).load();

        return v;


    }


}
