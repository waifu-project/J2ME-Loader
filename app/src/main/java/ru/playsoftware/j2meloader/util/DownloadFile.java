package ru.playsoftware.j2meloader.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import ru.playsoftware.j2meloader.webx.WebViewActivity;


public class DownloadFile {

    /**
     * @param context used to check the device version and DownloadManager information
     * @return true if the download manager is available
     */
    public static boolean isDownloadManagerAvailable(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
    }

    public static boolean download(Context context, WebViewActivity.GameInfo gameInfo) {

        if (!DownloadFile.isDownloadManagerAvailable(context)) return false;

        String url = gameInfo.game_url;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        String name = gameInfo.getFileName();

        request.setDescription("j2me game");
        request.setTitle(name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long id = manager.enqueue(request);

        context.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                queryRequestParameters(context, intent);
            }

            private void queryRequestParameters(Context context, Intent intent) {

                try {
                    // get request bundle
                    Bundle extras = intent.getExtras();
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
                    Cursor c = ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).query(q);

                    //get request parameters
                    if (c.moveToFirst()) {
                        int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            String path = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                            Toast mytoast = Toast.makeText(context, "下载成功: " + path, Toast.LENGTH_LONG);
                            mytoast.show();
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        return true;
    }


}