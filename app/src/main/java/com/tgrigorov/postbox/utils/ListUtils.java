package com.tgrigorov.postbox.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ListUtils {
    public static <T> List<T> filter(List<T> list, IPredicate<T> predicate) {
        List<T> copy = new LinkedList<>(list);
        for (Iterator<T> i = copy.iterator(); i.hasNext();) {
            T next = i.next();
            if (!predicate.filter(next)) {
                i.remove();
            }
        }
        return copy;
    }

    public static <T> T first(List<T> list) throws IllegalArgumentException {
        if (list.size() == 0) {
            throw new IllegalArgumentException("The list is empty.");
        }
        return list.get(0);
    }

    public static <T> T firstOrDefault(List<T> list) {
        return list.size() > 0 ? list.get(0) : null;
    }
}
