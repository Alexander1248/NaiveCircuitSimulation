package ru.alexander.scheme.components;

import ru.alexander.scheme.Component;

public class Diode extends Component {
    public double Is = 1e-12;
    public double T = 300;
    public double n = 1.5;
    public double R = 1e4;
    public boolean direction = false;

    public Diode(String name, String link1, String link2) {
        super(name, link1, link2);
    }
    @Override
    public void resetUpdates() {
        updatedU = false;
        updatedI = true;
    }

    public boolean calculate(double dt) {
        if (updatedU) {
            double nut = n * 0.86e-4 * T;
            double isr = Is * R;
            if (direction) I = -nut / R * W(isr / nut * Math.exp((-U + isr) / nut));
            else I = nut / R * W(isr / nut * Math.exp((U + isr) / nut));
            return true;
        }
        else return false;
    }

    private double stepW(double prev, double x) {
        double exp = Math.exp(prev);
        return (prev * prev * exp + x) / (exp * (prev + 1));
    }
    private double W(double x) {
        double y = 1;
        if (x > 2) {
            y = Math.log(x);
            y = y - Math.log(y);
        }
        for (int i = 0; i < 3; i++)
            y = stepW(y, x);
        return y;
    }
}
