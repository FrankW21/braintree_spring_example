package springexample;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springexample.c2model.*;

import java.util.Base64;

@Controller
public class C2Controller
{
    String site = "http://localhost:8080";

    public HttpHeaders getHeader()
    {
        // create auth credentials
        String authStr = "P7pbm2VCXCHCCj6BPTFpRmdC:sk_rQPyCTDmXBH9wKV7x9MpMP2f";
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        return headers;
    }

    public PaymentSessionResponse createPaymentSession()
    {
        final String uri = "https://uat.api.converge.eu.elavonaws.com/payment-sessions";

        PaymentSessionRequest pr = new PaymentSessionRequest();
        pr.setOrder("https://uat.api.converge.eu.elavonaws.com/orders/4w6fy3t8k7tk7pw8qtg7vyr9yw3r");
        pr.setReturnUrl(site + "/c2/return");
        pr.setCancelUrl(site + "/c2/cancel");
        pr.setOriginUrl(site);

        // create request
        HttpEntity request = new HttpEntity(pr, getHeader());

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
        // query hosted card to get acs
        final String uri = "https://uat.api.converge.eu.elavonaws.com/hosted-cards/" + convergePaymentToken;

        // create request
        HttpEntity request = new HttpEntity(getHeader());

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<HostedCardResponse> response = restTemplate.exchange(uri, HttpMethod.GET, request, HostedCardResponse.class);
        HostedCardResponse card = response.getBody();

        model.addAttribute("termurl", site + "/c2/term");
        if (card.getThreeDSecureV1() != null)
        {
            model.addAttribute("pareq", card.getThreeDSecureV1().getPayerAuthenticationRequest());
            model.addAttribute("md", convergePaymentToken);
        }

        return "/c2/acsform";
    }

    @RequestMapping(value = "/c2/term", method = RequestMethod.POST)
    public String postForm(@RequestParam("PaRes") String paRes, @RequestParam("MD") String md,  Model model)
    {
        // update hostedCard
        final String uri = "https://uat.api.converge.eu.elavonaws.com/hosted-cards/" + md;

        HostedCardRequest cardrequest = new HostedCardRequest();
        springexample.c2modelhack.TreeDSecureV1 tds = new springexample.c2modelhack.TreeDSecureV1();
        tds.setPayerAuthenticationResponse(paRes);
        cardrequest.setThreeDSecureV1(tds);

        // create request
        HttpEntity request = new HttpEntity(cardrequest, getHeader());

        RestTemplate restTemplate = new RestTemplate();
        HostedCardResponse result = restTemplate.postForObject( uri, request, HostedCardResponse.class);

        // complete transaction
        return "redirect:/c2/done";
    }


    @RequestMapping(value = "/c2/done", method = RequestMethod.GET)
    public String done(Model model)
    {
        return "c2/done";
    }

}
