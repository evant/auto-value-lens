package me.tatarka.lens;

import me.tatarka.lens.function.LongToLongFunction;

public abstract class LongLens<Outer> extends Lens<Outer, Long> {

    public abstract long getAsLong(Outer outer);

    public abstract Outer setAsLong(Outer outer, long inner);

    @Override
    public Long get(Outer outer) {
        return getAsLong(outer);
    }

    @Override
    public Outer set(Outer outer, Long inner) {
        return setAsLong(outer, inner);
    }

    public Outer updateAsLong(Outer outer, LongToLongFunction f) {
        return setAsLong(outer, f.applyAsLong(getAsLong(outer)));
    }

    @Override
    public <Outer2> LongLens<Outer2> compose(final Lens<Outer2, Outer> lens) {
        return new LongLens<Outer2>() {
            @Override
            public long getAsLong(Outer2 outer2) {
                return LongLens.this.getAsLong(lens.get(outer2));
            }

            @Override
            public Outer2 setAsLong(Outer2 outer2, long inner) {
                return lens.set(outer2, LongLens.this.setAsLong(lens.get(outer2), inner));
            }
        };
    }
}
