package springexample.c2model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HostedCardRequest
{
    // using 2nd version of object because the hostedCard update wants payerAuthenticationResponse
    private springexample.c2modelhack.TreeDSecureV1 threeDSecureV1;

    public springexample.c2modelhack.TreeDSecureV1 getThreeDSecureV1()
    {
        return threeDSecureV1;
    }

    public void setThreeDSecureV1(springexample.c2modelhack.TreeDSecureV1 threeDSecureV1)
    {
        this.threeDSecureV1 = threeDSecureV1;
    }
}
