package com.lemon.catacombs.engine.render;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public interface YSortable {
     int getYSort();

     static <T extends YSortable, V extends Collection<T>> List<T> sort(V array) {
          List<T> list = new LinkedList<>(array);
          list.sort(Comparator.comparingInt(YSortable::getYSort));
          return list;
     }

     @SafeVarargs
     static <T extends YSortable, V extends Collection<T>> List<T> sort(V... array) {
          List<T> list = new LinkedList<>();
          for (V arr : array) {
               list.addAll(arr);
          }
          list.sort(Comparator.comparingInt(YSortable::getYSort));
          return list;
     }
}
