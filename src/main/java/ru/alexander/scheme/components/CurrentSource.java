package ru.alexander.scheme.components;

import ru.alexander.scheme.Component;

public class CurrentSource extends Component {

    public CurrentSource(String name, String positiveLink, String negativeLink) {
        super(name, positiveLink, negativeLink);
    }

    public boolean calculate(double dt) {
        updatedI = true;
        return true;
    }
}
