package org.example.book_and_excel.controller;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.book_and_excel.entitiy.Author;
import org.example.book_and_excel.entitiy.Book;
import org.example.book_and_excel.repo.AuthorRepository;
import org.example.book_and_excel.repo.BookRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final BookRepository repository;
    private final AuthorRepository authorRepository;
    @GetMapping
    public String sentToBookPage(Model model){
        List<Book> books=repository.findAll();
        books.sort(Comparator.comparing(item->item.getId()));
        model.addAttribute("books",books);
        return "book";
    }
    @GetMapping("/add")
    public String sentToAddBook(Model model){
        List<Author> authors = authorRepository.findAll();
        model.addAttribute("authors", authors);
        return "addBook";
        }
    @PostMapping("/add")
    public String addBook(String name, Integer price,Integer authorId){
        Book book= Book.builder()
                .name(name)
                .price(price)
                .author(authorRepository.findById(authorId).get())
                .build();
        repository.save(book);
        return "redirect:/book";
        }
    @GetMapping("/edit/{id}")
    public String sentToEditPage(@PathVariable Integer id, Model model){
        Book book=repository.findById(id).get();
        List<Author> authors=authorRepository.findAll();
        model.addAttribute("book",book);
        model.addAttribute("authors", authors);
        return "editBook";
    }
    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable Integer id, String bookName, Integer price, Integer authorId){
        Book foundBook=repository.findById(id).get();
        foundBook.setName(bookName);
        foundBook.setPrice(price);
        Author author = authorRepository.findById(authorId).get();
        foundBook.setAuthor(author);
        repository.save(foundBook);
        return "redirect:/book";
    }
    @PostMapping("delete/{id}")
    public String deleteAuthor(@PathVariable Integer id){
        repository.deleteById(id);
        return "redirect:/book";
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportBooks() throws IOException {
        List<Book> books = repository.findAll();
        books.sort(Comparator.comparing(Book::getId));
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Books");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Id");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Price");
        headerRow.createCell(3).setCellValue("Author");
        headerRow.createCell(4).setCellValue("Author");
        int rowNum = 1;
        for (Book book : books) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(book.getId());
            row.createCell(1).setCellValue(book.getName());
            row.createCell(2).setCellValue(book.getPrice());
            row.createCell(3).setCellValue(book.getAuthor().getFirstName());
            row.createCell(4).setCellValue(book.getAuthor().getLastName());
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelData = bos.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
    @PostMapping("/import")
    public ResponseEntity<String> importBooks(@RequestParam("file") MultipartFile file) {
        try {
            List<Book> books = parseExcelFile(file.getInputStream());
            repository.saveAll(books);
            return ResponseEntity.ok("Books imported successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import books");
        }
    }
    @GetMapping("/importPage")
    public String sentImportPage(){
        return "importPage";
    }
    private List<Book> parseExcelFile(InputStream is) throws IOException {
        List<Book> books = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {  // Skip header row
                continue;
            }
            Book book = new Book();
            book.setId((int) row.getCell(0).getNumericCellValue());
            book.setName(row.getCell(1).getStringCellValue());
            book.setPrice((int) row.getCell(2).getNumericCellValue());
            String authorFirstName = row.getCell(3).getStringCellValue();
            String authorLastName = row.getCell(4).getStringCellValue();
            Author author =findOrCreateAuthor(authorFirstName,authorLastName);
            book.setAuthor(author);
            books.add(book);
        }
        workbook.close();
        return books;
    }
    private Author findOrCreateAuthor(String firstName, String LastName) {
        Optional<Author> existingAuthor = authorRepository.findByFirstNameAndLastName(firstName,LastName);
        if (existingAuthor.isPresent()) {
            return existingAuthor.get();
        } else {
            Author newAuthor = new Author();
            newAuthor.setFirstName(firstName);
            newAuthor.setLastName(LastName);
            return authorRepository.save(newAuthor);
        }
    }
}




