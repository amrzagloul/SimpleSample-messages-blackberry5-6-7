package com.injoit.examplepush.tvguide.controls;

import net.rim.device.api.system.Bitmap;

public class TVGuideListMenuObject {
        
        private Bitmap bitmap;
        private String name;
        
        public TVGuideListMenuObject(Bitmap bitmap, String name){
                this.bitmap = bitmap;
                this.name = name;
        }

        public Bitmap getBitmap() {
                return bitmap;
        }

        public String getName() {
                return name;
        }   
}
