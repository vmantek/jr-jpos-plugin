package com.vmantek.tools.jrplugins.jpos;

import com.vmantek.tools.jrplugins.jpos.cbp.DBConfigCBP;
import com.vmantek.tools.jrplugins.jpos.cbp.GenericPackagerConfigCBP;
import com.vmantek.tools.jrplugins.jpos.cbp.ResourceRegisterCBP;
import com.vmantek.tools.jrplugins.jpos.cbp.ResourceReloadCBP;
import com.vmantek.tools.jrplugins.jpos.util.QBeanReloadListener;
import org.zeroturnaround.javarebel.ClassResourceSource;
import org.zeroturnaround.javarebel.Integration;
import org.zeroturnaround.javarebel.IntegrationFactory;
import org.zeroturnaround.javarebel.Plugin;
import org.zeroturnaround.javarebel.Reloader;
import org.zeroturnaround.javarebel.ReloaderFactory;

public class JPOSPlugin implements Plugin
{
    @Override
    public void preinit()
    {
        Integration integration = IntegrationFactory.getInstance();
        ClassLoader cl = getClass().getClassLoader();

        integration.addIntegrationProcessor(cl, "org.jpos.q2.Q2", new ResourceReloadCBP());
        integration.addIntegrationProcessor(cl, "com.vmantek.jpos.deployer.ResourceDeployer",
                                            new ResourceRegisterCBP());
        integration.addIntegrationProcessor(cl, "org.jpos.ee.DB", new DBConfigCBP());
        integration.addIntegrationProcessor(cl, "org.jpos.iso.packager.GenericPackager",
                                            new GenericPackagerConfigCBP());

        final Reloader reloader = ReloaderFactory.getInstance();
        reloader.addClassReloadListener(new QBeanReloadListener());
    }

    @Override
    public boolean checkDependencies(ClassLoader cl, ClassResourceSource crs)
    {
        return crs.getClassResource("org.jpos.q2.QBean") != null;
    }

    @Override
    public String getId()
    {
        return "jpos_plugin";
    }

    @Override
    public String getName()
    {
        return "jPOS Plugin";
    }

    @Override
    public String getDescription()
    {
        return "" +
               "<li>Reloading of jPOS QBeans</li>" +
               "<li>Reloading of generic packager config</li>" +
               "<li>Redeploy of internal resources</li>"
            ;
    }

    @Override
    public String getAuthor()
    {
        return null;
    }

    @Override
    public String getWebsite()
    {
        return null;
    }

    @Override
    public String getSupportedVersions()
    {
        return "2.0.+";
    }

    @Override
    public String getTestedVersions()
    {
        return "2.0.2";
    }

}
