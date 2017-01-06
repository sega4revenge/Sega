package com.sega.vimarket.loader;

import android.content.Context;

import java.io.File;

class FileCache {

    private File cacheDir;

    FileCache(Context context) {
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "LazyList");
        }
        else {
            cacheDir = context.getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    File getFile(String url) {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename = String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        return new File(cacheDir, filename);

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files)
            f.delete();
    }

}