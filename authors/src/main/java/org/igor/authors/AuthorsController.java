package org.igor.authors;

import io.micronaut.http.annotation.Controller;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.validation.Valid;
import org.igor.authors.api.AuthorsApi;
import org.igor.authors.model.Author;
import org.igor.authors.model.Country;

import java.util.List;

//@Controller
public class AuthorsController
//        implements AuthorsApi
{

//    private final org.igor.authors.client.api.AuthorsApi client;
//
//    public AuthorsController(org.igor.authors.client.api.AuthorsApi client) {
//        this.client = client;
//    }
//
//    @ExecuteOn(TaskExecutors.BLOCKING)
//    @Override
//    public List<@Valid Author> search(String firstName, String secondName, String county, Integer countOfBooks) {
//        List<org.igor.authors.client.model.Author> result = client.search(firstName, secondName, county, countOfBooks);
//        return result
//                .stream()
//                .map(author -> {
//                    Author a = new Author(author.getFirstName(), author.getSecondName(), author.getDateOfBirth(), Country.fromValue(author.getCountry().getValue()));
//                    a.setBiography(author.getBiography());
//                    a.setCountOfBooks(author.getCountOfBooks());
//                    return a;
//                })
//                .toList();
//    }
}
