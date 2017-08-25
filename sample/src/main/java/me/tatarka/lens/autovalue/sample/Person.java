package me.tatarka.lens.autovalue.sample;

import com.google.auto.value.AutoValue;

import me.tatarka.lens.LensInt;
import me.tatarka.lens.autovalue.AutoValueLenses;

@AutoValue
public abstract class Person {

    public static Person create(int age) {
        return new AutoValue_Person(age);
    }

    public static Lenses lenses() {
        return AutoValue_Person.Lenses.instance;
    }

    public abstract int age();

    @AutoValueLenses
    public interface Lenses {
        LensInt<Person> age();
    }
}
