package com.clio.greenbean.dto;

/**
 * created by 吾乃逆世之神也 on 2020/3/13
 */
public class UserRatingDto {
    private Integer bookId;
    private Integer type;
    private Integer score;
    private Integer userId;
    
    public Integer getBookId() {
        return bookId;
    }
    
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
    
    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
