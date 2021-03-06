package pl.coderslab.controller;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.coderslab.dao.BookDao;
import pl.coderslab.dao.PublisherDao;
import pl.coderslab.entity.Book;
import pl.coderslab.entity.Publisher;

@Controller
@RequestMapping("/books")
public class BookController {

  private final BookDao bookDao;
  private final PublisherDao publisherDao;

  public BookController(BookDao bookDao, PublisherDao publisherDao) {
    this.bookDao = bookDao;
    this.publisherDao = publisherDao;
  }

  @GetMapping
  @ResponseBody
  public String findAll() {
    List<Book> all = bookDao.findAll();

    return all.stream()
        .map(Book::toString)
        .collect(Collectors.joining("\n"));
  }

  @GetMapping(path = "/rating", produces = "text/plain;charset=UTF-8")
  @ResponseBody
  public String findByRating(@RequestParam int rating){
    List<Book> books = bookDao.findByRatingGreaterThen(rating);

    return books.stream()
        .map(Book::toString)
        .collect(Collectors.joining("<br>"));
  }

  @GetMapping("/{id}")
  @ResponseBody
  public String getBook(@PathVariable long id) {
    return bookDao.findById(id).toString();
  }

  @GetMapping("/optional/{id}")
  @ResponseBody
  public String getOptionalBook(@PathVariable long id) {
    return bookDao.optionalFindById(id).orElseThrow(RuntimeException::new).toString();
  }

  @PostMapping
  @ResponseBody
  public String createBook() {
    Book book = new Book();
    book.setDescription("Super ksiazka");
    book.setRating(1);
    book.setTitle("Jak żyć");
    return bookDao.create(book).toString();
  }

  @PutMapping("/{id}")
  @ResponseBody
  public String updateBook(@PathVariable long id, @RequestParam String title) {
    Book book = bookDao.optionalFindById(id).orElseThrow(() -> new RuntimeException("Co ty chcesz edytowac ?"));
    book.setTitle(title);

    return bookDao.update(book).toString();
  }

  @DeleteMapping("/{id}")
  @ResponseBody
  public void deleteBook(@PathVariable long id) {
    Optional<Book> maybeBook = bookDao.optionalFindById(id);

    maybeBook.ifPresent(bookDao::remove);
  }


  @PostMapping("/with-publisher")
  @ResponseBody
  public String createWithPublisher() {
    Book book = new Book();
    book.setTitle("Ksiazka");
    book.setDescription("Ksiazka od publishera");

    Publisher publisher = new Publisher();
    publisher.setName("Super wydawca");

    book.setPublisher(publisher);

    return bookDao.create(book).toString();

  }


  @PostMapping("/with-publisher2")
  @ResponseBody
  public String createWithPublisher2() {
    Book book = new Book();
    book.setTitle("Ksiazka");
    book.setDescription("Ksiazka od publishera");

    book.setPublisher(publisherDao.findById(1));

    Book book1 = bookDao.create(book);
    return book1.toString();

  }


}
