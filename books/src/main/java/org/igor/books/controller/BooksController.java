/*
 * Copyright 2017-2025 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Library
 * This is a library API
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.igor.books.controller;

import org.igor.books.BookEntity;
import org.igor.books.BookRepository;
import org.igor.books.api.BooksApi;
import org.igor.books.model.BookInfo;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.annotation.Controller;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import java.util.List;
//end::import[]

//tag::clazz[]
@Controller
public class BooksController implements BooksApi {
//end::clazz[]

    //tag::inject[]
    private final BookRepository bookRepository; // <1>

    public BooksController(BookRepository bookRepository) { // <1>
        this.bookRepository = bookRepository;
    }
    //end::inject[]

    //tag::addBook[]
    @ExecuteOn(TaskExecutors.BLOCKING)
    public void addBook(BookInfo bookInfo) {
        bookRepository.save(bookInfo.getName(), // <3>
                bookInfo.getAvailability(),
                bookInfo.getAuthor(),
                bookInfo.getIsbn());
    }
    //end::addBook[]


    //tag::search[]
    @ExecuteOn(TaskExecutors.BLOCKING) // <1>
    public List<BookInfo> search(
            String bookName,
            String authorName) {
        return searchEntities(bookName, authorName)
                .stream()
                .map(this::map) // <5>
                .toList();
    }

    private BookInfo map(BookEntity entity) {
        var book = new BookInfo(entity.name(), entity.availability());
        book.setIsbn(entity.isbn());
        book.setAuthor(entity.author());
        return book;
    }

    @NonNull
    private List<BookEntity> searchEntities(String name, String author) { // <2>
        if (StringUtils.isEmpty(name) && StringUtils.isEmpty(author)) {
            return bookRepository.findAll();
        } else if (StringUtils.isEmpty(name)) {
            return bookRepository.findAllByAuthorContains(author); // <3>

        } else  if (StringUtils.isEmpty(author)) {
            return bookRepository.findAllByNameContains(name);
        } else {
            return bookRepository.findAllByAuthorContainsAndNameContains(author,name); // <4>
        }
    }
    //end::search[]
//tag::footer[]
}
//end::footer[]
