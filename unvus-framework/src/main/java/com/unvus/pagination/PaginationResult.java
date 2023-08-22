package com.unvus.pagination;

public class PaginationResult {
    public static ThreadLocal<PageModel> pageModel = new ThreadLocal<>();
    public static ThreadLocal<SortModel> sortModel = new ThreadLocal<>();

    public static void reset() {
        PaginationResult.pageModel.set(null);
        PaginationResult.sortModel.set(null);
    }
}
