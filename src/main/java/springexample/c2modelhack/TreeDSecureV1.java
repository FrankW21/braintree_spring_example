package springexample.c2modelhack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TreeDSecureV1
{
    private String payerAuthenticationResponse;

    public String getPayerAuthenticationResponse()
    {
        return payerAuthenticationResponse;
    }

    public void setPayerAuthenticationResponse(String payerAuthenticationResponse)
    {
        this.payerAuthenticationResponse = payerAuthenticationResponse;
    }
}
