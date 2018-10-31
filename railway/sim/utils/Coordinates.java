package railway.sim.utils;

import java.io.Serializable;

public class Coordinates implements Serializable {
    public double x;
    public double y;

    public Coordinates() {}

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
