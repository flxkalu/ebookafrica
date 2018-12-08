package com.felixkalu.e_bookafrica;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Date;

public class Books {

    private String bPublisher, bRentPrice, bBuyPrice,bookCoverLink, bTitle, bAuthor, bookLink,
            bDescription, bookId;
    private Date bPublishDate;

    public Books(String bAuthor, String bTitle, String bPublisher, String bDescription,
                 String bookCoverLink, String bookLink, String bookId) {
        this.bookCoverLink = bookCoverLink;
        this.bTitle = bTitle;
        this.bAuthor = bAuthor;
        this.bookLink = bookLink;
        this.bPublisher = bPublisher;
        this.bDescription = bDescription;
        this.bookId = bookId;


    }

    public String getBookLink() {
        return bookLink;
    }

    public void setBookLink(String bookLink) {
        this.bookLink = bookLink;
    }

    public String getBookCoverLink() {
        return bookCoverLink;
    }

    public void setBookCoverLink(String bookCoverLink) {
        this.bookCoverLink = bookCoverLink;
    }

    public String getbTitle() {
        return bTitle;
    }

    public void setbTitle(String bTitle) {
        this.bTitle = bTitle;
    }

    public String getbAuthor() {
        return bAuthor;
    }

    public void setbAuthor(String bAuthor) {
        this.bAuthor = bAuthor;
    }

    public String getbPublisher() {
        return bPublisher;
    }

    public void setbPublisher(String bPublisher) {
        this.bPublisher = bPublisher;
    }

    public String getbDescription() {
        return bDescription;
    }

    public void setbDescription(String bDescription) {
        this.bDescription = bDescription;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }



}
