package me.tatarka.lens;

import me.tatarka.lens.function.LongToLongFunction;

public interface LongLens<Outer> extends Lens<Outer, Long> {

    long getAsLong(Outer outer);

    Outer setAsLong(Outer outer, long inner);

    @Override
    default Long get(Outer outer) {
        return getAsLong(outer);
    }

    @Override
    default Outer set(Outer outer, Long inner) {
        return setAsLong(outer, inner);
    }

    default Outer updateAsLong(Outer outer, LongToLongFunction f) {
        return setAsLong(outer, f.applyAsLong(getAsLong(outer)));
    }

    default <Outer2> LongLens<Outer2> compose(Lens<Outer2, Outer> lens) {
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
