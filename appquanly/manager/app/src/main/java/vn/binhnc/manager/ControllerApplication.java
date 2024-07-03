package vn.binhnc.manager;

import android.app.Application;
import android.content.Context;


public class ControllerApplication extends Application {

    public static ControllerApplication get(Context context) {
        return (ControllerApplication) context.getApplicationContext();
    }

}