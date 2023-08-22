package com.unvus.pagination.util;

import java.util.List;

import com.unvus.pagination.Pagination;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DbIterator {

    public static int workInPagination(int dataPerPage, InDbPagination inPagination) {
        return workInPagination(dataPerPage, inPagination, 0);
    }

    public static int workInPagination(int dataPerPage, InDbPagination inPagination, int limitPage) {
        int totalCount = 0;
        int currentPage = 1;

        while (true) {
//            log.info("do job in pagination - page:" + currentPage);
            Pagination.of(currentPage++, dataPerPage);
            Pagination.pageModel.get().setSkipCount(true);
            List list = inPagination.job();
            int currCount = list.size();
            totalCount += currCount;
            if(currCount < dataPerPage) {
                break;
            }
            if(limitPage > 0 && currentPage >= limitPage) {
                break;
            }
        }
        return totalCount;
    }

    @FunctionalInterface
    public interface InDbPagination {
        List job();
    }

}

