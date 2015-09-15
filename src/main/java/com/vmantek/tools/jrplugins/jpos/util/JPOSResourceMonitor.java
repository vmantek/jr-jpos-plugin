package com.vmantek.tools.jrplugins.jpos.util;

import org.zeroturnaround.javarebel.integration.monitor.MonitoredResource;
import org.zeroturnaround.javarebel.integration.util.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class JPOSResourceMonitor
{
    public static MonitoredResource generate(String resource) throws IOException
    {
        URL url;
        if (resource.startsWith("jar:") && resource.length()>4)
        {
            ClassLoader cl=Thread.currentThread().getContextClassLoader();
            url = cl.getResource(resource.substring(4));
        }
        else
        {
            url = new File(resource).toURI().toURL();
        }
        return new MonitoredResource(ResourceUtil.asResource(url));
    }
}
