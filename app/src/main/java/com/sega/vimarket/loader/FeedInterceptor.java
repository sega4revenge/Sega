package com.sega.vimarket.loader;

/**
 * Created by Sega on 07/01/2017.
 */


import android.content.Context;

import com.sega.vimarket.util.NetworkUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class FeedInterceptor{
    private final static String TAG = FeedInterceptor.class.getSimpleName();

    /**
     * Validate cache, return stream. Return cache if no network.
     * @param context
     * @return
     */
    public static Interceptor getOnlineInterceptor(final Context context){
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                String headers = response.header("Cache-Control");
                System.out.println(headers+"abc1");
                if(NetworkUtils.isConnected(context) &&
                        (headers == null || headers.contains("no-store") || headers.contains("must-revalidate") || headers.contains("no-cache") || headers.contains("max-age=0"))){


                    return response.newBuilder()
                            .header("Cache-Control", "public, max-age=600")
                            .build();
                } else{

                    return response;
                }
            }
        };

        return interceptor;
    }

    /**
     * Get me cache.
     * @param context
     * @return
     */
    public static Interceptor getOfflineInterceptor(final Context context){
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if(!NetworkUtils.isConnected(context)){
                    request = request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached")
                            .build();
                }

                return chain.proceed(request);
            }
        };

        return interceptor;
    }}