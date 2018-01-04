package me.divytiwari.deviceinformation;

import android.graphics.drawable.Drawable;

/**
 * Created by divya on 4/1/18.
 */

public class AppData {

    private String Title;
    private Drawable icon;

    public AppData(String title, Drawable icon) {
        Title = title;
        this.icon = icon;
    }

    public void setTitle(String App_title){
        this.Title = App_title;
    }

    public void setIcon(Drawable App_icon){
        this.icon = App_icon;
    }

    public String getTitle(){
        return Title;
    }

    public Drawable getIcon(){
        return icon;
    }

}
