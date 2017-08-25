package me.tatarka.lens.function;

@FunctionalInterface
public interface Setter<Outer, Inner> {
    Outer set(Outer outer, Inner inner);
}
