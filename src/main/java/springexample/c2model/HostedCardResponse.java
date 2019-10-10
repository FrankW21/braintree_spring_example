package springexample.c2model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HostedCardResponse
{
    private String id;
    private String expiresAt;
    private TreeDSecureV1 threeDSecureV1;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getExpiresAt()
    {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt)
    {
        this.expiresAt = expiresAt;
    }

    public TreeDSecureV1 getThreeDSecureV1()
    {
        return threeDSecureV1;
    }

    public void setThreeDSecureV1(TreeDSecureV1 threeDSecureV1)
    {
        this.threeDSecureV1 = threeDSecureV1;
    }
}
