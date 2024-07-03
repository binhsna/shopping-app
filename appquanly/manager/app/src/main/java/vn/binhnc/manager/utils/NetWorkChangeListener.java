package vn.binhnc.manager.utils;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import vn.binhnc.manager.R;

public class NetWorkChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConnectedToInternet(context)) { //Internet is not Connected
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layout_dialog = LayoutInflater.from(context)
                    .inflate(R.layout.check_internet_dialog, null);
            builder.setView(layout_dialog);

            ImageButton cancelID = layout_dialog.findViewById(R.id.cancelID);
            Button btnRetry = layout_dialog.findViewById(R.id.btnRetry);

            //Show dialog
            AlertDialog dialog = builder.create();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            //dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);
            cancelID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });
        }
    }
}
