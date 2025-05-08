package com.czy.api.constant.search;

/**
 * @author 13225
 * @date 2025/4/4 23:36
 */
public class SearchConstant {

    public static final String serviceName = "search-service";
    public static final String serviceRoute = "/" + serviceName;
    public static final String MainSearch_CONTROLLER = "/mainSearch";

    public static final String serviceUri = "lb://" + serviceName;

    // 最少search结果数量；少于num就继续进入下一层
    public static final int SEARCH_MIN_NUM = 6;
    public static final int SEARCH_MIN_WORLDS = 2;
    public static final int SEARCH_MAX_WORLDS = 16;

    private static final int PYTHON_NLP_SEARCH_PORT = 60001;
    private static final String PYTHON_NLP_SEARCH_ROUTE = "http://127.0.0.1:" + PYTHON_NLP_SEARCH_PORT;
    public static final String PYTHON_NLP_SEARCH_URL = PYTHON_NLP_SEARCH_ROUTE + "/search/nlp";
}
