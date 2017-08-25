package me.tatarka.lens;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class LensTest {

    @Test
    public void identity() {
        Lens<Integer, Integer> id = Lens.identity();

        assertThat(id.get(1)).isEqualTo(1);
        assertThat(id.set(1, 2)).isEqualTo(2);
    }

    @Test
    public void compose() {
        LensInt<Object1> lens = Object2.lens.compose(Object1.lens);
        Object1 object1 = new Object1(new Object2(1));

        assertThat(lens.getAsInt(object1)).isEqualTo(1);
        assertThat(lens.setAsInt(object1, 2)).isEqualTo(new Object1(new Object2(2)));
    }

    @Test
    public void andThen() {
        LensInt<Object1> lens = Object1.lens.andThen(Object2.lens);
        Object1 object1 = new Object1(new Object2(1));

        assertThat(lens.getAsInt(object1)).isEqualTo(1);
        assertThat(lens.setAsInt(object1, 2)).isEqualTo(new Object1(new Object2(2)));
    }

    static class Object1 {
        final Object2 object2;

        static Lens<Object1, Object2> lens = new Lens<Object1, Object2>() {
            @Override
            public Object2 get(Object1 object1) {
                return object1.object2;
            }

            @Override
            public Object1 set(Object1 object1, Object2 object2) {
                return new Object1(object2);
            }
        };

        Object1(Object2 object2) {
            this.object2 = object2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Object1 object1 = (Object1) o;
            return object2.equals(object1.object2);
        }

        @Override
        public int hashCode() {
            return object2.hashCode();
        }
    }

    static class Object2 {
        final int value;

        Object2(int value) {
            this.value = value;
        }

        static LensInt<Object2> lens = new LensInt<Object2>() {
            @Override
            public int getAsInt(Object2 object2) {
                return object2.value;
            }

            @Override
            public Object2 setAsInt(Object2 object2, int inner) {
                return new Object2(inner);
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Object2 object2 = (Object2) o;
            return value == object2.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }
}
