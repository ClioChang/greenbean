package com.clio.greenbean.spring.controller;

import com.clio.greenbean.domain.Author;
import com.clio.greenbean.dto.BookDTO;
import com.clio.greenbean.dto.SearchPageDTO;
import com.clio.greenbean.spring.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * created by 吾乃逆世之神也 on 2019/12/29
 */
@Controller
public class BookController {
    private BookService bookService;
    
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    @GetMapping("/search")
    public String search(String searchKeyWord, Integer offset, Model model){
        if(offset == null) {
            offset = 0;
        }
        SearchPageDTO searchPageDTO = bookService.getSearchPage(searchKeyWord, offset);
        model.addAttribute("searchBooks",searchPageDTO.getBookItemsList());
        model.addAttribute("pagination",searchPageDTO.getPaginationVo());
        return "search";
    }
    
    @RequestMapping(value="/addBook")
    public String addBookPage(){
        return "addBook";
    }
    
    @PostMapping(value="/book")
    public String addBook(BookDTO bookDTO){
        this.bookService.saveBook(bookDTO);
        return "addBookSuccess";
    }
    
    @GetMapping(value="/getAuthorSuggestion")
    @ResponseBody
    public List<Author> getAuthorSuggestion(String keyword){
        List<Author> authorSuggestion = this.bookService.getAuthorSuggestion(keyword);
        return authorSuggestion;
    }
}

