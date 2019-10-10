package springexample.c2model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse
{
    private String id;
    private Total total;

    public Total getTotal()
    {
        return total;
    }

    public void setTotal(Total total)
    {
        this.total = total;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
