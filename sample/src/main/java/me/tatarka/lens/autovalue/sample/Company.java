package me.tatarka.lens.autovalue.sample;

import com.google.auto.value.AutoValue;

import java.util.List;

import me.tatarka.lens.Lens;
import me.tatarka.lens.autovalue.AutoValueLenses;

@AutoValue
public abstract class Company {
    public static Company create(List<Job> jobs) {
        return new AutoValue_Company(jobs);
    }

    public static Lenses lenses() {
        return AutoValue_Company.Lenses.instance;
    }

    public abstract List<Job> jobs();

    @AutoValueLenses
    public interface Lenses {
        Lens<Company, List<Job>> jobs();
    }
}
