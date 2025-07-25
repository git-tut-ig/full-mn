package org.igor.integration.books;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.igor.integration.SingletonCompose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BooksTest {

    private static final String BOOKS_BASE_URL = System.getenv("BOOKS_BASE_URL");

    @BeforeAll
    public static void setup() {
        SingletonCompose.start();
        // Добавление тестовых данных
        addTestBooks();
        //addTestAuthors();
    }

    private static void addTestBooks() {
        // Добавляем книги по одной
        addSingleBook(
                "Harry Potter and the Philosopher's Stone",
                "available",
                "J.K. Rowling",
                "9780747532743"
        );

        addSingleBook(
                "The Hobbit",
                "available",
                "J.R.R. Tolkien",
                "9780261102217"
        );

        addSingleBook(
                "1984",
                "not available",
                "George Orwell",
                "9780451524935"
        );
    }

    private static void addSingleBook(String name, String availability, String author, String isbn) {
        String bookJson = String.format(
                "{ \"name\": \"%s\", " +
                        "\"availability\": \"%s\", " +
                        "\"author\": \"%s\", " +
                        "\"isbn\": \"%s\" }",
                name, availability, author, isbn
        );

        given()
                .baseUri(BOOKS_BASE_URL)
                .contentType(ContentType.JSON)
                .body(bookJson)
                .when()
                .post("/books/add")
                .then()
                .statusCode(200);
    }

//    private static void addTestAuthors() {
//        // Предполагаем, что /authors/add тоже принимает по одному автору
//        addSingleAuthor(
//                "J.K.", "Rowling", "1965-07-31",
//                "British author", 20, "USA"
//        );
//
//        addSingleAuthor(
//                "J.R.R.", "Tolkien", "1892-01-03",
//                "British writer", 30, "UK"
//        );
//
//        addSingleAuthor(
//                "George", "Orwell", "1903-06-25",
//                "English novelist", 15, "UK"
//        );
//    }
//
//    private static void addSingleAuthor(String firstName, String secondName, String dateOfBirth,
//                                        String biography, int countOfBooks, String country) {
//        String authorJson = String.format(
//                "{ \"firstName\": \"%s\", " +
//                        "\"secondName\": \"%s\", " +
//                        "\"dateOfBirth\": \"%s\", " +
//                        "\"biography\": \"%s\", " +
//                        "\"countOfBooks\": %d, " +
//                        "\"country\": \"%s\" }",
//                firstName, secondName, dateOfBirth, biography, countOfBooks, country
//        );
//
//        given()
//                    .contentType(ContentType.JSON)
//                    .body(authorJson)
//                .when()
//                    .post("/authors/add")
//                .then()
//                    .statusCode(200);
//    }


    @Test
    @Order(1)
    public void testSearchBookByNameSuccess() {
        given()
                .baseUri(BOOKS_BASE_URL)
                .queryParam("book-name", "Harry Potter")
                .when()
                .get("/books/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].name", containsString("Harry Potter"));
    }

    @Test
    @Order(2)
    public void testSearchBookWithShortName() {
        given()
                .baseUri(BOOKS_BASE_URL)
                .queryParam("book-name", "Ha")
                .when()
                .get("/books/search")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(3)
    public void testSearchBookByNameAndAuthor() {
        given()
                .baseUri(BOOKS_BASE_URL)
                .queryParam("book-name", "The Hobbit")
                .queryParam("author-name", "Tolkien")
                .when()
                .get("/books/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].name", equalTo("The Hobbit"))
                .body("[0].author", equalTo("J.R.R. Tolkien"));
    }

    @Test
    @Order(4)
    public void testAddBookSuccess() {
        String bookJson = "{ " +
                "\"name\": \"The Lord of the Rings\", " +
                "\"availability\": \"available\", " +
                "\"author\": \"J.R.R. Tolkien\", " +
                "\"isbn\": \"9780618640157\" " +
                "}";

        Response response =
                given()
                        .baseUri(BOOKS_BASE_URL)
                        .contentType(ContentType.JSON)
                        .body(bookJson)
                        .when()
                        .post("/books/add")
                        .then()
                        .statusCode(200)
                        .extract().response();

        // Проверка, что книга добавлена
        given()
                .baseUri(BOOKS_BASE_URL)
                .queryParam("book-name", "The Lord of the Rings")
                .when()
                .get("/books/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].name", equalTo("The Lord of the Rings"));
    }

    @Test
    @Order(5)
    public void testAddBookMissingRequiredField() {
        String bookJson = "{ " +
                "\"availability\": \"available\", " +
                "\"author\": \"J.R.R. Tolkien\" " +
                "}";

        given()
                .baseUri(BOOKS_BASE_URL)
                .contentType(ContentType.JSON)
                .body(bookJson)
                .when()
                .post("/books/add")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(6)
    public void testAddBookInvalidIsbn() {
        String bookJson = "{ " +
                "\"name\": \"The Hobbit\", " +
                "\"availability\": \"available\", " +
                "\"author\": \"J.R.R. Tolkien\", " +
                "\"isbn\": \"123\" " +
                "}";

        given()
                .baseUri(BOOKS_BASE_URL)
                .contentType(ContentType.JSON)
                .body(bookJson)
                .when()
                .post("/books/add")
                .then()
                .statusCode(400);
    }

    //@Test
    @Order(7)
    public void testSearchAuthorByNameAndCountry() {
        given()
                .baseUri(BOOKS_BASE_URL)
                .queryParam("firstName", "J.K.")
                .queryParam("country", "USA")
                .when()
                .get("/authors/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].firstName", equalTo("J.K."))
                .body("[0].country", equalTo("USA"));
    }

    //@Test
    @Order(8)
    public void testSearchAuthorInvalidCountry() {
        given()
                .baseUri(BOOKS_BASE_URL)
                .queryParam("country", "Moon")
                .when()
                .get("/authors/search")
                .then()
                .statusCode(400);
    }

    //@Test
    @Order(9)
    public void testSearchAuthorByBookCount() {
        given()
                .baseUri(BOOKS_BASE_URL)
                .queryParam("countOfBooks", "20")
                .when()
                .get("/authors/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].countOfBooks", equalTo(20));
    }
}