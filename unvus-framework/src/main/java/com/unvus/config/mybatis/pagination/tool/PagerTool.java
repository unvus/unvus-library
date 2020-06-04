package com.unvus.config.mybatis.pagination.tool;

import com.unvus.config.UnvusConstants;
import com.unvus.config.mybatis.pagination.OrderBy;

import com.unvus.config.mybatis.pagination.Pagination;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class PagerTool {

	int totalPageLinkCnt = -1;
	int curPageScope = -1;
	int startPage = -1;
	int endPage = -1;

	/**
	 * 현재 페이지
	 * @return 현재 페이지
	 */
	public int getCurrentPage() {
		return Pagination.currentPage.get();
	}

	/**
	 * 한 페이지당 데이타수
	 * @return 한 페이지당 데이타수
	 */
	public int getDataPerPage() {
		return Pagination.dataPerPage.get();
	}

	public List<OrderBy> getOrderBy() {
	    return Pagination.orderBy.get();
    }


	/**
	 * 총 데이타수
	 * @return 총 데이타수
	 */
	public int getTotalCnt() {
		int totalPage = 0;
		try {
			totalPage = Pagination.totalCnt.get();
		}catch(Exception ignore) {}

		return totalPage;
	}


	/**
	 * 한 페이지당 페이지링크수
	 * @return 한 페이지당 페이지링크수
	 */
	public int getPageLinkCnt() {
		return Pagination.linkPerPage.get();
	}

	public int getPrevPage() {
		int prevPage = 1;
		int currentPage = getCurrentPage();
		int pageLinkCnt = getPageLinkCnt();
		if(currentPage > pageLinkCnt) {
			prevPage = currentPage - (pageLinkCnt + (currentPage % pageLinkCnt)) + 1;
		}
		return prevPage;
	}

	public int getNextPage() {
		int nextPage = getTotalPageLinkCnt();
		int currentPage = getCurrentPage();
		int pageLinkCnt = getPageLinkCnt();
		if(getTotalPageLinkCnt() > getEndPage()) {
			nextPage = currentPage + (pageLinkCnt - (currentPage % pageLinkCnt)) + 1;
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
		return Pagination.queryString.get();
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

        List<OrderBy> orderList = getOrderBy();
        if(CollectionUtils.isNotEmpty(orderList)) {
            StringBuffer sb = new StringBuffer();
            sb.append(UnvusConstants.ORDER_BY).append("=");

            for(OrderBy orderBy : orderList) {
                sb.append(orderBy.getOrderKey()).append(":").append(orderBy.getOrderValue()).append(",");
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
}
