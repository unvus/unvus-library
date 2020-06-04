package com.unvus.config;

/**
 * UnvUS constants.
 */
public interface UnvusConstants {

	// Spring profiles for development, test and production, see https://www.jhipster.tech/profiles/
	String SPRING_PROFILE_LOCAL = "local";
	String SPRING_PROFILE_DEVELOPMENT = "dev";
	String SPRING_PROFILE_PRODUCTION = "prod";

	// pagination
	String SKIP_PAGING = "SKIP_PAGING";

	String SKIP_COUNT = "skipCount";

	String CONDITION_PARAM_KEY = "q.";

	String CURRENT_PAGE = "currentPage";

	String DATA_PER_PAGE = "dataPerPage";

	String LINK_PER_PAGE = "linkPerPage";

	String ORDER_BY = "orderBy";

	String ORDER_BY_LIST = "orderByList";

	Integer DEFAULT_DATA_PER_PAGE = 15;

	Integer DEFAULT_PAGE_LINK_COUNT = 10;

	String PAGER_TOOL = "pager";
}
