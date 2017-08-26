package me.tatarka.lens.function;

@FunctionalInterface
public interface Function<T, R> {
    R apply(T var1);
}