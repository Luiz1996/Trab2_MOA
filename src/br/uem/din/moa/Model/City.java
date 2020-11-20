package br.uem.din.moa.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class City {
    private final List<Integer> distancias = new ArrayList<>();

    public City(){}

    public List<Integer> getDistancias() {
        return distancias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City cidade = (City) o;
        return distancias.equals(cidade.distancias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distancias);
    }
}
