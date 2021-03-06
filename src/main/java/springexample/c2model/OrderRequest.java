package springexample.c2model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest
{
    private Total total;

    public Total getTotal()
    {
        return total;
    }

    public void setTotal(Total total)
    {
        this.total = total;
    }
}
