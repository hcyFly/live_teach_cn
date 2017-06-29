package com.andruby.live.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by zhao on 2017/5/6.
 */

public class DialogUtil {

    public static void showComfirmDialog(Context context, String msg, DialogInterface.OnClickListener confirmListener,
                                         DialogInterface.OnClickListener cancelListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(msg);
        builder.setPositiveButton("确定", confirmListener);
        builder.setNegativeButton("取消", cancelListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showMsgDialog(Context context, String msg, DialogInterface.OnClickListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", confirmListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
