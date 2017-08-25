package me.tatarka.lens;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class LensesTest {

    @Test
    public void testListIndex() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        Lens<List<Integer>, Integer> lens = Lenses.listIndex(1);

        assertThat(lens.get(list)).isEqualTo(2);
        assertThat(lens.set(list, 8)).isEqualTo(Arrays.asList(1, 8, 3, 4));
    }

    @Test
    public void testMapKey() {
        Map<String, Integer> map = ImmutableMap.of("one", 1, "two", 2);
        Lens<Map<String, Integer>, Integer> lens = Lenses.mapKey("one");

        assertThat(lens.get(map)).isEqualTo(1);
        assertThat(lens.set(map, 8)).isEqualTo(ImmutableMap.of("one", 8, "two", 2));
    }
}
