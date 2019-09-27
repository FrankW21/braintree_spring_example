package springexample;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springexample.c2model.PaymentSessionRequest;
import springexample.c2model.PaymentSessionResponse;

import java.util.Base64;

@Controller
public class C2Controller
{
/*
    @Bean
    RestOperations rest(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.basicAuthorization("P7pbm2VCXCHCCj6BPTFpRmdC", "sk_rQPyCTDmXBH9wKV7x9MpMP2f").build();
    }
*/

    public PaymentSessionResponse createPaymentSession()
    {
        final String uri = "https://uat.api.converge.eu.elavonaws.com/payment-sessions";
        String site = "http://localhost:8080";

        PaymentSessionRequest pr = new PaymentSessionRequest();
        pr.setOrder("https://uat.api.converge.eu.elavonaws.com/orders/4w6fy3t8k7tk7pw8qtg7vyr9yw3r");
        pr.setReturnUrl(site + "/c2/return");
        pr.setCancelUrl(site + "/c2/cancel");
        pr.setOriginUrl(site);

        // create auth credentials
        String authStr = "P7pbm2VCXCHCCj6BPTFpRmdC:sk_rQPyCTDmXBH9wKV7x9MpMP2f";
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        // create request
        HttpEntity request = new HttpEntity(pr, headers);

        RestTemplate restTemplate = new RestTemplate();
        PaymentSessionResponse result = restTemplate.postForObject( uri, request, PaymentSessionResponse.class);
        return result;
    }

    @RequestMapping(value = "/c2", method = RequestMethod.GET)
    public String c2(Model model)
    {
        PaymentSessionResponse response = createPaymentSession();
        model.addAttribute("id", response.getId());
        return "c2";
    }

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
