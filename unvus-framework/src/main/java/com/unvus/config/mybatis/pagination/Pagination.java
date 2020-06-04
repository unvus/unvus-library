package com.unvus.config.mybatis.pagination;


import java.util.List;

public class Pagination {
	public static ThreadLocal<Integer> currentPage = new ThreadLocal<>();
	public static ThreadLocal<Integer> dataPerPage = new ThreadLocal<>();
	public static ThreadLocal<Integer> linkPerPage = new ThreadLocal<>();
	public static ThreadLocal<Integer> totalCnt = new ThreadLocal<>();
	public static ThreadLocal<String> queryString = new ThreadLocal<>();
	public static ThreadLocal<List<OrderBy>> orderBy = new ThreadLocal<>();
    public static ThreadLocal<List<String>> projections = new ThreadLocal<>();
    public static ThreadLocal<Boolean> skipPaging = new ThreadLocal<>();
    public static ThreadLocal<Boolean> skipCount = new ThreadLocal<>();
    public static ThreadLocal<Boolean> projectionToJoin = new ThreadLocal<>();

	public static void resetAll() {
		Pagination.currentPage.set(null);
		Pagination.dataPerPage.set(null);
		Pagination.linkPerPage.set(null);
		Pagination.totalCnt.set(null);
		Pagination.queryString.set(null);
        Pagination.orderBy.set(null);
        Pagination.projections.set(null);
        Pagination.skipPaging.set(null);
        Pagination.skipCount.set(null);
        Pagination.projectionToJoin.set(null);
	}

}
