package railway.g4;

import java.io.Serializable;

public class Pair implements Serializable 
{
    public int to;
    public int from;

    public Pair() {}

    public Pair(int _from, int _to) 
    {
        this.from = _from;
        this.to = _to;
    }

    public String toString()
    {
        String x = String.valueOf(from) + " -> " + String.valueOf(to);
        return x;
    }
}
