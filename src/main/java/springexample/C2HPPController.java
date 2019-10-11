package springexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// used in the C2 Hosted Payment Page (HPP) example which shows uses a dedicated page to take the credit card details
@Controller
public class C2HPPController
{
    C2IntegrationService c2IntegrationService;
    C2Configuration c2Configuration;

    C2HPPController()
    {
        c2IntegrationService = new C2IntegrationService();
        c2Configuration = Application.c2Configuration;
    }

    // sample page for entering amount and currency and purchasing
    @RequestMapping(value = "/c2/hpp/c2-hpp-merchant-demo", method = RequestMethod.GET)
    public String c2HPPDemoPage(Model model)
    {
        return "/c2/hpp/c2-hpp-merchant-demo";
    }

    // post amounts here which will create an order and a payment-session and forward to the 3ds verification page
    @RequestMapping(value = "/c2/hpp/c2-hpp-3ds-challenge", method = RequestMethod.POST)
    public String c2HPP3dsChallenge(@RequestParam("amount") String amount, @RequestParam("currency-code") String currencyCode,
        Model model) throws com.fasterxml.jackson.core.JsonProcessingException
    {
        OrderResponse order = c2IntegrationService.createOrder(amount, currencyCode);
        PaymentSessionResponse response = c2IntegrationService.createPaymentSession(order);

        String hppurl =
            c2Configuration.getHpp() + "?sessionId=" + response.getId() + "&publicApiKey=" + c2Configuration.getPublicApiKey() +
                "&merchantAlias=" + c2Configuration.getMerchantAlias();

        return "redirect:" + hppurl;
    }

    // used for hpp integration to receive data back from 3ds verification
    @RequestMapping(value = "/c2/hpp/return", method = RequestMethod.GET)
    public String c2Return(@RequestParam("token") String token, @RequestParam("sessionId") String sessionId,
        @RequestParam("hostedCard") String hostedCard, Model model) throws Exception
    {
        final String hosteduri = c2Configuration.getApi() + "/hosted-cards/" + hostedCard;
        final String paymentsessionuri = c2Configuration.getApi() + "/payment-sessions/" + sessionId;

        HttpEntity request = new HttpEntity(c2IntegrationService.getHeader());
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PaymentSessionResponse>
            response = restTemplate.exchange(paymentsessionuri, HttpMethod.GET, request, PaymentSessionResponse.class);
        PaymentSessionResponse ps = response.getBody();

        HttpEntity request2 = new HttpEntity(c2IntegrationService.getHeader());
        RestTemplate restTemplate2 = new RestTemplate();

        ResponseEntity<OrderResponse> response2 =
            restTemplate.exchange(ps.getOrder(), HttpMethod.GET, request, OrderResponse.class);
        OrderResponse order = response2.getBody();

        // complete transaction
        final String sturi = c2Configuration.getApi() + "/transactions";
        URI turi = new URI(sturi);

        TransactionRequest tr = new TransactionRequest();
        Total total = new Total();
        total.setAmount(order.getTotal().getAmount());
        total.setCurrencyCode(order.getTotal().getCurrencyCode());
        tr.setTotal(total);
        tr.setHostedCard(hosteduri);
        tr.setPaymentSession(paymentsessionuri);
        tr.setDoCapture("false");

        HttpEntity transrequest = new HttpEntity(tr, c2IntegrationService.getHeader());
        RestTemplate restTemplate3 = new RestTemplate();
        ResponseEntity<String> transresult = restTemplate2.postForEntity(turi, transrequest, String.class);

        return "redirect:/c2/done?response=" +
            URLEncoder.encode(transresult.getBody(), StandardCharsets.UTF_8.toString()); //, model);
    }

    // called in hpp integration when user cancels out of credit card page
    @RequestMapping(value = "/c2/hpp/cancel", method = RequestMethod.GET)
    public String c2Cancel(@RequestParam("sessionId") String sessionId, Model model) throws Exception
    {
        return "/c2/hpp/c2-hpp-cancel";
    }
}