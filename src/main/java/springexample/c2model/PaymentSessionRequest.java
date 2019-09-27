package springexample.c2model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSessionRequest
{
    private String order;
    private String returnUrl;
    private String cancelUrl;
    private String OriginUrl;

    public String getOrder()
    {
        return order;
    }

    public void setOrder(String order)
    {
        this.order = order;
    }

    public String getReturnUrl()
    {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl)
    {
        this.returnUrl = returnUrl;
    }

    public String getCancelUrl()
    {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl)
    {
        this.cancelUrl = cancelUrl;
    }

    public String getOriginUrl()
    {
        return OriginUrl;
    }

    public void setOriginUrl(String originUrl)
    {
        OriginUrl = originUrl;
    }
}
