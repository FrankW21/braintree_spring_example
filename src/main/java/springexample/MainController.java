package springexample;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController
{
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root(Model model) {
        return "index";
    }

    @RequestMapping(value = "/braintree", method = RequestMethod.GET)
    public String braintree(Model model) {
        return "braintreeform";
    }

}
