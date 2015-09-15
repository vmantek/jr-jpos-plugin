package com.vmantek.tools.jrplugins.jpos.util;

import org.zeroturnaround.javarebel.ClassEventListener;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.integration.util.ClassEventListenerUtil;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Set;

/**
 * A clas reload listener to redeploy a QBean if its main class
 * is modified.
 */
public class QBeanReloadListener implements ClassEventListener
{
    public void onClassEvent(int eventType, Class klass)
    {
        try
        {
            // Restart QBean if class is reloaded
            if (Class.forName("org.jpos.q2.QBean").isAssignableFrom(klass))
            {
                MBeanServer server = getMBeanServer();

                final ObjectName on = new ObjectName("Q2:type=qbean,service=*");
                Set<ObjectInstance> instances = server.queryMBeans(on, null);
                instances.stream()
                    .filter(instance -> instance.getClassName().equals(klass.getName()))
                    .forEach(instance -> restartQBean(server, instance));
            }
        }
        catch (Exception e)
        {
            LoggerFactory.getInstance().error(e);
        }
    }

    public int priority()
    {
        return 0;
    }

    private void restartQBean(MBeanServer server, ObjectInstance instance)
    {
        ObjectName oname = instance.getObjectName();
        try
        {
            server.invoke(oname, "stop", null, null);
            server.invoke(oname, "start", null, null);
        }
        catch (Exception e)
        {
            LoggerFactory.getInstance().error("Could not reload QBean: " +
                                              oname.getCanonicalName(), e);
        }
    }

    private MBeanServer getMBeanServer()
    {
        MBeanServer server;
        ArrayList mbeanServerList =
            MBeanServerFactory.findMBeanServer(null);
        if (mbeanServerList.isEmpty())
        {
            server = MBeanServerFactory.createMBeanServer("Q2");
        }
        else
        {
            server = (MBeanServer) mbeanServerList.get(0);
        }
        return server;
    }
}
