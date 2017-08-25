package me.tatarka.lens;

import me.tatarka.lens.function.IntToIntFunction;

public interface LensInt<Outer> extends Lens<Outer, Integer> {

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

    default Outer update(Outer outer, IntToIntFunction f) {
        return setAsInt(outer, f.applyAsInt(getAsInt(outer)));
    }

    default <Outer2> LensInt<Outer2> compose(Lens<Outer2, Outer> lens) {
        return new LensInt<Outer2>() {
            @Override
            public int getAsInt(Outer2 outer2) {
                return LensInt.this.getAsInt(lens.get(outer2));
            }

            @Override
            public Outer2 setAsInt(Outer2 outer2, int inner) {
                return lens.set(outer2, LensInt.this.setAsInt(lens.get(outer2), inner));
            }
        };
    }
}
