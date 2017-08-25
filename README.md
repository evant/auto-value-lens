# AutoValue: Lens Extension

An extension to google's [AutoValue](https://github.com/google/auto/tree/master/value) that creates
lenses for the properties of an AutoValue object.

## Download

### Java

```groovy
buildscript {
  repositories {
    maven { url "https://plugins.gradle.org/m2/" }
  }
  dependencies {
    classpath "net.ltgt.gradle:gradle-apt-plugin:0.11"
  }
}

apply plugin: "net.ltgt.apt"

repositories {
  maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}

dependencies {
  compile 'me.tatarka.lens:lens:0.1-SNAPSHOT'
  apt 'me.tatarka.lens:auto-value-lens:0.1-SNAPSHOT'
  compileOnly 'me.tatarka.lens:auto-value-lens-annotations:0.1-SNAPSHOT'
}
```

### Android

```groovy
repositories {
  maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}

dependencies {
  compile 'me.tatarka.lens:lens:0.1-SNAPSHOT'
  annotationProcessor 'me.tatarka.lens:auto-value-lens:0.1-SNAPSHOT'
  compileOnly 'me.tatarka.lens:auto-value-lens-annotations:0.1-SNAPSHOT'
}
```

## Usage

Create an interface (or abstract class) similar to how you would for a builder.

```java
@AutoValue public abstract class Foo {
  public abstract String bar();
  public abstract int baz();

  @AutoValueLenses public interface Lenses {
    Lens<Foo, String> bar();
    IntLens<Foo> baz();
  }

  public static Foo create(String bar, int baz) {
    return new AutoValue_Foo(bar, baz);
  }

  public static Lenses lenses() {
    // The generated implementation of your interface.
    return AutoValue_Foo.Lenses.instance;
  }
}
```

You can now do functional queries and updates to your fields.

```java
Foo foo = Foo.create("test", 1);
Foo.lenses().bar().get(foo); // => "test"
Foo.lenses().bar().set(foo, "new"); // => Foo("new", 1)
Foo.lenses().baz().updateAsInt(foo, v -> v + 1); // => Foo("test", 2)
```

## What are lenses? Why do I want them?

A lens abstracts over 'getting' and 'setting' a value. (Where setting is a functional update). What
makes them nice is that they are _composeable_. This makes is nicer to update a nested property.

For example, say you have the AutoValue classes:

```java
@AutoValue public abstract Company {
  public abstract List<Job> jobs();
  ...
}
@AutoValue public abstract Job {
  public abstract Map<String, Person> people();
  ...
}
@AutoValue public abstract Person {
  public abstract int age();
  ...
}
```

And you wanted to update the Person "Sue"'s age for job at index 1.

Without any extensions, you'd probably want to use builders:

```java
Job job = company.jobs().get(1);
Person person = job.people().get("Sue");
Person newPerson = person.newBuilder()
  .age(person.age() + 1)
  .build();
Job newJob = job.newBuilder()
  .people(updateMap(job.people(), "Sue", newPerson))
  .build();
Company newCompany = company.newBuilder()
  .jobs(updateList(company.jobs(), 1, newJob))
  .build();
```

The actual logic is `person.age() + 1` but its' lost in the boilerplate!

There is an [auto-value-with](https://github.com/gabrielittner/auto-value-with) which gives you with
methods that does make this a little nicer, but it's still a lot. And more importantly, your update
logic is still lost in the middle of it.

```java
Job job = company.jobs().get(1);
Person person = job.people().get("Sue");
Person newPerson = person.withAge(person.age() + 1);
Job newJob = job.withPeople(updateMap(job.people(), "Sue", newPerson));
Company newCompany = company.withJobs(newJob);
```

With lenses, you can separate your update logic from where it's applied. You build up a 'lens' into
your data, then you can directly manipulate it.

```java
IntLens<Company> lens = Company.lenses().jobs()
  .andThen(Lenses.listIndex(1))
  .andThen(Job.lenses().people())
  .andThen(Lenses.mapKey("Sue"))
  .andThen(Person.lenses().age());

Company newCompany = lens.updateAsInt(company, age -> age + 1);
```
