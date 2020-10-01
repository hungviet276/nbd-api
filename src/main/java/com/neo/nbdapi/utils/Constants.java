package com.neo.nbdapi.utils;

public interface Constants {
    class APPLICATION_API {
        public static final String API_PREFIX = "/api/v1";

        public class MODULE {
            public static final String URI_LOGIN = "/authenticate";
        }
    }

    class EXCEPTION {
        public static final int BAD_REQUEST = 400;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
}
