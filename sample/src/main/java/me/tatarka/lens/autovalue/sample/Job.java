package me.tatarka.lens.autovalue.sample;

import com.google.auto.value.AutoValue;

import java.util.Map;

import me.tatarka.lens.Lens;
import me.tatarka.lens.autovalue.AutoValueLenses;

@AutoValue
public abstract class Job {

    public static Job create(Map<String, Person> people) {
        return new AutoValue_Job(people);
    }

    public static Lenses lenses() {
        return AutoValue_Job.Lenses.instance;
    }

    public abstract Map<String, Person> people();

    @AutoValueLenses
    public interface Lenses {
        Lens<Job, Map<String, Person>> people();
    }
}
