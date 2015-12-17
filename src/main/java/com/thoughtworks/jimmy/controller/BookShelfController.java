package com.thoughtworks.jimmy.controller;

import com.thoughtworks.jimmy.model.Book;
import com.thoughtworks.jimmy.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
//@Controller
public class BookShelfController {

    @Autowired
    private BookService bookService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<Iterable<Book>> listAllBooks() {

        Iterable<Book> books = bookService.findAll();
        return new ResponseEntity<Iterable<Book>>(books, HttpStatus.OK);

    }

    @RequestMapping(value = "/{isbn}", method = RequestMethod.GET)
    public ResponseEntity<Book> showBook(@PathVariable String isbn) {

        Book book = bookService.findByIsbn(isbn);
        if (book == null) {
            return new ResponseEntity<Book>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Book>(book, HttpStatus.OK);

    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Void> createNewBook(@RequestBody Book book, UriComponentsBuilder ucBuilder) {

        if (bookService.equals(book)) {
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }

        bookService.create(book);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/{isbn}").buildAndExpand(book.getIsbn()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);

    }

    @RequestMapping(value = "/{isbn}", method = RequestMethod.PUT)
    public ResponseEntity<Book> editBook(@PathVariable String isbn, @RequestBody Book book) {

        Book currentBook = bookService.findByIsbn(isbn);

        if (currentBook==null) {
            return new ResponseEntity<Book>(HttpStatus.NOT_FOUND);
        }

        currentBook.setIsbn(book.getIsbn());
        currentBook.setName(book.getName());
        currentBook.setAuthor(book.getAuthor());
        currentBook.setPrice(book.getPrice());

        bookService.edit(currentBook);
        return new ResponseEntity<Book>(currentBook, HttpStatus.OK);

    }

    @RequestMapping(value = "/{isbn}", method = RequestMethod.DELETE)
    public ResponseEntity<Book> deleteBook(@PathVariable String isbn) {

        Book book = bookService.findByIsbn(isbn);
        if (book == null) {
            return new ResponseEntity<Book>(HttpStatus.NOT_FOUND);
        }

        bookService.delete(isbn);
        return new ResponseEntity<Book>(HttpStatus.NO_CONTENT);
    }

}
