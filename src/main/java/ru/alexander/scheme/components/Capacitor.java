package ru.alexander.scheme.components;

import ru.alexander.scheme.Component;

public class Capacitor extends Component {
    public double C = 1e-5;

    public Capacitor(String name, String link1, String link2) {
        super(name, link1, link2);
    }

    @Override
    public void resetUpdates() {
        updatedI = false;
        updatedU = true;
    }

    public boolean calculate(double dt) {
        if (updatedI) {
            U -= I * dt / C;
            return true;
        }
        else return false;
    }
}
