package springexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
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

@Controller
public class C2LightboxController
{
    C2IntegrationService c2IntegrationService;
    C2Configuration c2Configuration;

    C2LightboxController()
    {
        c2IntegrationService = new C2IntegrationService();
        c2Configuration = Application.c2Configuration;
    }


    // sample page for entering amount and currency and purchasing
    @RequestMapping(value = "/c2/lightbox/c2-lightbox-merchant-demo", method = RequestMethod.GET)
    public String c2LightboxDemoPage(Model model)
    {
        return "/c2/lightbox/c2-lb-merchant-demo";
    }

    @RequestMapping(value = "/c2/lightbox/c2-lb-3ds-challenge", method = RequestMethod.POST)
    public String c2Lightbox3dsChallenge(@RequestParam("amount") String amount, @RequestParam("currency-code") String currencyCode, Model model) throws com.fasterxml.jackson.core.JsonProcessingException
    {
        OrderResponse order = c2IntegrationService.createOrder(amount, currencyCode);
        PaymentSessionResponse response = c2IntegrationService.createPaymentSession(order);
        model.addAttribute("src", c2Configuration.getHpp() + "/client/index.js");
        model.addAttribute("id", response.getId());

        MerchantData merchantData = new MerchantData();
        merchantData.setAmount(amount);
        merchantData.setCurrencyCode(currencyCode);
        merchantData.setPaymentSessionId(response.getId());

        ObjectMapper objectMapper = new ObjectMapper();
        String md = objectMapper.writeValueAsString(merchantData);

        model.addAttribute("md", md);

        return "/c2/lightbox/c2-lb-3ds-challenge";
    }

    // process return from 3ds verification.  create a transaction
    @RequestMapping(value = "/c2/lightbox/c2-lb-3ds-results", method = RequestMethod.POST)
    public String c2Post3dsResults(@RequestParam("convergePaymentToken") String convergePaymentToken, @RequestParam("MD") String md, Model model, final RedirectAttributes redirectAttributes) throws Exception //, java.io.UnsupportedEncodingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        MerchantData merchantData = objectMapper.readValue(md, MerchantData.class);

        final String hosteduri = c2Configuration.getApi() + "/hosted-cards/" + convergePaymentToken;
        final String paymenturi = c2Configuration.getApi() + "/payment-sessions/" + merchantData.getPaymentSessionId();

        // complete transaction
        final String sturi = c2Configuration.getApi() + "/transactions";
        URI turi = new URI(sturi);

        TransactionRequest tr = new TransactionRequest();
        Total total = new Total();
        total.setAmount(merchantData.getAmount());
        total.setCurrencyCode(merchantData.getCurrencyCode());
        tr.setTotal(total);
        tr.setHostedCard(hosteduri);
        tr.setPaymentSession(paymenturi);
        tr.setDoCapture("false");

        HttpEntity transrequest = new HttpEntity(tr, c2IntegrationService.getHeader());
        RestTemplate restTemplate2 = new RestTemplate();
        ResponseEntity<String> transresult = restTemplate2.postForEntity( turi, transrequest, String.class);

        return "redirect:/c2/done?response=" + URLEncoder.encode(transresult.getBody(), StandardCharsets.UTF_8.toString()); //, model);
    }
}
