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
        }
    }

    class EXCEPTION {
        public static final int BAD_REQUEST = 400;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
}
