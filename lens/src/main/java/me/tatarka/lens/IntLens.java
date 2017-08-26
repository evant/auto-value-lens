package me.tatarka.lens;

import me.tatarka.lens.function.IntToIntFunction;

public abstract class IntLens<Outer> extends Lens<Outer, Integer> {

    public abstract int getAsInt(Outer outer);

    public abstract Outer setAsInt(Outer outer, int inner);

    public Outer updateAsInt(Outer outer, IntToIntFunction f) {
        return setAsInt(outer, f.applyAsInt(getAsInt(outer)));
    }

    @Override
    public Integer get(Outer outer) {
        return getAsInt(outer);
    }

    @Override
    public Outer set(Outer outer, Integer inner) {
        return setAsInt(outer, inner);
    }

    @Override
    public <Outer2> IntLens<Outer2> compose(final Lens<Outer2, Outer> lens) {
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
