package springexample.c2model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// the data in this structure is returned by the 3ds callback
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantData
{
    private String amount;
    private String currencyCode;
    private String paymentSessionId;
    // add merchant specific data here

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCurrencyCode()
    {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public String getPaymentSessionId()
    {
        return paymentSessionId;
    }

    public void setPaymentSessionId(String paymentSessionId)
    {
        this.paymentSessionId = paymentSessionId;
    }
}
