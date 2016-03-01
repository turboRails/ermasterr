package org.insightech.er.util;

import java.net.URL;
import java.net.URLClassLoader;

public class URLFirstClassLoader extends URLClassLoader {

    private final ClassLoader parentClassLoader;

    public URLFirstClassLoader(final URL[] paramArrayOfURL, final ClassLoader paramClassLoader) {
        super(paramArrayOfURL);

        parentClassLoader = paramClassLoader;
    }

    @Override
    public URL getResource(final String paramString) {
        URL url = super.getResource(paramString);

        if (url == null) {
            url = parentClassLoader.getResource(paramString);
        }

        return url;
    }

}
