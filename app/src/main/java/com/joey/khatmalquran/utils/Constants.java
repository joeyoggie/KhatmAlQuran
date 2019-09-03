package com.joey.khatmalquran.utils;

public class Constants {
    public static final String PACKAGE_NAME = "com.joey.khatmalquran";


    //url constants
    private static final String DOMAIN_URl = "https://khatmalquran.com/api/v1";

    public static final String GET_VERSES_URL = DOMAIN_URl + "/verses";


    public static final String PARAMETER_NEXT_PAGE_URL = "next_page_url";

    public static final String PARAMETER_STATUS = "status";
    public static final String PARAMETER_VERSES = "verses";

    public static final String PARAMETER_STATUS_OK = "ok";

    //user item parameters
    public static final String PARAMETER_USER_ID = "id";
    public static final String PARAMETER_USER_NAME = "name";


    public static final String DB_NAME = "main-db";

    //notification channel constant, only for api26+
    public static final String CHANNEL_ID = "144";
    public static final String CHANNEL_ID_MUTE = "144_mute";
}
