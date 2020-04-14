package com.clio.greenbean.dto;

import com.clio.greenbean.domain.Author;
import com.clio.greenbean.domain.Book;
import com.clio.greenbean.domain.Translator;

import java.util.ArrayList;
import java.util.List;

/**
 * created by 吾乃逆世之神也 on 2020/2/12
 */
public class BookDTO {
    private Integer id;
    private String name;
    private String isbn;
    private List<String> author;
    private List<String> translator;
    private String publisher;
    private Integer publicationYear;
    private Integer publicationMonth;
    private Integer publicationDay;
    private float price;
    private String picture;
    private String subtitle;
    private String originalName;
    private Integer binding;
    private Integer page;
    private String contentIntro;
    private String authorIntro;
    private String directory;
    
    public BookDTO() {
    }
    
    public BookDTO(Book book) {
        // XXX 反射 apache commons
        this.id = book.getId();
        this.name = book.getName();
        this.isbn = book.getIsbn();
        this.publisher = book.getPublisher();
        this.publicationYear = book.getPublishYear();
        this.publicationMonth = book.getPublishMonth();
        this.publicationDay = book.getPublishDay();
        this.price = book.getPrice();
        this.picture = book.getPicture();
        this.subtitle = book.getSubtitle();
        this.originalName = book.getOriginalName();
        this.binding = book.getBinding();
        this.page = book.getPage();
        this.contentIntro = book.getContentIntro();
        this.authorIntro = book.getAuthorIntro();
        this.directory = book.getDirectory();
        
        this.author = new ArrayList<>();
        for(Author authorFromBook : book.getAuthors()) {
            this.author.add(authorFromBook.getName());
        }
        this.translator = new ArrayList<>();
        for(Translator translatorFormBook : book.getTranslators()) {
            this.translator.add(translatorFormBook.getName());
        }
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public List<String> getAuthor() {
        return author;
    }
    
    public void setAuthor(List<String> author) {
        this.author = author;
    }
    
    public List<String> getTranslator() {
        return translator;
    }
    
    public void setTranslator(List<String> translator) {
        this.translator = translator;
    }
    
    public float getPrice() {
        return price;
    }
    
    public void setPrice(float price) {
        this.price = price;
    }
    
    public String getPicture() {
        return picture;
    }
    
    public void setPicture(String picture) {
        this.picture = picture;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public Integer getPublicationYear() {
        return publicationYear;
    }
    
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }
    
    public Integer getPublicationMonth() {
        return publicationMonth;
    }
    
    public void setPublicationMonth(Integer publicationMonth) {
        this.publicationMonth = publicationMonth;
    }
    
    public Integer getPublicationDay() {
        return publicationDay;
    }
    
    public void setPublicationDay(Integer publicationDay) {
        this.publicationDay = publicationDay;
    }
    
    public String getSubtitle() {
        return subtitle;
    }
    
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    
    public String getOriginalName() {
        return originalName;
    }
    
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    
    public Integer getBinding() {
        return binding;
    }
    
    public void setBinding(Integer binding) {
        this.binding = binding;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public String getContentIntro() {
        return contentIntro;
    }
    
    public void setContentIntro(String contentIntro) {
        this.contentIntro = contentIntro;
    }
    
    public String getAuthorIntro() {
        return authorIntro;
    }
    
    public void setAuthorIntro(String authorIntro) {
        this.authorIntro = authorIntro;
    }
    
    public String getDirectory() {
        return directory;
    }
    
    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
