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

    @RequestMapping(value = "/braintreeform", method = RequestMethod.GET)
    public String braintreeform(Model model) {
        return "braintreeform";
    }

    @RequestMapping(value = "/braintreejs", method = RequestMethod.GET)
    public String braintreejs(Model model) {
        return "braintreejs";
    }

}
