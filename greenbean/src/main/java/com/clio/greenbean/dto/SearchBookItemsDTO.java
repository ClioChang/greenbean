package com.clio.greenbean.dto;

/**
 * created by 吾乃逆世之神也 on 2020/1/2
 */
public class SearchBookItemsDTO {
    private String bookName;
    private String starRatingName;
    private String rating;
    private String ratingCount;
    private String authorName;
    private String translatorName;
    private String publisher;
    private String publishDate;
    private String price;
    private String isbn;
  
    public String getBookName() {
        return bookName;
    }
    
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    
    public String getStarRatingName() {
        return starRatingName;
    }
    
    public void setStarRatingName(String starRatingName) {
        this.starRatingName = starRatingName;
    }
    
    public String getRating() {
        return rating;
    }
    
    public void setRating(String rating) {
        this.rating = rating;
    }
    
    public String getRatingCount() {
        return ratingCount;
    }
    
    public void setRatingCount(String ratingCount) {
        this.ratingCount = ratingCount;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public String getTranslatorName() {
        return translatorName;
    }
    
    public void setTranslatorName(String translatorName) {
        this.translatorName = translatorName;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public String getPublishDate() {
        return publishDate;
    }
    
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
