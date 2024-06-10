package org.example.book_and_excel.component;

import lombok.RequiredArgsConstructor;
import org.example.book_and_excel.entitiy.Author;
import org.example.book_and_excel.entitiy.Book;
import org.example.book_and_excel.repo.AuthorRepository;
import org.example.book_and_excel.repo.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Runnuble implements CommandLineRunner {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    @Override
    public void run(String... args) throws Exception {

        //getIndfo();
    }

    private void getIndfo() {
        Author author1= Author.builder()
                .firstName("Erkin")
                .lastName("Vohidov")
                .build();
        Author author2= Author.builder()
                .firstName("Muhammad")
                .lastName("Yusuf")
                .build();
        authorRepository.save(author1);
        authorRepository.save(author2);
        Book book1= Book.builder()
                .name("Qo`shiqlarim sizga")
                .price(50_000)
                .author(author1)
                .build();
        Book book2= Book.builder()
                .name("Yurak va aql")
                .price(80_000)
                .author(author1)
                .build();
        Book book3= Book.builder()
                .name("Nido")
                .price(70_000)
                .author(author1)
                .build();
        Book book4= Book.builder()
                .name("Sirli qotillik")
                .price(100_000)
                .author(author2)
                .build();
        Book book5= Book.builder()
                .name("O`g`irlangan bolalik")
                .price(100_000)
                .author(author2)
                .build();
        Book book6= Book.builder()
                .name("Marhumlar ororli")
                .price(100_000)
                .author(author2)
                .build();
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
        bookRepository.save(book6);
    }
}
