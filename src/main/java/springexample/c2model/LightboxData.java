package springexample.c2model;

public class LightboxData
{
    String src;
    String md;
    String dataSessionId;
    String dataMerchantAlias;
    String dataPublicKey;

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

    public String getDataSessionId()
    {
        return dataSessionId;
    }

    public void setDataSessionId(String dataSessionId)
    {
        this.dataSessionId = dataSessionId;
    }

    public String getDataMerchantAlias()
    {
        return dataMerchantAlias;
    }

    public void setDataMerchantAlias(String dataMerchantAlias)
    {
        this.dataMerchantAlias = dataMerchantAlias;
    }

    public String getDataPublicKey()
    {
        return dataPublicKey;
    }

    public void setDataPublicKey(String dataPublicKey)
    {
        this.dataPublicKey = dataPublicKey;
    }
}
