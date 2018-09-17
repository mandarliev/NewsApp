package com.example.android.newsapp;

// A News object contains information related to a single article.
public class News {

    // Title, Author, URL, date and section of the article
    String title;
    String author;
    String url;
    String date;
    String section;

    /**
     * Constructs a new News object
     * @param title is the title of the article
     * @param author is the author of the article
     * @param url is the url of the article
     * @param date is the date when the article was released
     * @param section is the section where of the article
     */
    public News(String title, String author, String url, String date, String section) {
        this.title = title;
        this.author = author;
        this.url = url;
        this.date = date;
        this.section = section;
    }

    // Getter and setter methods for the title, author, Url, Date and Section
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    // Make it all into a string
    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", url='" + url + '\'' +
                ", date='" + date + '\'' +
                ", section='" + section + '\'' +
                '}';
    }
}
