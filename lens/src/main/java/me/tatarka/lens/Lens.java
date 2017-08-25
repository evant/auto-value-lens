package me.tatarka.lens;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Lens<Outer, Inner> {

    Inner get(Outer outer);

    Outer set(Outer outer, Inner inner);

    default Outer update(Outer outer, Function<Inner, Inner> f) {
        return set(outer, f.apply(get(outer)));
    }

    default <Outer2> Lens<Outer2, Inner> compose(Lens<Outer2, Outer> lens) {
        return new Lens<Outer2, Inner>() {
            @Override
            public Inner get(Outer2 outer2) {
                return Lens.this.get(lens.get(outer2));
            }

            @Override
            public Outer2 set(Outer2 outer2, Inner inner) {
                return lens.set(outer2, Lens.this.set(lens.get(outer2), inner));
            }
        };
    }

    default <Inner2> Lens<Outer, Inner2> andThen(Lens<Inner, Inner2> lens) {
        return lens.compose(this);
    }

    default IntLens<Outer> andThen(IntLens<Inner> lens) {
        return lens.compose(this);
    }

    default LongLens<Outer> andThen(LongLens<Inner> lens) {
        return lens.compose(this);
    }

    default DoubleLens<Outer> andThen(DoubleLens<Inner> lens) {
        return lens.compose(this);
    }

    static <T> Lens<T, T> identity() {
        return new Lens<T, T>() {
            @Override
            public T get(T value) {
                return value;
            }

            @Override
            public T set(T value, T newValue) {
                return newValue;
            }
        };
    }

    static <Outer, Inner> Lens<Outer, Inner> of(Function<Outer, Inner> getFunction, BiFunction<Outer, Inner, Outer> setFunction) {
        return new Lens<Outer, Inner>() {
            @Override
            public Inner get(Outer outer) {
                return getFunction.apply(outer);
            }

            @Override
            public Outer set(Outer outer, Inner inner) {
                return setFunction.apply(outer, inner);
            }
        };
    }
}
