package org.example.book_and_excel.repo;

import org.example.book_and_excel.entitiy.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}