package ru.alexander.scheme.components;

import ru.alexander.scheme.Component;

public class Resistor extends Component {
    public double R = 1e3;

    public Resistor(String name, String link1, String link2) {
        super(name, link1, link2);
    }

    public boolean calculate(double dt) {
        if (updatedI) {
            U = I * R;
            updatedU = true;
            return true;
        } else if (updatedU) {
            I = U / R;
            updatedI = true;
            return true;
        }
        else return false;
    }
}
