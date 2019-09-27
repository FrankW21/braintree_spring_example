package springexample.c2model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSessionResponse
{
    private String id;
    private String expiresAt;
    private String modifiedAt;
    private String createdAt;
    private String forexAdvice;

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

    public String getModifiedAt()
    {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }

    public String getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(String createdAt)
    {
        this.createdAt = createdAt;
    }

    public String getForexAdvice()
    {
        return forexAdvice;
    }

    public void setForexAdvice(String forexAdvice)
    {
        this.forexAdvice = forexAdvice;
    }
}
