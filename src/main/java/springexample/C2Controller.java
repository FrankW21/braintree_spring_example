package springexample;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class C2Controller
{
    @RequestMapping(value = "/c2/lightbox", method = RequestMethod.POST)
    public String postForm(@RequestParam("convergePaymentToken") String convergePaymentToken, Model model, final RedirectAttributes redirectAttributes)
    {
        return "redirect:/c2/done";
    }

    @RequestMapping(value = "/c2/done", method = RequestMethod.GET)
    public String done(Model model)
    {
        return "c2/done";
    }

}
