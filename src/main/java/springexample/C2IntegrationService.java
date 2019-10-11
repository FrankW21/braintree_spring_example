package springexample;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import springexample.c2model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

public class C2IntegrationService
{
    C2Configuration c2Configuration = Application.c2Configuration;

    // need to set a header on the api calls to handle authentication
    public HttpHeaders getHeader()
    {
        // create auth credentials
        String authStr = c2Configuration.getMerchantAlias() + ":" + c2Configuration.getSecretKey();
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        return headers;
    }

    // an order is used to set an amount and currency before 3ds verification
    public OrderResponse createOrder(String amount, String currencyCode)
    {
        final String uri = c2Configuration.getApi() + "/orders";

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


    // a payment-session must exist before calling 3ds.  It is composed of an order and other attributes needed by hpp and the lightbox
    public PaymentSessionResponse createPaymentSession(OrderResponse order)
    {
        final String uri = c2Configuration.getApi() + "/payment-sessions";

        PaymentSessionRequest pr = new PaymentSessionRequest();
        pr.setOrder( c2Configuration.getApi() + "/orders/" + order.getId());

        // return and cancel used for hpp but not the lightbox
        pr.setReturnUrl(c2Configuration.getMerchantSite() + "/c2/hpp/return");
        pr.setCancelUrl(c2Configuration.getMerchantSite() + "/c2/hpp/cancel");

        // originurl is required for the lightbox but not hpp
        pr.setOriginUrl(c2Configuration.getMerchantSite());

        // create request
        HttpEntity request = new HttpEntity(pr, getHeader());

        RestTemplate restTemplate = new RestTemplate();
        PaymentSessionResponse result = restTemplate.postForObject( uri, request, PaymentSessionResponse.class);
        return result;
    }

    public String createTransaction(String amount, String currencyCode, String hosteduri, String paymentsessionuri) throws
        URISyntaxException
    {
        final String sturi = c2Configuration.getApi() + "/transactions";
        URI turi = new URI(sturi);

        TransactionRequest tr = new TransactionRequest();
        Total total = new Total();
        total.setAmount(amount);
        total.setCurrencyCode(currencyCode);
        tr.setTotal(total);
        tr.setHostedCard(hosteduri);
        if (paymentsessionuri != null)
        {
            tr.setPaymentSession(paymentsessionuri);
        }
        tr.setDoCapture("false");

        HttpEntity transrequest = new HttpEntity(tr, getHeader());
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> transresult = restTemplate.postForEntity(turi, transrequest, String.class);
        return transresult.getBody();
    }

}
