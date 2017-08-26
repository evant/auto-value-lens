package me.tatarka.lens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Lenses {
    private Lenses() {
        throw new AssertionError();
    }

    public static <E> Lens<List<E>, E> listIndex(final int index) {
        return new Lens<List<E>, E>() {
            @Override
            public E get(List<E> list) {
                return list.get(index);
            }

            @Override
            public List<E> set(List<E> list, E element) {
                List<E> newList = new ArrayList<>(list);
                newList.set(index, element);
                return newList;
            }
        };
    }

    public static <K, V> Lens<Map<K, V>, V> mapKey(final K key) {
        return new Lens<Map<K, V>, V>() {
            @Override
            public V get(Map<K, V> map) {
                return map.get(key);
            }

            @Override
            public Map<K, V> set(Map<K, V> map, V value) {
                Map<K, V> newMap = new HashMap<>(map);
                newMap.put(key, value);
                return newMap;
            }
        };
    }
}
