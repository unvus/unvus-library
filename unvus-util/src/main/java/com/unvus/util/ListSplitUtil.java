package com.unvus.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ListSplitUtil {

    public static <T> SplitCud<T> splitCud(List<T> oldList, List<T> newList) {
        List<T> addList = newList.stream().filter(nu -> !oldList.contains(nu)).collect(Collectors.toList());
        List<T> updateList = newList.stream().filter(oldList::contains).collect(Collectors.toList());
        List<T> deleteList = oldList.stream().filter(old -> !newList.contains(old)).collect(Collectors.toList());

        return new SplitCud<>(addList, deleteList, updateList);
    }

    public static <T> SplitCudEx<T> splitCud(List<T> oldList, List<T> newList, SplitHelper<T> splitHelper) {
        List<T> notChangedList = new ArrayList<>();
        List<T> updateList = new ArrayList<>();
        List<T> deleteList = new ArrayList<>();
        oldList.forEach(old -> {
            if(!newList.contains(old)) {
                deleteList.add(old);
            }
            if(newList.contains(old)) {
                if(splitHelper.isUpdated(old, newList.get(newList.indexOf(old)))) {
                    updateList.add(old);
                }else {
                    notChangedList.add(old);
                }
            }
        });

        List<T> addList = newList.stream().filter(nu -> !oldList.contains(nu)).collect(Collectors.toList());
        return new SplitCudEx<>(addList, deleteList, updateList, notChangedList);
    }

    @Getter
    @AllArgsConstructor
    public static class SplitCud<T> {
        protected List<T> addList;
        protected List<T> deleteList;
        protected List<T> updateList;
    }

    @Getter
    public static class SplitCudEx<T> extends SplitCud<T> {
        protected List<T> notChangedList;

        public SplitCudEx(List<T> addList, List<T> deleteList, List<T> updateList, List<T> notChangedList) {
            super(addList, deleteList, updateList);
            this.notChangedList = notChangedList;
        }
    }

    @FunctionalInterface
    public interface SplitHelper<T> {
        boolean isUpdated(T old, T nu);
    }

}
