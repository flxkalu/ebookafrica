package com.felixkalu.e_bookafrica;

public class Comments {
    private String author, comment, bookId;

    public Comments(String author, String comment) {
        this.author = author;
        this.comment = comment;
    }

    public String getAuthor() {

        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
