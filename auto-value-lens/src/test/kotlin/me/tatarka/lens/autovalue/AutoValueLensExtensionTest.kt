package me.tatarka.lens.autovalue

import com.google.auto.value.processor.AutoValueProcessor
import com.google.common.truth.Truth.assertAbout
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory.javaSources
import org.junit.Test

class AutoValueLensExtensionTest {
    @Test
    fun emptyAbstactClass() {
        val source = JavaFileObjects.forSourceString("test.Test", """
            package test;

            import com.google.auto.value.AutoValue;
            import me.tatarka.lens.autovalue.AutoValueLenses;

            @AutoValue
            public abstract class Test {

                public static Lenses lenses() {
                    return AutoValue_Test.Lenses.instance;
                }

                @AutoValueLenses
                public static abstract class Lenses {
                }
            }
        """)

        val expectedSource = JavaFileObjects.forSourceString("test/AutoValue_Test", """
            package test;

            final class AutoValue_Test extends ${'$'}AutoValue_Test {
                AutoValue_Test() {
                    super();
                }

                static final class Lenses extends Test.Lenses {
                    static final Lenses instance = new Lenses()

                    private Lenses() {
                    }
                }
            }
        """)

        assertAbout(javaSources())
                .that(listOf(source))
                .processedWith(AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource)
    }

    @Test
    fun emptyInterface() {
        val source = JavaFileObjects.forSourceString("test.Test", """
            package test;

            import com.google.auto.value.AutoValue;
            import me.tatarka.lens.autovalue.AutoValueLenses;

            @AutoValue
            public abstract class Test {

                public static Lenses lenses() {
                    return AutoValue_Test.Lenses.instance;
                }

                @AutoValueLenses
                public interface Lenses {
                }
            }
        """)

        val expectedSource = JavaFileObjects.forSourceString("test/AutoValue_Test", """
            package test;

            final class AutoValue_Test extends ${'$'}AutoValue_Test {
                AutoValue_Test() {
                    super();
                }

                static final class Lenses implements Test.Lenses {
                    static final Lenses instance = new Lenses()

                    private Lenses() {
                    }
                }
            }
        """)

        assertAbout(javaSources())
                .that(listOf(source))
                .processedWith(AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource)
    }

    @Test
    fun simple() {
        val source = JavaFileObjects.forSourceString("test.Test", """
            package test;

            import com.google.auto.value.AutoValue;
            import me.tatarka.lens.Lens;
            import me.tatarka.lens.LensInt;
            import me.tatarka.lens.autovalue.AutoValueLenses;

            @AutoValue
            public abstract class Test {

                public abstract String s();

                public abstract int i();

                public static Lenses lenses() {
                    return AutoValue_Test.Lenses.instance;
                }

                @AutoValueLenses
                public static abstract class Lenses {
                    public abstract Lens<Test, String> s();

                    public abstract LensInt<Test> i();
                }
            }
        """)

        val expectedSource = JavaFileObjects.forSourceString("test/AutoValue_Test", """
            package test;

            import java.lang.Override;
            import java.lang.String;
            import me.tatarka.lens.Lens;
            import me.tatarka.lens.LensInt;

            final class AutoValue_Test extends ${'$'}AutoValue_Test {
                AutoValue_Test(String s, int i) {
                    super(s, i);
                }

                static final class Lenses extends Test.Lenses {
                    static final Lenses instance = new Lenses();

                    private final Lens<Test, String> s = new Lens<Test, String>() {
                        @Override
                        public String get(Test outer) {
                            return outer.s();
                        }

                        @Override
                        public Test set(Test outer, String inner) {
                            return new AutoValue_Test(inner, outer.i());
                        }
                    };

                    private final LensInt<Test> i = new LensInt<Test>() {
                        @Override
                        public int getAsInt(Test outer) {
                            return outer.i();
                        }

                        @Override
                        public Test setAsInt(Test outer, int inner) {
                            return new AutoValue_Test(outer.s(), inner);
                        }
                    };

                    private Lenses() {
                    }

                    @Override
                    public Lens<Test, String> s() {
                        return s;
                    }

                    @Override
                    public LensInt<Test> i() {
                        return i;
                    }
                }
            }
        """)

        assertAbout(javaSources())
                .that(listOf(source))
                .processedWith(AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource)
    }

    @Test
    fun generic() {
        val source = JavaFileObjects.forSourceString("test.Test", """
            package test;

            import com.google.auto.value.AutoValue;
            import me.tatarka.lens.Lens;
            import me.tatarka.lens.LensInt;
            import me.tatarka.lens.autovalue.AutoValueLenses;

            @AutoValue
            public abstract class Test<T> {

                public abstract T t();

                public static <T> Lenses<T> lenses() {
                    return AutoValue_Test.Lenses.instance;
                }

                @AutoValueLenses
                public static abstract class Lenses<T> {
                    public abstract Lens<Test<T>, T> t();
                }
            }
        """)

        val expectedSource = JavaFileObjects.forSourceString("test/AutoValue_Test", """
            package test;

            import java.lang.Override;
            import me.tatarka.lens.Lens;

            final class AutoValue_Test<T> extends ${'$'}AutoValue_Test<T> {
                AutoValue_Test(T t) {
                    super(t);
                }

                static final class Lenses<T> extends Test.Lenses<T> {
                    static final Lenses instance = new Lenses();

                    private final Lens<Test<T>, T> t = new Lens<Test<T>, T>() {
                        @Override
                        public T get(Test<T> outer) {
                            return outer.t();
                        }

                        @Override
                        public Test<T> set(Test<T> outer, T inner) {
                            return new AutoValue_Test(inner);
                        }
                    };

                    private Lenses() {
                    }

                    @Override
                    public Lens<Test<T>, T> t() {
                        return t;
                    }
                }
            }
        """)

        assertAbout(javaSources())
                .that(listOf(source))
                .processedWith(AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource)
    }
}