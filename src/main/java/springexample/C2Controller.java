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
    String site = "http://localhost:8080";
    //String apisite = "https://uat.api.converge.eu.elavonaws.com/";
    //String hppsrc = "https://uat.hpp.converge.eu.elavonaws.com/client/index.js";

    String apisite = "https://dev.api.converge.eu.elavonaws.com/";
    String hppsrc = "https://dev.hpp.converge.eu.elavonaws.com/client/index.js";

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

    public OrderResponse createOrder(String amount, String currencyCode)
    {
        final String uri = apisite + "orders";

        OrderRequest order = new OrderRequest();
        Total total = new Total();
        total.setAmount(amount);
        total.setCurrencyCode(currencyCode);
        order.setTotal(total);
        HttpEntity request = new HttpEntity(order, getHeader());

        RestTemplate restTemplate = new RestTemplate();
        OrderResponse orderResponse = restTemplate.postForObject( uri, request, OrderResponse.class);
        return orderResponse;
    }


    public PaymentSessionResponse createPaymentSession(OrderResponse order)
    {
        final String uri = apisite + "payment-sessions";

        PaymentSessionRequest pr = new PaymentSessionRequest();
        pr.setOrder( apisite + "orders/" + order.getId());
        pr.setReturnUrl(site + "/c2/return");
        pr.setCancelUrl(site + "/c2/cancel");
        pr.setOriginUrl(site);

        // create request
        HttpEntity request = new HttpEntity(pr, getHeader());

        RestTemplate restTemplate = new RestTemplate();
        PaymentSessionResponse result = restTemplate.postForObject( uri, request, PaymentSessionResponse.class);
        return result;
    }

    @RequestMapping(value = "/c2-3dsv1", method = RequestMethod.GET)
    public String c2v1(Model model)
    {
        OrderResponse order = createOrder("4", "EUR");
        PaymentSessionResponse response = createPaymentSession(order);
        model.addAttribute("src", hppsrc);
        model.addAttribute("id", response.getId());
        return "/c2/c2-3dsv1";
    }

    @RequestMapping(value = "/c2/c2-3dsv2-order", method = RequestMethod.GET)
    public String c2v2(Model model)
    {
        return "/c2/c2-3dsv2-order";
    }


    @RequestMapping(value = "/c2/c2-3dsv2", method = RequestMethod.POST)
    public String c2v2(@RequestParam("amount") String amount, @RequestParam("currency-code") String currencyCode, Model model) throws com.fasterxml.jackson.core.JsonProcessingException
    {
        OrderResponse order = createOrder(amount, currencyCode);
        PaymentSessionResponse response = createPaymentSession(order);
        model.addAttribute("src", hppsrc);
        model.addAttribute("id", response.getId());

        MerchantData merchantData = new MerchantData();
        merchantData.setAmount(amount);
        merchantData.setCurrencyCode(currencyCode);
        merchantData.setPaymentSessionId(response.getId());

        ObjectMapper objectMapper = new ObjectMapper();
        String md = objectMapper.writeValueAsString(merchantData);

        model.addAttribute("md", md);
        return "/c2/c2-3dsv2";
    }


    @RequestMapping(value = "/c2/lightboxV1", method = RequestMethod.POST)
    public String postlightbox1(@RequestParam("convergePaymentToken") String convergePaymentToken, Model model, final RedirectAttributes redirectAttributes)
    {
        // query hosted card to get acs
        final String uri = apisite + "hosted-cards/" + convergePaymentToken;

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

    @RequestMapping(value = "/c2/lightboxV2", method = RequestMethod.POST)
    public String postlightboxv2(@RequestParam("convergePaymentToken") String convergePaymentToken, @RequestParam("MD") String md, Model model, final RedirectAttributes redirectAttributes) throws Exception //, java.io.UnsupportedEncodingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        MerchantData merchantData = objectMapper.readValue(md, MerchantData.class);

        final String hosteduri = apisite + "hosted-cards/" + convergePaymentToken;
        final String paymenturi = apisite + "payment-sessions/" + merchantData.getPaymentSessionId();

        // complete transaction
        final String sturi = apisite + "transactions";
        URI turi = new URI(sturi);

        TransactionRequest tr = new TransactionRequest();
        Total total = new Total();
        total.setAmount(merchantData.getAmount());
        total.setCurrencyCode(merchantData.getCurrencyCode());
        tr.setTotal(total);
        tr.setHostedCard(hosteduri);
        tr.setPaymentSession(paymenturi);
        tr.setDoCapture("false");

        HttpEntity transrequest = new HttpEntity(tr, getHeader());
        RestTemplate restTemplate2 = new RestTemplate();
        ResponseEntity<String> transresult = restTemplate2.postForEntity( turi, transrequest, String.class);

        return "redirect:/c2/done?response=" + URLEncoder.encode(transresult.getBody(), StandardCharsets.UTF_8.toString()); //, model);
    }


    @RequestMapping(value = "/c2/term", method = RequestMethod.POST)
    public String postForm(@RequestParam("PaRes") String paRes, @RequestParam("MD") String md,  Model model) throws URISyntaxException, java.io.UnsupportedEncodingException
    {
        // updated pares in hosted card
        final String uri = apisite + "hosted-cards/" + md;

        HostedCardRequest cardrequest = new HostedCardRequest();
        TreeDSecureV1Response tds = new TreeDSecureV1Response();
        tds.setPayerAuthenticationResponse(paRes);
        cardrequest.setThreeDSecureV1(tds);

        HttpEntity request = new HttpEntity(cardrequest, getHeader());

        RestTemplate restTemplate = new RestTemplate();
        HostedCardResponse result = restTemplate.postForObject( uri, request, HostedCardResponse.class);

        // complete transaction
        final String sturi = apisite + "transactions";
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
    public String done(@RequestParam("response") String responseraw, Model model)
    {
        String response = Pattern.compile("\\n").matcher(responseraw).replaceAll("<br/>");
        response = Pattern.compile(" ").matcher(response).replaceAll("&nbsp;");
        model.addAttribute("response", response);
        return "c2/done";
    }

}
