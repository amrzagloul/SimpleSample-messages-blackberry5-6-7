package com.injoit.examplepush.utils;

public interface Constants {
        
        //TEST SERVER
        public final String PUSH_APP_ID = "911";                  // add here your Application id
        public final String PUSH_AUTH_KEY = "5uJ2GDeMVbEa47W";    // add here your Authorization key
        public final String PUSH_AUTH_SECRET = "6XWkNFHejzaRxL4"; // add here your Authorization secret
        public final String PUSH_SERVER_API = "http://api.quickblox.com/";
        
        public final String SERVER = "chat.quickblox.com";
        public final String API_SERVER = "http://api.quickblox.com/";
        
        public final String APP_ID = "700";                  // add here your Application id
        public final String AUTH_KEY = "daAWOEuh49tJhHO";    // add here your Authorization key
        public final String AUTH_SECRET = "tp5kpbYdZjfdN6J"; // add here your Authorization secret
        
        public final String PORT = "5222";
    
        public final static String SERVER_IP = "http://197.80.203.118";
        public final static String SERVER_IMAGES = "http://cdn.dstv.com/www.dstv.com/";                        // highlights & billboards
        public final static String SERVER_LOGO_IMAGES = "http://cdn.dstv.com/www.dstv.com/DStvChannels/";      //channel's logos
        public final static String API = "/api/";
        //http://cms.multichoice.co.za/ContentImages/DStvChannels/
        
        /*
         * Error codes
         */
        public final static int WRONG_API_KEY_COMBINATION = 100;
        public final static int WRONG_USER_ID_COMBINATION = 101;
        public final static int ITEM_REQUIRED_COUNTRYCODE = 102;
        public final static int ITEM_REQUIRES_A_COUNTRY_CODE = 103;
        
        /*
         * Requests
         */
        public final static String GET_NEWS = "getNews";
        public final static String GET_RELATED_NEWS = "getRelatedNews";
        public final static String GET_NEWS_ITEM = "getNewsItem";
        
        public final static String GET_HIGHLIGHTS = "gethighlights";
        public final static String GET_HIGHLIGHTS_ITEM = "gethighlightsitem";
        
        public final static String GET_BILLBOARDS = "getbillboards";
        public final static String GET_BILLBOARDS_ID = "getbillboards";
        
        public final String GET_EVENT_BY_CHANNEL_URL = "http://197.80.203.118/EPGRestService/api/json/geteventsforchannels?apikey=bda11d91-7ade-4da1-855d-24adfe39d174&u=3fb11b9b-6aea-475c-b149-26dd1224b390";
        public final String GET_COUNTRIES_URL = "http://197.80.203.118/EPGRestService/api/json/getcountries?apikey=bda11d91-7ade-4da1-855d-24adfe39d174";
        public final String GET_BOUQUETS_URL  = "http://197.80.203.118/EPGRestService/api/json/getbouquets?apikey=bda11d91-7ade-4da1-855d-24adfe39d174";
        public final String GET_CHANELS_BY_PRODUCT_URL  = "http://197.80.203.118/EPGRestService/api/json/getChannelsByProduct?apikey=bda11d91-7ade-4da1-855d-24adfe39d174&u=3fb11b9b-6aea-475c-b149-26dd1224b390";
        public final String GET_GENRES_URL  = "http://197.80.203.118/EPGRestService/api/json/getgenres?apikey=bda11d91-7ade-4da1-855d-24adfe39d174";
        public final String GET_SEARCH_RESULTS_URL  = "http://197.80.203.118/EPGRestService/api/json/getsearchresults?apikey=bda11d91-7ade-4da1-855d-24adfe39d174&u=3fb11b9b-6aea-475c-b149-26dd1224b390";
        
        public final static int GET_COUNTRY = 0;
        public final static int GET_BOUQUET = 1;
        public final static int GET_CHANNELS_BY_PRODUCT = 2;
        public final static int GET_GENRES = 3;
        public final static int GET_EVENTS_BY_CHANNEL = 4;
        public final static int GET_SEARCH_RESULTS = 5;
}
