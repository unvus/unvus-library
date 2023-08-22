package com.unvus.pagination;


import com.unvus.config.UnvusConstants;
import com.unvus.web.util.WebUtil;
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Pagination {
    private static final Pagination INSTANCE = new Pagination();

    public static ThreadLocal<PageModel> pageModel = new ThreadLocal<>();
    public static ThreadLocal<SortModel> sortModel = new ThreadLocal<>();
    public static ThreadLocal<Boolean> keepAsResult = new ThreadLocal<>();

    public static Pagination getInstance() {
        return Pagination.INSTANCE;
    }


	public static Pagination reset() {
        Pagination.pageModel.set(null);
        Pagination.sortModel.set(null);
        Pagination.keepAsResult.set(null);
        return INSTANCE;
	}

    /**
     * 요청(<code>HttpServletRequest</code>) 파라미터에서 pagination 정보를 가져와서 설정한다.<br/>
     * request 는 RequestContextHolder 로 부터 가져온다.<br/>
     * 이 pagination 실행 정보(currentPage, dataPerPage) 와 pagination 적용 결과(totalCount) 를 응답(code>HttpServletResponse</code>) header 에 설정한다. <br/>
     *
     * Spring MVC 를 거치지 않을때는 이 method 대신 <code>primary(HttpServletRequest request)</code> 를 사용하여에 직접 request 를 넘겨주도록 한다. <br/>
     *
     * 이 메소드 호출 이후 SQL 실행시 Pagination 기능을 1회 적용한다.
     * @return
     */
    public static Pagination primary() {
        return primary(WebUtil.request());
    }

    /**
     * 요청(<code>HttpServletRequest</code>) 파라미터에서 pagination 정보를 가져와서 설정한다. <br/>
     * 이 pagination 실행 정보(currentPage, dataPerPage) 와 pagination 적용 결과(totalCount) 를 응답(code>HttpServletResponse</code>) header 에 설정한다. <br/>
     *
     * 이 메소드 호출 이후 SQL 실행시 Pagination 기능을 1회 적용한다.
     * @param request
     * @return
     */
    public static Pagination primary(HttpServletRequest request) {
        if(request == null) {
            return INSTANCE;
        }
        Pagination.sortModel.set((SortModel) request.getAttribute(UnvusConstants.SORT_MODEL));

        if(!Boolean.FALSE.equals(request.getAttribute(UnvusConstants.USE_PAGING))) {
            Pagination.pageModel.set((PageModel) request.getAttribute(UnvusConstants.PAGE_MODEL));
            Pagination.keepAsResult.set(true);
        }

        return INSTANCE;
    }

    /**
     * 페이지-소트 모델(<code>PageSortModel</code>) 기반으로 pagination 정보를 설정한다. <br/>
     * 이 메소드 호출 이후 SQL 실행시 Pagination 기능을 1회 적용한다.
     * @param pageModel
     * @return
     */
    public static Pagination of(PageModel pageModel) {
        return of(pageModel,false);
    }

    /**
     * 페이지-소트 모델(<code>PageSortModel</code>) 기반으로 pagination 정보를 설정한다. <br/>
     * 이 pagination 실행 정보(currentPage, dataPerPage) 와 pagination 적용 결과(totalCount) 를 응답(code>HttpServletResponse</code>) header 에 설정한다. <br/>
     *
     * 이 메소드 호출 이후 SQL 실행시 Pagination 기능을 1회 적용한다.
     * @param pageModel
     * @param keepAsResult
     * @return
     */
    public static Pagination of(PageModel pageModel, boolean keepAsResult) {
        Pagination.pageModel.set(pageModel);
        Pagination.keepAsResult.set(keepAsResult);
        return INSTANCE;
    }

    /**
     * 최초 dataPerPage 만큼의 데이터만 가져온다. <br/>
     *
     * 이 메소드 호출 이후 SQL 실행시 Pagination 기능을 1회 적용한다.
     * @param dataPerPage
     * @return
     */
    public static Pagination of(int dataPerPage) {
        PageModel qm = new PageModel();
        qm.setDataPerPage(dataPerPage);
        qm.setSkipCount(true);
        Pagination.pageModel.set(qm);
        Pagination.keepAsResult.set(false);
        return INSTANCE;
    }

    /**
     * dataPerPage 기준으로 currentPage 번째 페이지의 dataPerPage 만큼의 데이터만 가져온다. <br/>
     *
     * 이 메소드 호출 이후 SQL 실행시 Pagination 기능을 1회 적용한다.
     * @param currentPage
     * @param dataPerPage
     * @return
     */
    public static Pagination of(int currentPage, int dataPerPage) {
        PageModel qm = new PageModel();
        qm.setCurrentPage(currentPage);
        qm.setDataPerPage(dataPerPage);
        Pagination.pageModel.set(qm);
        Pagination.keepAsResult.set(false);
        return INSTANCE;
    }

    public static Pagination ofOrder(List<SortBy> sortByList) {
        return ofOrder(false, sortByList);
    }

    public static Pagination ofOrder(boolean usePaging, List<SortBy> sortByList) {
        if(usePaging) {
            if(Pagination.pageModel.get() == null) {
                Pagination.pageModel.set(new PageModel());
            }
        }
        SortModel qm = new SortModel();
        qm.setSortByList(sortByList);
        Pagination.sortModel.set(qm);
        return INSTANCE;
    }

    public static Pagination ofOrder(SortBy... sortBy) {
        return ofOrder(false, sortBy);
    }

    public static Pagination ofOrder(boolean usePaging, SortBy... sortBy) {
        if (usePaging) {
            if (Pagination.pageModel.get() == null) {
                Pagination.pageModel.set(new PageModel());
            }
        }
        SortModel qm = new SortModel();
        qm.setSortByList(Arrays.asList(sortBy));
        Pagination.sortModel.set(qm);
        return INSTANCE;
    }

    public static Pagination of(PageModel page, List<SortBy> sortByList) {
        Pagination.pageModel.set(page);
        SortModel sortModel = new SortModel();
        sortModel.setSortByList(sortByList);
        Pagination.sortModel.set(sortModel);
        return INSTANCE;
    }

    public static Pagination keepAsResult() {
        return keepAsResult(true);
    }

    public static Pagination keepAsResult(boolean keepAsResult) {
        Pagination.keepAsResult.set(keepAsResult);
        return INSTANCE;
    }

    public Pagination dataPerPage(int dataPerPage) {
        Pagination.pageModel.get().setDataPerPage(dataPerPage);
        return this;
    }

    public Pagination addOrderBy(SortBy... sortBy) {
        List<SortBy> sortByList = Pagination.sortModel.get().getSortByList();
        if(CollectionUtils.isEmpty(sortByList)) {
            sortByList = new ArrayList<>();
            Pagination.sortModel.get().setSortByList(sortByList);
        }
        Collections.addAll(sortByList, sortBy);
        return this;
    }

    public Pagination setOrderBy(SortBy... sortBy) {
        if(Pagination.sortModel.get() == null) {
            Pagination.sortModel.set(new SortModel());
        }
        Pagination.sortModel.get().setSortByList(Arrays.asList(sortBy));
        return this;
    }

    public Pagination resetOrderBy() {
        Pagination.sortModel.set(new SortModel());

        return this;
    }

    public Pagination setDefaultOrderBy(SortBy... sortBy) {
        if(!hasOrderBy()) {
            setOrderBy(sortBy);
        }
        return this;
    }

    private boolean hasOrderBy() {
        if(Pagination.sortModel.get() == null) {
            return false;
        }
        List<SortBy> sortByList = Pagination.sortModel.get().getSortByList();
        return CollectionUtils.isNotEmpty(sortByList);
    }
}
