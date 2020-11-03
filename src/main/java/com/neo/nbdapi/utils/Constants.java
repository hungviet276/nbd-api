package com.neo.nbdapi.utils;

public interface Constants {
    class APPLICATION_API {
        public static final String API_PREFIX = "/api/v1";

        public class MODULE {
            public static final String URI_LOGIN = "/authenticate";
            public static final String URI_USER_INFO = "/user-info";
            public static final String URI_MAIL_CONFIG = "/mail-config";
            public static final String URI_GROUP_MAIL_RECEIVE = "/group-mail-receive";
            public static final String URI_GROUP_MAIL_RECEIVE_DETAIL = "/group-mail-receive-detail";
            public static final String URI_MENU_MANAGE = "/menu-manage";
            public static final String URI_LOG_ACT = "/log-act";
            public static final String URI_USER_MANAGER = "/user-manager";
            public static final String URI_STATION = "/station";
            public static final String URI_VALUE_TYPES = "/value-type";
            public static final String URI_CONFIG_VALUE_TYPES = "/config-value-type";
            public static final String URI_CONFIG_WARNING_THRESOLD = "/warning-thresold";
            public static final String URI_CHANGER_PASS = "/changer-pass";
            public static final String URI_CONFIG_WARNING_THRESHOLD_STATION = "/warning-threshold-station";
            public static final String URI_MANAGER_OUTPUTS = "/management-of-outputs";
        }
    }

    class EXCEPTION {
        public static final int BAD_REQUEST = 400;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    class LOGGER {
        public static final String MAKER_LOG_STATION = "LOG_STATION";
        public static final String MAKER_LOG_ACTION_CRUD = "LOG_ACTION_CRUD";
        public static final String MAKER_LOG_DEBUG= "LOG_DEBUG";
    }

    class ConstantParams{
        public static final String SPLIT_CHARACTER = ",";
        public static final String SPLIT_FILE_CHARACTER = "\\.";

        public static final String ENCODE_UTF8 = "UTF-8";
        public static final String ERROR_VIEW_NAME = "error";
        public static final int DEFAULT_COOKIE_EXPIRE_DATE = 1 * 24 * 60 * 60;
        public static final int INITIAL_PAGE = 0;
        public static int PAGE_SIZE = 10;
        public static int BUTTONS_TO_SHOW = 5;
        public static int[] PAGE_SIZES = { 5, 10, 20 };

        public static String SECRET_KEY_PATH = "ZDf+fWqwFNUPelJpn87uZQ==";

         public static String sqlFile = "sql.properties";
//        public static String sqlFile = "static/sql.properties";
        public static String LOG_CONFIG_FILE = "log4j.properties";
//        public static String LOG_CONFIG_FILE = "static/log4j.properties";
        //ConstantParams.class.getClassLoader().getResource("log4j.properties").getPath();
        // public static String sqlFile = "sql.properties";
        public static final int REFRESH_DELAY = 2 * 1000;
        //	public static final int REFRESH_DELAY = 1000;
    }

    class LOG_ACT {
        public static final String FILE_NAME_EXPORT_LOG_ACT = "log_act";
    }
}
