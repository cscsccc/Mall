package com.cs.util;

import java.net.URI;

public class MallUtils {
    public static URI getHost(URI uri){
        URI effectiveURI = null;
        try{
            System.out.println(uri.getScheme() + uri.getUserInfo() + uri.getHost() + uri.getPort() + "");
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        }catch (Throwable e){
            effectiveURI = null;
        }
        return effectiveURI;
    }
}
