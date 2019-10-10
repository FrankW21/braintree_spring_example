package springexample.c2model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequest
{
    private Total total;
    private String hostedCard;
    private String paymentSession;
    private String doCapture;

    public Total getTotal()
    {
        return total;
    }

    public void setTotal(Total total)
    {
        this.total = total;
    }

    public String getHostedCard()
    {
        return hostedCard;
    }

    public void setHostedCard(String hostedCard)
    {
        this.hostedCard = hostedCard;
    }

    public String getDoCapture()
    {
        return doCapture;
    }

    public void setDoCapture(String doCapture)
    {
        this.doCapture = doCapture;
    }

    public String getPaymentSession()
    {
        return paymentSession;
    }

    public void setPaymentSession(String paymentSession)
    {
        this.paymentSession = paymentSession;
    }
}
