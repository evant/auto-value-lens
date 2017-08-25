package me.tatarka.lens;

import me.tatarka.lens.function.IntToIntFunction;

public interface IntLens<Outer> extends Lens<Outer, Integer> {

    int getAsInt(Outer outer);

    Outer setAsInt(Outer outer, int inner);

    @Override
    default Integer get(Outer outer) {
        return getAsInt(outer);
    }

    @Override
    default Outer set(Outer outer, Integer inner) {
        return setAsInt(outer, inner);
    }

    default Outer updateAsInt(Outer outer, IntToIntFunction f) {
        return setAsInt(outer, f.applyAsInt(getAsInt(outer)));
    }

    default <Outer2> IntLens<Outer2> compose(Lens<Outer2, Outer> lens) {
        return new IntLens<Outer2>() {
            @Override
            public int getAsInt(Outer2 outer2) {
                return IntLens.this.getAsInt(lens.get(outer2));
            }

            @Override
            public Outer2 setAsInt(Outer2 outer2, int inner) {
                return lens.set(outer2, IntLens.this.setAsInt(lens.get(outer2), inner));
            }
        };
    }
}
