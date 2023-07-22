package ru.alexander.scheme;

import java.util.Arrays;

public abstract class Component {
    public double U;
    public double I;

    public boolean updatedU;
    public boolean updatedI;

    private final String name;
    private final String[] pinLinks;

    protected Component(String name, String... pinLinks) {
        this.name = name;
        this.pinLinks = pinLinks;
    }
    public String getName() {
        return name;
    }

    public String[] getPinLinks() {
        return pinLinks;
    }

    public abstract boolean calculate(double dt);
    public void resetUpdates() {
        updatedI = false;
        updatedU = false;
    }
    public boolean isUpdated() {
        return updatedI && updatedU;
    }


    @Override
    public String toString() {
        return "{name=" + name + ", pinLinks=" + Arrays.toString(pinLinks) + '}';
    }
}
