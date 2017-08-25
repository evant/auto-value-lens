package me.tatarka.lens.autovalue.sample;

import com.google.auto.value.AutoValue;
import me.tatarka.lens.Lens;
import me.tatarka.lens.autovalue.AutoValueLenses;

@AutoValue
public abstract class Object1<T> {

    public static <T> Object1<T> create(T object2) {
        return new AutoValue_Object1(object2);
    }

    public static <T> Lenses<T> lenses() {
        return AutoValue_Object1.Lenses.instance;
    }

    public abstract T object2();

    @AutoValueLenses
    public interface Lenses<T> {
        Lens<Object1<T>, T> object2();
    }
}
