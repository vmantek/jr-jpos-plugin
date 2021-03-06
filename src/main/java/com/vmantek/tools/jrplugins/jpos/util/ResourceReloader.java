package com.vmantek.tools.jrplugins.jpos.util;

import org.zeroturnaround.javarebel.Resource;
import org.zeroturnaround.javarebel.integration.monitor.MonitoredResource;
import org.zeroturnaround.javarebel.integration.util.ResourceUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ResourceReloader
{
    private static Map<String, MonitoredResource> monitoredResources=new HashMap<>();

    public static void unregisterResource(String entry)
    {
        monitoredResources.remove(entry);
    }

    public static void registerResource(String entry)
    {
        ClassLoader cl = ResourceReloader.class.getClassLoader();
        final Resource res = ResourceUtil.asResource(cl.getResource(entry));
        final MonitoredResource mr = new MonitoredResource(res, true);
        monitoredResources.put(entry, mr);
    }

    private static Method getDeployerMethod(String method,Class... args)
        throws ClassNotFoundException, NoSuchMethodException
    {
        Class cls=Class.forName("com.vmantek.jpos.deployer.ResourceDeployer");
        return cls.getDeclaredMethod(method,args);
    }

    private static Object getDeployer() throws Exception
    {
        return getDeployerMethod("getInstance").invoke(null);
    }

    public static void checkReload() throws Exception
    {
        try
        {
            Object deployer = getDeployer();
            List<String> entries = getAvailableResources(deployer);

            // Check for stale monitored resources
            final HashSet<String> clonedKeys = new HashSet<>(monitoredResources.keySet());
            clonedKeys.stream()
                .filter(s -> !entries.contains(s))
                .forEach(s -> {
                    monitoredResources.remove(s);
                    uninstallResource(deployer, s);
                });

            for (String entry : entries)
            {
                // Start monitoring new resources
                if (!monitoredResources.containsKey(entry))
                {
                    registerResource(entry);
                    installResource(deployer, entry);
                    continue;
                }

                // If a resource has been modified, reinstall to target location
                final MonitoredResource mr = monitoredResources.get(entry);
                if (mr.modified())
                {
                    installResource(deployer, entry);
                }
            }
        }
        catch (Exception e)
        {
            throw new IOException("Could not execute",e);
        }
    }

    private static void installResource(Object deployer,String s)
    {
        try
        {
            getDeployerMethod("installResource",String.class)
                .invoke(deployer,s);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could install resource",e);
        }
    }

    private static void uninstallResource(Object deployer,String s)
    {
        try
        {
            getDeployerMethod("uninstallResource",String.class)
                .invoke(deployer,s);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could uninstall resource",e);
        }
    }

    private static List<String> getAvailableResources(Object deployer)
    {
        try
        {
            return (List<String>) getDeployerMethod("getAvailableResources")
                .invoke(deployer);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not get resource list",e);
        }
    }
}
