package springexample;

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
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Controller
public class C2LegacyController
{
    C2IntegrationService c2IntegrationService;
    C2Configuration c2Configuration;

    C2LegacyController()
    {
        c2IntegrationService = new C2IntegrationService();
        c2Configuration = Application.c2Configuration;
    }

    @RequestMapping(value = "/c2/legacy/c2-lb-legacy-merchant-demo", method = RequestMethod.GET)
    public String c2v1(Model model)
    {
        OrderResponse order = c2IntegrationService.createOrder("4", "EUR");
        PaymentSessionResponse response = c2IntegrationService.createPaymentSession(order);
        model.addAttribute("src", c2Configuration.getHpp() + "/client/index.js");
        model.addAttribute("id", response.getId());
        return "/c2/legacy/c2-lb-legacy-merchant-demo";
    }

    @RequestMapping(value = "/c2/legacy/c2-lb-receive-payment-token", method = RequestMethod.POST)
    public String postlightbox1(@RequestParam("convergePaymentToken") String convergePaymentToken, Model model, final RedirectAttributes redirectAttributes)
    {
        // query hosted card to get acs
        final String uri = c2Configuration.getApi() + "/hosted-cards/" + convergePaymentToken;

        // create request
        HttpEntity request = new HttpEntity(c2IntegrationService.getHeader());

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<HostedCardResponse> response = restTemplate.exchange(uri, HttpMethod.GET, request, HostedCardResponse.class);
        HostedCardResponse card = response.getBody();

        model.addAttribute("termurl", c2Configuration.getMerchantSite() + "/c2/legacy/c2-lb-3dsv1-term");
        if (card.getThreeDSecureV1() != null)
        {
            model.addAttribute("pareq", card.getThreeDSecureV1().getPayerAuthenticationRequest());
            model.addAttribute("md", convergePaymentToken);
        }

        return "/c2/legacy/acsform";
    }

    @RequestMapping(value = "/c2/legacy/c2-lb-3dsv1-term", method = RequestMethod.POST)
    public String postForm(@RequestParam("PaRes") String paRes, @RequestParam("MD") String md,  Model model) throws
        URISyntaxException, java.io.UnsupportedEncodingException
    {
        // updated pares in hosted card
        final String uri = c2Configuration.getApi() + "/hosted-cards/" + md;

        HostedCardRequest cardrequest = new HostedCardRequest();
        TreeDSecureV1Response tds = new TreeDSecureV1Response();
        tds.setPayerAuthenticationResponse(paRes);
        cardrequest.setThreeDSecureV1(tds);

        HttpEntity request = new HttpEntity(cardrequest, c2IntegrationService.getHeader());

        RestTemplate restTemplate = new RestTemplate();
        HostedCardResponse result = restTemplate.postForObject( uri, request, HostedCardResponse.class);

        // create the transaction
        String transresult = c2IntegrationService.createTransaction("3.23", "EUR", uri, null);

        //return "redirect:/c2/done?response=" + URLEncoder.encode(transresult.getBody(), StandardCharsets.UTF_8.toString());
        String transactionResponse = Pattern.compile("\\n").matcher(transresult).replaceAll("<br/>");
        transactionResponse = Pattern.compile(" ").matcher(transactionResponse).replaceAll("&nbsp;");
        model.addAttribute("response", transactionResponse);
        return "c2/done";
    }

}
