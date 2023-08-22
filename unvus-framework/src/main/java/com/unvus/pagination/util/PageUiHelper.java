package com.unvus.pagination.util;

import com.unvus.config.UnvusConstants;
import com.unvus.pagination.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class PageUiHelper {

	private int totalPageLinkCnt = -1;
	private int curPageScope = -1;
	private int startPage = -1;
	private int endPage = -1;

    private PageModel pageModel = null;

    private SortModel sortModel = null;

    private PageModel getPageModel() {
        if (pageModel == null) {
            pageModel = PaginationResult.pageModel.get();
        }
        return pageModel;
    }

    private SortModel getSortModel() {
        if (sortModel == null) {
            sortModel = PaginationResult.sortModel.get();
        }
        return sortModel;
    }

	public PageUiHelper() {
    }

	/**
	 * 현재 페이지
	 * @return 현재 페이지
	 */
	public int getCurrentPage() {
		return getPageModel().getCurrentPage();
	}

	/**
	 * 한 페이지당 데이타수
	 * @return 한 페이지당 데이타수
	 */
	public int getDataPerPage() {
		return getPageModel().getDataPerPage();
	}

	public List<SortBy> getOrderBy() {
	    return getSortModel().getSortByList();
    }


	/**
	 * 총 데이타수
	 * @return 총 데이타수
	 */
	public int getTotalCnt() {
		int totalPage = 0;
		try {
			totalPage = getPageModel().getTotalCnt();
		}catch(Exception ignore) {}

		return totalPage;
	}


	/**
	 * 한 페이지당 페이지링크수
	 * @return 한 페이지당 페이지링크수
	 */
	public int getPageLinkCnt() {
		return getPageModel().getLinkPerPage();
	}

	public int getPrevPage() {
		int prevPage = 1;
		int currentPage = getCurrentPage();
		int pageLinkCnt = getPageLinkCnt();
		if(currentPage > pageLinkCnt) {
			prevPage = currentPage - mod(currentPage, pageLinkCnt);
		}
		return prevPage;
	}

	public int getNextPage() {
		int nextPage = getTotalPageLinkCnt();
		int currentPage = getCurrentPage();
		int pageLinkCnt = getPageLinkCnt();
		if(getTotalPageLinkCnt() > getEndPage()) {
			nextPage = currentPage + (pageLinkCnt - mod(currentPage, pageLinkCnt)) + 1;
		}
		return nextPage;
	}

	public int getFromData() {
		return (getCurrentPage() - 1) * getDataPerPage() + 1;
	}


	public int getToData() {
		int toData = getCurrentPage() * getDataPerPage();
		if(getTotalCnt() < toData) {
			toData = getTotalCnt();
		}
		return toData;
	}

	/**
	 * 총 페이지 링크 수
	 * @return 총 페이지 링크 수
	 */
	public int getTotalPageLinkCnt() {
		if(totalPageLinkCnt == -1) {
			totalPageLinkCnt = (getTotalCnt() - 1) / getDataPerPage() + 1;
		}
		return totalPageLinkCnt;
	}

	public int getCurrentPageScope() {
		if(curPageScope == -1) {
			curPageScope = (getCurrentPage() - 1) / getPageLinkCnt() + 1;
		}
		return curPageScope;
	}

	/**
	 * 현재페이지 리스트의 시작 페이지 번호
	 * @return 현재페이지 리스트의 시작 페이지 번호
	 */
	public int getStartPage() {
		if(startPage == -1) {
			startPage = (getCurrentPageScope() - 1) * getPageLinkCnt() + 1;
		}
		return startPage;
	}

	/**
	 * 현재페이지 리스트의 끝 페이지 번호
	 * @return 현재페이지 리스트의 끝 페이지 번호
	 */
	public int getEndPage() {
		if(endPage == -1) {
			endPage = getCurrentPageScope() * getPageLinkCnt();
			if (endPage > getTotalPageLinkCnt()) {
				endPage = getTotalPageLinkCnt();
			}
		}
		return endPage;
	}


	/**
	 * 현재 페이지의 페이지 번호의 리스트
	 * @return 페이지번호 리스트
	 */
	public List<Integer> getPageList() {
		List<Integer> list = new ArrayList<Integer>();
		for(int idx = getStartPage(); idx <= getEndPage(); idx++ ) {
			list.add(idx);
		}
		return list;
	}


	/**
	 * 파라미터로 부터 query string 을 추출한다.
	 * @return 파라미터가 없을 경우에는 "?" 를 반환. * 파라미터가 있을 경우에는 "?" 로 시작해서 "&amp;" 로 끝나는 문자열을 반환
	 */
	public String getQueryString() {
		return getPageModel().getQueryString();
	}

	/**
	 * 페이징 할때 필요한 queryString 을 반환
	 *
	 * @return ex) currentPage=3&amp;dataPerPage=10
	 */
	public String getPageQuery() {
		StringBuffer sb = new StringBuffer();
		sb.append(UnvusConstants.CURRENT_PAGE).append("=").append(getCurrentPage());
		sb.append("&amp;").append(UnvusConstants.DATA_PER_PAGE).append("=").append(getDataPerPage());
		return sb.toString();
	}

	public String getOrderQuery() {

        List<SortBy> orderList = getOrderBy();
        if(CollectionUtils.isNotEmpty(orderList)) {
            StringBuffer sb = new StringBuffer();
            sb.append(UnvusConstants.SORT_BY).append("=");

            for(SortBy sortBy : orderList) {
                sb.append(sortBy.getSortKey()).append(":").append(sortBy.getSortValue()).append(",");
            }
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }
        return "";
	}

	/**
	 *
	 * @return	/mdbcms/song.cms?
	 */
	public String getPageLink() {
		StringBuffer sb = new StringBuffer();
		sb.append(getQueryString().substring(1)).append(getPageQuery()).append("&amp;").append(getOrderQuery());
		return sb.toString();
	}

    private int mod(int currentPage, int pageLinkCnt) {
        int mod = currentPage % pageLinkCnt;
        if(mod == 0) {
            return pageLinkCnt;
        }
        return mod;
    }

}
