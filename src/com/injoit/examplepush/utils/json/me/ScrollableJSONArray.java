package com.injoit.examplepush.utils.json.me;

public class ScrollableJSONArray {

        public ScrollableJSONArray(JSONTokener x, JSONParseListener listener) throws JSONException {
            if (x.nextClean() != '[') {
                        throw x.syntaxError("A JSONArray text must start with '['");
                }
                if (x.nextClean() == ']') {
                        return;
                }
                x.back();
                boolean enough = false;
                for (;;) {
                        if (x.nextClean() == ',') {
                                x.back();
                        } else {
                                x.back();
                                enough = listener.valueParsed(x.nextValue());
                        }
                        if (enough) {
                                return;
                        }
                        switch (x.nextClean()) {
                        case ';':
                        case ',':
                                if (x.nextClean() == ']') {
                                        return;
                                }
                                x.back();
                                break;
                        case ']':
                                return;
                        default:
                                throw x.syntaxError("Expected a ',' or ']'");
                        }
                }

        }

}
