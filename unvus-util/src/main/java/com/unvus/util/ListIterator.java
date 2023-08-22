package com.unvus.util;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListIterator {

    public static <T> int workInPagination(int dataPerPage, List<T> list, InListPagination<T> inPagination) {

        return workInPagination(dataPerPage, list, inPagination, 0);
    }

    public static <T> int workInPagination(int dataPerPage, List<T> list, InListPagination<T> inPagination, int limitPage) {
        int totalCount = 0;
        int currentPage = 1;

        while (true) {
            log.info("do job in pagination - page:" + currentPage);
            int from = (currentPage - 1) * dataPerPage;
            int to = Math.min(from + dataPerPage, list.size());

            List<T> subList = list.subList(from, to);

            inPagination.job(subList);
            int currCount = subList.size();
            totalCount += currCount;
            if(currCount < dataPerPage || totalCount == list.size()) {
                break;
            }
            currentPage++;
            if(limitPage > 0 && currentPage >= limitPage) {
                break;
            }
        }
        return totalCount;
    }

    @FunctionalInterface
    public interface InListPagination<T> {
        void job(List<T> list);
    }


}
