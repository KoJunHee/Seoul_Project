package com.seoulprojet.seoulpoem.component;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by pyoinsoo on 2017-06-16.
 */

public class SeoulPoemGlideModule implements GlideModule {
    private static final int DEFAULT_DISK_CACHE_SIZE = 250 * 1024 * 1024;
    private static final int DEFAULT_MEMORY_CACHE_SIZE = 30 * 1024 * 1024;
    private static final int DEFAULT_BITMAP_POOL_SIZE = 30 * 1024 * 1024;
    private static final String DEFAULT_DISK_CACHE_DIR = "appmate_glide_cache_dir";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context,
                DEFAULT_DISK_CACHE_DIR, DEFAULT_DISK_CACHE_SIZE));
        builder.setMemoryCache(new LruResourceCache(DEFAULT_MEMORY_CACHE_SIZE));
        builder.setBitmapPool(new LruBitmapPool(DEFAULT_BITMAP_POOL_SIZE));
        builder.setDecodeFormat(DecodeFormat.DEFAULT);
    }
    @Override
    public void registerComponents(Context context, Glide glide) {
    }
}
