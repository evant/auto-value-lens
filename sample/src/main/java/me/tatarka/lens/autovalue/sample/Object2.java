package me.tatarka.lens.autovalue.sample;

import com.google.auto.value.AutoValue;

import me.tatarka.lens.IntLens;
import me.tatarka.lens.autovalue.AutoValueLenses;

@AutoValue
public abstract class Object2 {

    public static Object2 create(int value) {
        return new AutoValue_Object2(value);
    }

    public static Lenses lenses() {
        return AutoValue_Object2.Lenses.instance;
    }

    public abstract int value();

    @AutoValueLenses
    public interface Lenses {
        IntLens<Object2> value();
    }
}
