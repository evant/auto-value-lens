package me.tatarka.lens;

import me.tatarka.lens.function.DoubleToDoubleFunction;

public abstract class DoubleLens<Outer> extends Lens<Outer, Double> {

    public abstract double getAsDouble(Outer outer);

    public abstract Outer setAsDouble(Outer outer, double inner);

    public Outer updateAsDouble(Outer outer, DoubleToDoubleFunction f) {
        return setAsDouble(outer, f.applyAsDouble(getAsDouble(outer)));
    }

    @Override
    public Double get(Outer outer) {
        return getAsDouble(outer);
    }

    @Override
    public Outer set(Outer outer, Double inner) {
        return setAsDouble(outer, inner);
    }

    @Override
    public <Outer2> DoubleLens<Outer2> compose(final Lens<Outer2, Outer> lens) {
        return new DoubleLens<Outer2>() {
            @Override
            public double getAsDouble(Outer2 outer2) {
                return DoubleLens.this.getAsDouble(lens.get(outer2));
            }

            @Override
            public Outer2 setAsDouble(Outer2 outer2, double inner) {
                return lens.set(outer2, DoubleLens.this.setAsDouble(lens.get(outer2), inner));
            }
        };
    }
}
