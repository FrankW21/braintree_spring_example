package springexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springexample.c2model.*;
import springexample.c2model.TreeDSecureV1Response;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

@Controller
public class C2Controller
{
    @RequestMapping(value = "/c2/done", method = RequestMethod.GET)
    public String done(@RequestParam("response") String responseraw, Model model)
    {
        String response = Pattern.compile("\\n").matcher(responseraw).replaceAll("<br/>");
        response = Pattern.compile(" ").matcher(response).replaceAll("&nbsp;");
        model.addAttribute("response", response);
        return "c2/done";
    }

}
