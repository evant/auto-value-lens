package me.tatarka.lens;

import me.tatarka.lens.function.DoubleToDoubleFunction;

public interface DoubleLens<Outer> extends Lens<Outer, Double> {

    double getAsDouble(Outer outer);

    Outer setAsDouble(Outer outer, double inner);

    @Override
    default Double get(Outer outer) {
        return getAsDouble(outer);
    }

    @Override
    default Outer set(Outer outer, Double inner) {
        return setAsDouble(outer, inner);
    }

    default Outer updateAsDouble(Outer outer, DoubleToDoubleFunction f) {
        return setAsDouble(outer, f.applyAsDouble(getAsDouble(outer)));
    }

    default <Outer2> DoubleLens<Outer2> compose(Lens<Outer2, Outer> lens) {
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
