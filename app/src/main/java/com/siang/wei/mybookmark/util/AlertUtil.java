package com.siang.wei.mybookmark.util;

import android.widget.ProgressBar;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import com.siang.wei.mybookmark.R;


public class AlertUtil {

    /**
     * 顯示只有一個按鈕的提示對話框
     * @param message 要顯示的訊息
     * @param btnResId 按鈕文字資源id
     * @param btnClickListener 按鈕點擊事件listener
     * @param cancelable 點擊背景是否可取消
     */
    public static AlertDialog showAlertDialog(Context context, String message, int btnResId, DialogInterface.OnClickListener btnClickListener, boolean cancelable){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message)
                .setTitle(R.string.dialog_title)
                .setCancelable(cancelable);

        builder.setPositiveButton(btnResId, btnClickListener);

        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * 顯示只有一個按鈕的提示對話框
     * @param message 要顯示的訊息
     * @param positiveBtnResId positive按鈕文字資源id
     * @param negativeBtnResId negative按鈕文字資源id
     * @param positiveBtnClickListener positive按鈕點擊事件listener
     * @param negativeBtnClickListener negative按鈕點擊事件listener
     * @param cancelable 點擊背景是否可取消
     */
    public static AlertDialog showAlertDialog(Context context, String message, int positiveBtnResId, int negativeBtnResId, DialogInterface.OnClickListener positiveBtnClickListener, DialogInterface.OnClickListener negativeBtnClickListener, boolean cancelable){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(R.string.dialog_title)
                .setCancelable(cancelable);

        // On devices prior to Honeycomb, the button order (left to right) was POSITIVE - NEUTRAL - NEGATIVE.
        // On newer devices using the Holo theme, the button order (left to right) is now NEGATIVE - NEUTRAL - POSITIVE.
        builder.setPositiveButton(positiveBtnResId, positiveBtnClickListener);
        builder.setNegativeButton(negativeBtnResId, negativeBtnClickListener);

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }


    /**
     * 顯示提示對話框
     * @param message 傳回來的訊息
     */
    public static AlertDialog showAlertDialog(Context context, String message){
       return showAlertDialog(context, message,
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, false);
    }


    public static AlertDialog showProgressBar(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.dialog_progress)
                .setTitle(R.string.loading)
                .setCancelable(false);

        builder.setPositiveButton("停止", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();

       return dialog;
    }
}
