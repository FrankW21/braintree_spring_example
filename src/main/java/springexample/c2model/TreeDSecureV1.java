package springexample.c2model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.org.apache.xpath.internal.operations.Bool;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TreeDSecureV1
{
    private Boolean isSupported;
    private Boolean isSuccessful;
    private String accessControlServerUrl;
    private String payerAuthenticationRequest;

    public Boolean getSupported()
    {
        return isSupported;
    }

    public void setSupported(Boolean supported)
    {
        isSupported = supported;
    }

    public Boolean getIsSuccessful()
    {
        return isSuccessful;
    }

    public void setIsSuccessful(Boolean isSuccessful)
    {
        this.isSuccessful = isSuccessful;
    }

    public String getAccessControlServerUrl()
    {
        return accessControlServerUrl;
    }

    public void setAccessControlServerUrl(String accessControlServerUrl)
    {
        this.accessControlServerUrl = accessControlServerUrl;
    }

    public String getPayerAuthenticationRequest()
    {
        return payerAuthenticationRequest;
    }

    public void setPayerAuthenticationRequest(String payerAuthenticationRequest)
    {
        this.payerAuthenticationRequest = payerAuthenticationRequest;
    }
}
