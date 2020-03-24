package com.clio.greenbean.spring.service;

import com.clio.greenbean.domain.Author;
import com.clio.greenbean.domain.Book;
import com.clio.greenbean.domain.Translator;
import com.clio.greenbean.dto.*;
import com.clio.greenbean.mybatis.mapper.BookMapper;
import com.clio.greenbean.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * created by 吾乃逆世之神也 on 2020/1/2
 */
@Service
public class BookService {
    private BookMapper bookMapper;

    @Autowired
    public BookService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }
    
    @Value("#{${greenbean.pagination.size}}")
    private Integer paginationSize;
    
    public SearchPageDTO getSearchPage(String keyword ,Integer offset){
        SearchPageDTO searchPageDTO = new SearchPageDTO();
        List<BookItemsDTO> bookItemsDTOS = this.getSearchBooksInOnePage(keyword,offset);
        searchPageDTO.setBookItemsList(bookItemsDTOS);
        Integer totalItemsCount = this.getSearchBooksCount(keyword);
        PaginationVo paginationVo = new PaginationVo(paginationSize, offset, totalItemsCount);
        searchPageDTO.setPaginationVo(paginationVo);
        return searchPageDTO;
    }
    
    @Transactional
    public void saveBook(BookDTO bookDTO){
        Book book = this.generatedBook(bookDTO);
        this.insertBookBasicInfo(book);
        //XXX 待重构的代码
        //XXX 作者和译者的重复代码
        List<String> authorNames = bookDTO.getAuthor();
        List<Integer> authorIds = new ArrayList<>();
        for(String name : authorNames){
            List<Integer> id = this.bookMapper.getAuthorIdByName(name);
            if (id.size() >= 1) {
                authorIds.add(id.get(0));
            } else {
                Author author = new Author();
                author.setName(name);
                this.bookMapper.insertAuthorByName(author);
                authorIds.add(author.getId());
            }
        }
        if(authorIds.size() > 0){
            this.insertBookAuthor(book.getId(),authorIds);
        }
    
        List<String> translatorNames = bookDTO.getTranslator();
        List<Integer> translatorIds = new ArrayList<>();
        for(String name : translatorNames){
            List<Integer> id = this.bookMapper.getTranslatorIdByName(name);
            if (id.size() >= 1) {
                translatorIds.add(id.get(0));
            } else {
                Translator translator = new Translator();
                translator.setName(name);
                this.bookMapper.insertTranslatorByName(translator);
                translatorIds.add(translator.getId());
            }
        }
        if(translatorIds.size() > 0){
            this.insertBookTranslator(book.getId(),translatorIds);
        }
    }
    
    public List<String> getAuthorSuggestion(String authorSuggestion){
        return this.bookMapper.getAuthorSuggestion(authorSuggestion);
    }
    
    public List<String> getTranslatorSuggestion(String translatorSuggestion){
        return this.bookMapper.getTranslatorSuggestion(translatorSuggestion);
    }
    
    public BookPageDTO getBookPageDTO(Integer bookId, Integer userId) {
        return this.getBookPage(bookId, userId);
    }
    
    private Book generatedBook(BookDTO bookDTO){
        Book book = new Book();
        //XXX 书的反射处理
        book.setName(bookDTO.getName());
        book.setIsbn(bookDTO.getIsbn());
        book.setPrice(bookDTO.getPrice());
        book.setPublisher(bookDTO.getPublisher());
        book.setPublishYear(bookDTO.getPublicationYear());
        book.setSubtitle(bookDTO.getSubtitle());
        book.setOriginalName(bookDTO.getOriginalName());
        book.setBinding(bookDTO.getBinding());
        book.setPage(bookDTO.getPage());
        book.setContentIntro(bookDTO.getContentIntro());
        book.setAuthorIntro(bookDTO.getAuthorIntro());
        book.setDirectory(bookDTO.getDirectory());
        
        if(bookDTO.getPublicationMonth() != 0){
            book.setPublishMonth(bookDTO.getPublicationMonth());
        }
        if(bookDTO.getPublicationDay() != 0){
            book.setPublishDay(bookDTO.getPublicationDay());
        }
        return book;
    }
    
    private void insertBookBasicInfo(Book book){
        this.bookMapper.insertBookBasicInfo(book);
    }
    
    private void insertBookAuthor(Integer bookId,List<Integer> author){
        this.bookMapper.insertBookAuthor(bookId, author);
    }
    
    private void insertBookTranslator(Integer bookId,List<Integer> translator){
        this.bookMapper.insertBookTranslator(bookId, translator);
    }
    
    private Integer getSearchBooksCount(String keyword){
        return this.bookMapper.getSearchBooksCount(keyword);
    }
    
    private List<BookItemsDTO> getSearchBooksInOnePage(String keyword, Integer offset){
        List<Map<String, Integer>> searchBookItemsIDs = this.getSearchBooksIDInOnePage(keyword, offset);
        List<BookItemsDTO> SearchBookDTOs = new ArrayList<>();
        for (Map<String, Integer> idMap : searchBookItemsIDs) {
            Integer id = idMap.get("id");
            //TODO 拿到book信息
            SearchBookDTOs.add(this.getBookItemsById(id));
        }
        return SearchBookDTOs;
    }
    
    private BookItemsDTO getBookItemsById(Integer id){
        BookItemsDTO bookItemsDTO = new BookItemsDTO();
        Book book = this.getBooksBaseInfoByID(id);
        this.setBookBriefBasicIntoDTO(book, bookItemsDTO.getBookBriefBasicInfo());
    
        Map<String, Object>  ratings = this.getRatingAndRatingCountByID(id);
        this.setBookBriefStarRatingByID(ratings, bookItemsDTO.getBookBriefStarRating());
        
        return bookItemsDTO;
    }
    
    private BookPageDTO getBookPage(Integer bookId, Integer userId){
        BookPageDTO bookPageDTO = new BookPageDTO();
        Book book = this.getBooksBaseInfoByID(bookId);
        this.setBookBriefBasicIntoDTO(book, bookPageDTO.getBookBriefBasicInfo());
        this.setBookDetailBasicIntoDTO(book, bookPageDTO.getBookDetailBasicInfo());
        
        Map<String, Object>  ratings = this.getRatingAndRatingCountByID(bookId);
        this.setBookBriefStarRatingByID(ratings, bookPageDTO.getBookBriefStarRating());
        
        List<Map<String, Object>> getScoreAndRatingCountGroupByScoreList = this.getScoreAndRatingCountGroupByScore(bookId);
        long totalRatingCount =  (long)ratings.get("ratingCount");
        this.setRatingPercentageList(getScoreAndRatingCountGroupByScoreList, bookPageDTO.getBookDetailStarRating(), totalRatingCount);
        this.setRatingPowerPercentageList(getScoreAndRatingCountGroupByScoreList,bookPageDTO.getBookDetailStarRating());
    
        Map<String, Object> userRatings = this.getBookUserRatingsByBookIdAndUserId(bookId, userId);
        this.setBookRatingInfo(bookPageDTO.getBookUserRatingInfo(), userRatings);
        return bookPageDTO;
    }
    
    private Map<String, Object> getBookUserRatingsByBookIdAndUserId(Integer bookId, Integer userId) {
        return this.bookMapper.getBookUserRatingsByBookIdAndUserId(bookId, userId);
    }
    
    private void setBookRatingInfo(BookUserRatingInfo bookUserRatingInfo, Map<String, Object> userRatings) {
        if(userRatings != null && !userRatings.isEmpty()){
            Integer type = (Integer) userRatings.get("type");
            bookUserRatingInfo.setType(type);
            Integer score = (Integer) userRatings.get("score");
            if(score != null) {
                bookUserRatingInfo.setStar(score / 2);
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String time = dateFormat.format((Timestamp)userRatings.get("time"));
            bookUserRatingInfo.setTime(time);
            bookUserRatingInfo.setComment((String)userRatings.get("comment"));
        }
    }
    
    private List<Map<String, Integer>> getSearchBooksIDInOnePage(String keyword, Integer offset){
        return bookMapper.getSearchBooksWithPagination(keyword, offset, paginationSize);
    }
    
    private Book getBooksBaseInfoByID (Integer id) {
        return this.bookMapper.getBooksBaseInfoByID(id);
    }
    
    private Map<String, Object> getRatingAndRatingCountByID(Integer id) {
        return this.bookMapper.getRatingAndRatingCountByID(id);
    }
    
    private List<Map<String, Object>> getScoreAndRatingCountGroupByScore(Integer id){
        return this.bookMapper.getScoreAndRatingCountGroupByScore(id);
    }
    
    private BookBriefBasicInfo setBookBriefBasicIntoDTO(Book book, BookBriefBasicInfo bookBriefBasicInfo){
        this.setBookBriefBasicInfo(book, bookBriefBasicInfo);
        return bookBriefBasicInfo;
    }
    
    private BookBriefBasicInfo setBookBriefBasicInfo(Book book, BookBriefBasicInfo bookBriefBasicInfo){
        bookBriefBasicInfo.setId(String.valueOf(book.getId()));
        bookBriefBasicInfo.setBookName(book.getName());
        bookBriefBasicInfo.setPicture(book.getPicture());
        bookBriefBasicInfo.setPublisher(book.getPublisher());
        StringBuilder stringBuilder = new StringBuilder();
        Integer year = book.getPublishYear();
        stringBuilder.append(year);
        Integer month = book.getPublishMonth();
        if(month != null) {
            stringBuilder.append("-");
            stringBuilder.append(month);
            Integer day = book.getPublishDay();
            if(day != null) {
                stringBuilder.append("-");
                stringBuilder.append(day);
            }
        }
        bookBriefBasicInfo.setPublicationDate(stringBuilder.toString());
        bookBriefBasicInfo.setPrice(String.valueOf(book.getPrice()));
        
        // XXX set author与translator 重复代码
        List<Author> authorList = book.getAuthors();
        stringBuilder = new StringBuilder();
        for (Author author : authorList) {
            stringBuilder.append(author.getName());
            stringBuilder.append(" / ");
        }
        stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length());
        bookBriefBasicInfo.setAuthorName(stringBuilder.toString());
        
        List<Translator> translatorList = book.getTranslators();
        stringBuilder = new StringBuilder();
        for (Translator translator : translatorList){
            stringBuilder.append(translator.getName());
            stringBuilder.append(" / ");
        }
        if(stringBuilder.length() > 0) {
            stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length());
            bookBriefBasicInfo.setTranslatorName(stringBuilder.toString());
        }
        
        return bookBriefBasicInfo;
    }
    
    private BookDetailBasicInfo setBookDetailBasicIntoDTO(Book book, BookDetailBasicInfo bookDetailBasicInfo){
        bookDetailBasicInfo.setPage(String.valueOf(book.getPage()));
        bookDetailBasicInfo.setSubtitle(book.getSubtitle());
        bookDetailBasicInfo.setOriginalName(book.getOriginalName());
        String bindingStr = null;
        Integer binding = book.getBinding();
        if(binding != null){
            if(binding == 1){
                bindingStr = "平装";
            } else if(binding == 2){
                bindingStr = "精装";
            }
            bookDetailBasicInfo.setBinding(bindingStr);
        }
        bookDetailBasicInfo.setIsbn((book.getIsbn()));
        bookDetailBasicInfo.setContentIntroList(this.separateParagraph(book.getContentIntro()));
        bookDetailBasicInfo.setAuthorIntroList(this.separateParagraph(book.getAuthorIntro()));
        bookDetailBasicInfo.setDirectoryList(this.separateParagraph(book.getDirectory()));
        return bookDetailBasicInfo;
    }
    
    private List<String> separateParagraph(String str){
        List<String> list = null;
        // 使用org.apache.commons.lang3.StringUtils
        if(StringUtils.isNotBlank(str)){
            list = Arrays.asList(str.split("\\n+"));
        }
        return list;
    }
    
    private void setBookBriefStarRatingByID(Map<String, Object>  ratings, BookBriefStarRating bookBriefStarRating) {
        Long ratingCount = (Long)ratings.get("ratingCount");
        bookBriefStarRating.setRatingCount(String.valueOf(ratingCount));
        if(ratingCount > 0){
            BigDecimal rating = (BigDecimal) ratings.get("rating");
            BigDecimal ratingWithOneDecimal = rating.setScale(1, RoundingMode.HALF_UP);
            bookBriefStarRating.setRating(String.valueOf(ratingWithOneDecimal));
           
            DecimalFormat ratingFormat = new DecimalFormat("#");
            BigDecimal ratingWithTwoNum = rating.divide(new BigDecimal(2)).multiply(new BigDecimal(10));
            String starSuffix = ratingFormat.format(ratingWithTwoNum);
            //XXX 使得starRatingName的值是5的倍数，如 ： 05，10，15，20，25，30，35，40，45，50
            String starRatingName = starSuffix;
            bookBriefStarRating.setStarRatingName(starRatingName);
        } else {
            //XXX 修改硬代码
            bookBriefStarRating.setStarRatingName("00");
        }
    }
    
    private void setRatingPercentageList(List<Map<String, Object>> getScoreAndRatingCountGroupByScoreList, BookDetailStarRating bookDetailStarRating, long ratingCount) {
        bookDetailStarRating.setRatingPercentageList(this.createPercentage(getScoreAndRatingCountGroupByScoreList, ratingCount));
    }
    
    private void setRatingPowerPercentageList(List<Map<String, Object>> getScoreAndRatingCountGroupByScoreList, BookDetailStarRating bookDetailStarRating) {
        long maxPercentage = 0;
        for(Map<String, Object> scoreAndRatingCountGroupByScoreMap : getScoreAndRatingCountGroupByScoreList){
            long ratingCount = (long)scoreAndRatingCountGroupByScoreMap.get("ratingCount");
           if(ratingCount > maxPercentage){
               maxPercentage = ratingCount;
           }
        }
        bookDetailStarRating.setRatingPowerPercentageList(this.createPercentage(getScoreAndRatingCountGroupByScoreList,maxPercentage));
    }
    
    private List<String> createPercentage(List<Map<String, Object>> getScoreAndRatingCountGroupByScoreList, long divisor){
        List<String> percentageList = new ArrayList<>();
        if(divisor != 0) {
            // 初始化 list
            for (int i = 0; i < 5; i++) {
                String percentageStr = "0.0%";
                percentageList.add(percentageStr);
            }
            for (Map<String, Object> scoreAndRatingCountGroupByScoreMap : getScoreAndRatingCountGroupByScoreList) {
                BigDecimal ratingCountBigDecimal = new BigDecimal((Long) scoreAndRatingCountGroupByScoreMap.get("ratingCount"));
                BigDecimal totalRatingCountBigDecimal = new BigDecimal(divisor);
                DecimalFormat format = new DecimalFormat("#.0%");
                String singleRatingCount = format.format(ratingCountBigDecimal.divide(totalRatingCountBigDecimal, 3, RoundingMode.HALF_UP).doubleValue());
                Integer score = (Integer) scoreAndRatingCountGroupByScoreMap.get("score");
                /* index的算法
                 * score / 2 - 1 ： 从数据库取出的 score 值可分别为2，4，6，8，10五种情况，List 数组长度为5，把这五种情况的值放在List的index为0到4的五个元素中
                 * 4 - (score / 2 - 1) ： 倒序
                 * */
                percentageList.set(4 - (score / 2 - 1), singleRatingCount);
            }
        }
        return percentageList;
    }
    
    public void saveOrUpdateUserRating(UserRatingDTO userRatingDto) {
        Integer bookId = userRatingDto.getBookId();
        Integer userId = userRatingDto.getUserId();
        if(this.isUserRatingExisted(bookId, userId)){
           this.bookMapper.updateUserRating(userRatingDto);
        } else {
            this.bookMapper.insertUserRating(userRatingDto);
        }
    }
    
    private boolean isUserRatingExisted(Integer bookId, Integer userId){
        int count = this.bookMapper.getUserRatingCount(bookId, userId);
        if(count > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public void removeUserRating(Integer bookId, Integer userId) {
        // TODO 业务层验证“评论”是否存在 存在则删除
        this.bookMapper.removeUserRating(bookId, userId);
    }
}
