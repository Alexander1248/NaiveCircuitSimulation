package ru.alexander.scheme.components;

import ru.alexander.scheme.Component;

public class VoltageSource extends Component {

    public VoltageSource(String name, String negativeLink, String positiveLink) {
        super(name, negativeLink, positiveLink);
    }

    @Override
    public boolean calculate(double dt) {
        updatedU = true;
        return true;
    }
}
