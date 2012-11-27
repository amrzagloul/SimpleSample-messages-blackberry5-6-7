package com.injoit.examplepush.utils;

import java.util.Vector;
import net.rim.device.api.ui.Font;

import com.injoit.examplepush.me.regexp.RE;

public class TextUtils {
        private static final String HTML_PATTERN = "(\\<.*?\\>)|(&nbsp;)|(&amp;)";    //"(</?\\w+\\s+[^>]*>)|(&nbsp;)|(&amp;)";     //"</?\\w+\\s+[^>]*>";       //"(\\<.*?\\>)|(&nbsp;)|(&amp;)";
        private static final String DATE_PATTERN = "/Date\\(([0-9]+)([\\+,-])([0-9]{2})([0-9]{2})\\)/";
    
        public static String cleanHtml(String string) {         
                RE re = new RE(HTML_PATTERN);
                String[] strings = re.split(string);
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < strings.length; i++) {
                        sb.append(strings[i]);
                }
                String out = "";
                try
                {
                    out =  new String(sb.toString().getBytes(),"UTF-8");
                }
                catch(Exception ex)
                {
                }
                return sb.toString();
        }
        
        public static long parseDate(String date) {
                try {
                        RE re = new RE(DATE_PATTERN);
                        re.match(date);
                        String paren = re.getParen(1);
                        return Long.parseLong(paren);
                } catch (Exception e) {
                        return 0;
                }
        }

        public static Vector wrapText(String text, int width, Font font) 
        {
            Vector result = new Vector();
                if (text == null)
                        return result;

                boolean hasMore = true;

                // The current index of the cursor
                int current = 0;

                // The next line break index
                int lineBreak = -1;

                // The space after line break
                int nextSpace = -1;

                while (hasMore) {
                        // Find the line break
                        while (true) {
                                lineBreak = nextSpace;
                                if (lineBreak == text.length() - 1) {
                                        // We have reached the last line
                                        hasMore = false;
                                        break;
                                } else {
                                        nextSpace = text.indexOf(' ', lineBreak + 1);
                                        if (nextSpace == -1)
                                                nextSpace = text.length() - 1;
                                        int linewidth = font.getAdvance(text, current, nextSpace
                                                        - current);
                                        // If too long, break out of the find loop
                                        if (linewidth > width)
                                                break;
                                }
                        }
                        String line = text.substring(current, lineBreak + 1);
                        result.addElement(line);
                        current = lineBreak + 1;
                }
                return result;
        }
     
        
        public static String replaceAll(String source, String pattern, String replacement)
        {    
            //If source is null then Stop
            //and retutn empty String.
            if (source == null)
            {
                return "";
            }
        
            StringBuffer sb = new StringBuffer();
            //Intialize Index to -1
            //to check agaist it later 
            int idx = -1;
            //Search source from 0 to first occurrence of pattern
            //Set Idx equal to index at which pattern is found.
        
            String workingSource = source;
            
            //Iterate for the Pattern till idx is not be -1.
            while ((idx = workingSource.indexOf(pattern)) != -1)
            {
                //append all the string in source till the pattern starts.
                sb.append(workingSource.substring(0, idx));
                //append replacement of the pattern.
                sb.append(replacement);
                //Append remaining string to the String Buffer.
                sb.append(workingSource.substring(idx + pattern.length()));
                
                //Store the updated String and check again.
                workingSource = sb.toString();
                
                //Reset the StringBuffer.
                sb.delete(0, sb.length());
            }
        
            return workingSource;
        }
        
        public static String reverse (String input) 
        {
            StringBuffer sb = new StringBuffer();
                
            //defensive coding in the event someone tries to use a null string
            //you could also just let an NPE trigger.  personal choice
            if (input==null) 
            {
                return null;
            }
              
            if (input.length()<2) 
            {
                return input;
            }
            
            //strings are zero-based (see referenced docs for details).
            //subtract 1 in the call to charAt, decrement to 'step back' 
            //down thru the string.
            for (int i=input.length(); i > 0 ; --i ) 
            {
                sb.append(input.charAt(i-1));
            }
              
            return sb.toString();
        }
}
