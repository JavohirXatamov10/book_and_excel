package org.example.book_and_excel.controller;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.book_and_excel.entitiy.Author;
import org.example.book_and_excel.repo.AuthorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
@Controller
@RequestMapping("author")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorRepository authorRepository;
    @GetMapping
    public String sentToAuthPage(Model model){
        List<Author> authors=authorRepository.findAll();
        authors.sort(Comparator.comparing(Author::getId));
        model.addAttribute("authors", authors);
        return "author";
    }
    @GetMapping("/add")
    public String sendToAddAuthorPage(){
        return "addAuthor";
    }
    @PostMapping("/add")
    public String addAuthor(String firstName,String lastName){
        Author author=Author.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
        authorRepository.save(author);
        return "redirect:/author";
    }
    @GetMapping("/edit/{id}")
    public String sentToEditPage(@PathVariable Integer id, Model model){
        Author author=authorRepository.findById(id).get();
        model.addAttribute("author",author);
        return "editAuth";
    }
    @PostMapping("/edit/{id}")
    public String editAuth(@PathVariable Integer id,@ModelAttribute Author author){
        Author foundAuthor = authorRepository.findById(id).get();
        foundAuthor.setId(author.getId());
        foundAuthor.setFirstName(author.getFirstName());
        foundAuthor.setLastName(author.getLastName());
        authorRepository.save(foundAuthor);
        return "redirect:/author";
    }
    @PostMapping("delete/{id}")
    public String deleteAuthor(@PathVariable Integer id){
        authorRepository.deleteById(id);
        return "redirect:/author";
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAuthor() throws IOException {
        List<Author> authors=authorRepository.findAll(Sort.by(Sort.Order.asc("id") ));
        Workbook workbook=new XSSFWorkbook();
        Sheet sheet =workbook.createSheet("Authors");
        Row headerRow=sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Id");
        headerRow.createCell(1).setCellValue("Firstname");
        headerRow.createCell(2).setCellValue("Last name");
         int numRow=1;
        for (Author author : authors) {
            Row row= sheet.createRow(numRow++);
            row.createCell(0).setCellValue(author.getId());
            row.createCell(1).setCellValue(author.getFirstName());
            row.createCell(2).setCellValue(author.getLastName());
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelData = bos.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=author.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}

