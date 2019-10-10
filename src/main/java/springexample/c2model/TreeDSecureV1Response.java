package springexample.c2model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TreeDSecureV1Response
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
