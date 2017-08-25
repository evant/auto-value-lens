package me.tatarka.lens.autovalue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import me.tatarka.lens.IntLens;
import me.tatarka.lens.Lenses;
import me.tatarka.lens.autovalue.sample.Company;
import me.tatarka.lens.autovalue.sample.Job;
import me.tatarka.lens.autovalue.sample.Object1;
import me.tatarka.lens.autovalue.sample.Object2;
import me.tatarka.lens.autovalue.sample.Person;

import static com.google.common.truth.Truth.assertThat;

public class AutoValueLensTest {

    @Test
    public void composeGeneratedLenses() {
        Object1<Object2> object1 = Object1.create(Object2.create(1));
        IntLens<Object1<Object2>> lens = Object1.<Object2>lenses().object2().andThen(Object2.lenses().value());

        assertThat(lens.getAsInt(object1)).isEqualTo(1);
        assertThat(lens.setAsInt(object1, 2)).isEqualTo(Object1.create(Object2.create(2)));
    }

    @Test
    public void exampleTest() {
        Map<String, Person> job = new HashMap<>();
        job.put("Sue", Person.create(22));
        Company company = Company.create(Arrays.asList(
                Job.create(Collections.emptyMap()),
                Job.create(job)
        ));

        IntLens<Company> lens = Company.lenses().jobs()
                .andThen(Lenses.listIndex(1))
                .andThen(Job.lenses().people())
                .andThen(Lenses.mapKey("Sue"))
                .andThen(Person.lenses().age());

        Company newCompany = lens.updateAsInt(company, age -> age + 1);

        assertThat(newCompany.jobs().get(1).people().get("Sue").age()).isEqualTo(23);
    }
}