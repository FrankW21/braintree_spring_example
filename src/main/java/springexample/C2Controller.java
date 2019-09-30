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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

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

    public OrderResponse createOrder()
    {
        final String uri = "https://uat.api.converge.eu.elavonaws.com/orders";

        OrderRequest order = new OrderRequest();
        Total total = new Total();
        total.setAmount("3.23");
        total.setCurrencyCode("EUR");
        order.setTotal(total);
        HttpEntity request = new HttpEntity(order, getHeader());

        RestTemplate restTemplate = new RestTemplate();
        OrderResponse orderResponse = restTemplate.postForObject( uri, request, OrderResponse.class);
        return orderResponse;
    }


    public PaymentSessionResponse createPaymentSession(OrderResponse order)
    {
        final String uri = "https://uat.api.converge.eu.elavonaws.com/payment-sessions";

        PaymentSessionRequest pr = new PaymentSessionRequest();
        pr.setOrder("https://uat.api.converge.eu.elavonaws.com/orders/" + order.getId());
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
        OrderResponse order = createOrder();
        PaymentSessionResponse response = createPaymentSession(order);
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
    public String postForm(@RequestParam("PaRes") String paRes, @RequestParam("MD") String md,  Model model) throws URISyntaxException, java.io.UnsupportedEncodingException
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
        final String sturi = "https://uat.api.converge.eu.elavonaws.com/transactions";
        URI turi = new URI(sturi);

        TransactionRequest tr = new TransactionRequest();
        Total total = new Total();
        total.setAmount("3.23");
        total.setCurrencyCode("EUR");
        tr.setTotal(total);
        tr.setHostedCard(uri);
        tr.setDoCapture("false");

        HttpEntity transrequest = new HttpEntity(tr, getHeader());
        RestTemplate restTemplate2 = new RestTemplate();
        ResponseEntity<String> transresult = restTemplate2.postForEntity( turi, transrequest, String.class);

        //model.addAttribute("response", transresult);
        return "redirect:/c2/done?response=" + URLEncoder.encode(transresult.getBody(), StandardCharsets.UTF_8.toString()); //, model);
    }


    @RequestMapping(value = "/c2/done", method = RequestMethod.GET)
    public String done(@RequestParam("response") String responseraw, Model model) throws com.fasterxml.jackson.core.JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        //String response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseraw);
        String response = Pattern.compile("\\n").matcher(responseraw).replaceAll("<br/>");
        model.addAttribute("response", response);
        return "c2/done";
    }

}
