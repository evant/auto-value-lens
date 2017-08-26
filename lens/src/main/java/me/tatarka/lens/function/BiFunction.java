package me.tatarka.lens.function;

@FunctionalInterface
public interface BiFunction<T, U, R> {
    R apply(T var1, U var2);
}