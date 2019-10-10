package springexample.c2model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HostedCardRequest
{
    // using 2nd version of object because the hostedCard update wants payerAuthenticationResponse
    private TreeDSecureV1Response threeDSecureV1;

    public TreeDSecureV1Response getThreeDSecureV1()
    {
        return threeDSecureV1;
    }

    public void setThreeDSecureV1(TreeDSecureV1Response threeDSecureV1)
    {
        this.threeDSecureV1 = threeDSecureV1;
    }
}
