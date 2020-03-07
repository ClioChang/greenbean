package com.clio.greenbean.spring.service;

import com.clio.greenbean.domain.Author;
import com.clio.greenbean.domain.Book;
import com.clio.greenbean.domain.Translator;
import com.clio.greenbean.dto.BookDTO;
import com.clio.greenbean.dto.BookItemsDTO;
import com.clio.greenbean.dto.SearchPageDTO;
import com.clio.greenbean.mybatis.mapper.BookMapper;
import com.clio.greenbean.vo.PaginationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
            // TODO 拿到book信息
            SearchBookDTOs.add(this.getBookItemsById(id));
        }
        return SearchBookDTOs;
    }
    
    private BookItemsDTO getBookItemsById(Integer id){
        BookItemsDTO dto = new BookItemsDTO();
        Book book = this.getBooksBaseInfoByID(id);
        this.setBookIntoDTO(book, dto);
    
        List<Author> authorList = this.getAuthorByID(id);
        this.setAuthorByID(authorList,dto);
    
        List<Translator> translatorList = this.getTranslatorByID(id);
        this.setTranslatorByID(translatorList,dto);
    
        Map<String, Object>  ratings = this.getRatingAndRatingCountByID(id);
        this.setRatingByID(ratings,dto);
        
        List<Map<String, Object>> getScoreAndRatingCountGroupByScoreList = this.getScoreAndRatingCountGroupByScore(id);
        this.setRatingPercentageList(getScoreAndRatingCountGroupByScoreList,dto);
      
        return dto;
    }
    
    private List<Map<String, Integer>> getSearchBooksIDInOnePage(String keyword, Integer offset){
        return bookMapper.getSearchBooksWithPagination(keyword, offset, paginationSize);
    }
    
    private Book getBooksBaseInfoByID (Integer id) {
        return this.bookMapper.getBooksBaseInfoByID(id);
    }
    
    private List<Author> getAuthorByID(Integer id) {
        return this.bookMapper.getAuthorByID(id);
    }
    
    private List<Translator> getTranslatorByID(Integer id) {
        return this.bookMapper.getTranslatorByID(id);
    }
    
    private Map<String, Object> getRatingAndRatingCountByID(Integer id) {
        return this.bookMapper.getRatingAndRatingCountByID(id);
    }
    
    private List<Map<String, Object>> getScoreAndRatingCountGroupByScore(Integer id){
        return this.bookMapper.getScoreAndRatingCountGroupByScore(id);
    }
    
    private BookItemsDTO setBookIntoDTO(Book book, BookItemsDTO dto){
        dto.setId(String.valueOf(book.getId()));
        dto.setBookName(book.getName());
        dto.setPicture(book.getPicture());
        dto.setPublisher(book.getPublisher());
        StringBuilder publishDate = new StringBuilder();
        Integer year = book.getPublishYear();
        publishDate.append(year);
        Integer month = book.getPublishMonth();
        if(month != null) {
            publishDate.append("-");
            publishDate.append(month);
            Integer day = book.getPublishDay();
            if(day != null) {
                publishDate.append("-");
                publishDate.append(day);
            }
        }
        dto.setPublicationDate(publishDate.toString());
        dto.setPage(String.valueOf(book.getPage()));
        dto.setPrice(String.valueOf(book.getPrice()));
        dto.setSubtitle(book.getSubtitle());
        dto.setOriginalName(book.getOriginalName());
        String bindingStr = null;
        Integer binding = book.getBinding();
        if(binding != null){
            if(binding == 1){
                bindingStr = "平装";
            } else if(binding == 2){
                bindingStr = "精装";
            }
            dto.setBinding(bindingStr);
        }
        dto.setIsbn((book.getIsbn()));
        dto.setContentIntro(book.getContentIntro());
        dto.setAuthorIntro(book.getAuthorIntro());
        dto.setDirectory(book.getDirectory());
        return dto;
    }
    
    private void setAuthorByID(List<Author> authorList, BookItemsDTO dto){
        StringBuilder authorsBuilder = new StringBuilder();
        for (Author author : authorList) {
            authorsBuilder.append(author.getName());
            authorsBuilder.append(" / ");
        }
        authorsBuilder.delete(authorsBuilder.length() - 3, authorsBuilder.length());
        dto.setAuthorName(authorsBuilder.toString());
    }
    
    private void setTranslatorByID(List<Translator> translatorList, BookItemsDTO dto){
        StringBuilder translatorBuilder = new StringBuilder();
        for (Translator translator : translatorList){
            translatorBuilder.append(translator.getName());
            translatorBuilder.append(" / ");
        }
        if(translatorBuilder.length() > 0) {
            translatorBuilder.delete(translatorBuilder.length() - 3, translatorBuilder.length());
            dto.setTranslatorName(translatorBuilder.toString());
        }
    }
    
    private void setRatingByID(Map<String, Object>  ratings, BookItemsDTO dto) {
        Long ratingCount = (Long)ratings.get("ratingCount");
        dto.setRatingCount(String.valueOf(ratingCount));
        if(ratingCount > 0){
            BigDecimal rating = (BigDecimal) ratings.get("rating");
            BigDecimal ratingWithOneDecimal = rating.setScale(1, RoundingMode.HALF_UP);
            dto.setRating(String.valueOf(ratingWithOneDecimal));

            DecimalFormat ratingFormat = new DecimalFormat("#.0");
            BigDecimal ratingWithTwoNum = rating.divide(new BigDecimal(2)).multiply(new BigDecimal(10));
            String starSuffix = ratingFormat.format(ratingWithTwoNum);
            String starRatingName = starSuffix;
            dto.setStarRatingName(starRatingName);
        } else {
            // XXX 修改硬代码
            dto.setStarRatingName("00");
        }
    }
    
    private void setRatingPercentageList(List<Map<String, Object>> getScoreAndRatingCountGroupByScoreList, BookItemsDTO dto) {
        Integer totalRatingCount = Integer.valueOf(dto.getRatingCount());
        if(totalRatingCount != 0){
            List<String> ratingPercentageList = new ArrayList<>();
            // 初始化 list
            for(int i = 0;i < 5; i++){
                String percentageStr = "0.0%";
                ratingPercentageList.add(percentageStr);
            }
            for(Map<String, Object> scoreAndRatingCountGroupByScoreMap :getScoreAndRatingCountGroupByScoreList){
                BigDecimal ratingCountBigDecimal = new BigDecimal((Long)scoreAndRatingCountGroupByScoreMap.get("ratingCount"));
                BigDecimal totalRatingCountBigDecimal = new BigDecimal(totalRatingCount);
                DecimalFormat format = new DecimalFormat("#.0%");
                String singleRatingCount = format.format(ratingCountBigDecimal.divide(totalRatingCountBigDecimal).setScale(3,RoundingMode.HALF_UP));
                Integer score = (Integer)scoreAndRatingCountGroupByScoreMap.get("score");
                /*
                * 从数据库取出的 score 可分别为2，4，6，8，10，
                * List 数组长度为5，index 为 0,1,2,3,4, 要得到1-5星，分别除于2，即 score / 2 - 1,
                * 4 - (score / 2 - 1)  对list倒序进行添加元素
                * */
                ratingPercentageList.set(4 - (score / 2 - 1),singleRatingCount);
            }
            dto.setRatingPercentageList(ratingPercentageList);
        }
        
    }
    
    public BookItemsDTO getBookPage(Integer id) {
        return this.getBookItemsById(id);
    }
}
