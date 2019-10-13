package springexample.c2model;

public class LightboxData
{
    String src;
    String md;
    String sessionId;
    String merchantAlias;
    String publicKey;

    public String getSrc()
    {
        return src;
    }

    public void setSrc(String src)
    {
        this.src = src;
    }

    public String getMd()
    {
        return md;
    }

    public void setMd(String md)
    {
        this.md = md;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getMerchantAlias()
    {
        return merchantAlias;
    }

    public void setMerchantAlias(String merchantAlias)
    {
        this.merchantAlias = merchantAlias;
    }

    public String getPublicKey()
    {
        return publicKey;
    }

    public void setPublicKey(String publicKey)
    {
        this.publicKey = publicKey;
    }
}
