package me.tatarka.lens;

import me.tatarka.lens.function.LongToLongFunction;

import java.util.function.Function;

public interface LensLong<Outer> extends Lens<Outer, Long> {

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

    default Outer update(Outer outer, LongToLongFunction f) {
        return setAsLong(outer, f.applyAsLong(getAsLong(outer)));
    }

    default <Outer2> LensLong<Outer2> compose(Lens<Outer2, Outer> lens) {
        return new LensLong<Outer2>() {
            @Override
            public long getAsLong(Outer2 outer2) {
                return LensLong.this.getAsLong(lens.get(outer2));
            }

            @Override
            public Outer2 setAsLong(Outer2 outer2, long inner) {
                return lens.set(outer2, LensLong.this.setAsLong(lens.get(outer2), inner));
            }
        };
    }
}
