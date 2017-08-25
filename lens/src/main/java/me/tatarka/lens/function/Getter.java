package me.tatarka.lens.function;

@FunctionalInterface
public interface Getter<Outer, Inner> {
    Inner get(Outer outer);
}
