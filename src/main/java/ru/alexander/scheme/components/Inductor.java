package ru.alexander.scheme.components;

import ru.alexander.scheme.Component;

public class Inductor extends Component {
    public double L = 1;

    public Inductor(String name, String link1, String link2) {
        super(name, link1, link2);
    }

    @Override
    public void resetUpdates() {
        updatedU = false;
        updatedI = true;
    }

    public boolean calculate(double dt) {
        if (updatedU) {
            I -= U * dt / L;
            return true;
        } else return false;
    }
}
