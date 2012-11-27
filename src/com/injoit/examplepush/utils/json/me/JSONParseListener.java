package com.injoit.examplepush.utils.json.me;

public interface JSONParseListener {        
        // returns true if enough to fetch
        public boolean valueParsed(Object object) throws JSONException;
}
