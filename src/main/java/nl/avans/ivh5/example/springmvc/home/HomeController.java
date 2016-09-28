package nl.avans.ivh5.example.springmvc.home;

import nl.avans.ivh5.example.springmvc.book.Book;
import nl.avans.ivh5.example.springmvc.book.BookRepository;
import nl.avans.ivh5.example.springmvc.copy.CopyRepository;
import nl.avans.ivh5.example.springmvc.loan.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
class HomeController {

    private final Logger logger = LoggerFactory.getLogger(HomeController.class);;

    private CopyRepository copyRepository;
    private BookRepository bookRepository;
    private LoanRepository loanRepository;

    @Autowired
    public HomeController(CopyRepository copyRepository, BookRepository bookRepository, LoanRepository loanRepository) {
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    /**
     * Geef een overzicht van alle boeken die te leen zijn. Ieder Book heeft één of meer copies.
     * We willen de member cover tonen, met de titel en de auteur, en informatie over leningen.
     *
     * @param model
     * @return
     */
    @RequestMapping("/")
    String index(Model model) {

        logger.debug("index");

        List<Book> books = new ArrayList<>();
        try {
            books = bookRepository.findAll();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw ex;
        }
        model.addAttribute("books", books);

        // Stuur de tijd mee naar de view - niet omdat het moet, ...
        model.addAttribute("now", LocalDateTime.now());
        // Zet een 'flag' om in Bootstrap header nav het actieve menu item te vinden.
        model.addAttribute("classActiveHome","active");
        return "views/home/index";
    }

    /**
     * Als de database een exception geeft vangen we die hier op. Kan bv. wanneer
     * de database server niet draait.
     *
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleError(HttpServletRequest req, Exception ex) {
         logger.error("Request: " + req.getRequestURL() + " raised " + ex);

        ModelAndView mav = new ModelAndView();
        mav.addObject("title", "Database error");
        mav.addObject("lead", "Geen verbinding met de database mogelijk");
        mav.addObject("message", "Het lukte niet om verbinding met de database te maken. Is de database server bereikbaar en de database beschikbaar?");
        mav.addObject("exception", ex);

        // Je kunt hier kiezen in welke view je een melding toont - op een
        // aparte pagina, of als alertbox op de huidige pagina.
        mav.setViewName("error/error");
        return mav;
    }

}
