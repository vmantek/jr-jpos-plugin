package com.vmantek.tools.jrplugins.jpos.cbp;

import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtField;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.bundled.javassist.NotFoundException;
import org.zeroturnaround.javarebel.Logger;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

public class DBConfigCBP extends JavassistClassBytecodeProcessor
{
    private static final Logger log = LoggerFactory.getLogger("jPOS");

    @Override
    public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception
    {
        final CtClass[] stringType = cp.get(new String[]{"java.lang.String"});

        CtMethod loadPropsMethod = null;
        try
        {
            loadPropsMethod = ctClass.getDeclaredMethod("loadProperties",
                                                        stringType);
        }
        catch (NotFoundException e)
        {
            log.errorEcho("-**-** jPOS-EE version is too old for DB enhancement support");
            return;
        }

        cp.importPackage("java.util");
        cp.importPackage("java.net.URL");
        cp.importPackage("com.vmantek.tools.jrplugins.jpos.util");
        cp.importPackage("org.zeroturnaround.javarebel.integration.util.ResourceUtil");
        cp.importPackage("org.zeroturnaround.javarebel.integration.monitor.MonitoredResource");

        ctClass.addField(CtField.make("public Map monitoredResources = new HashMap();", ctClass));
        ctClass.addField(CtField.make("private static MonitoredResource configFile;", ctClass));

        loadPropsMethod.insertBefore("configFile=JPOSResourceMonitor.generate(filename);");

        ctClass.getDeclaredMethod("getSessionFactory")
            .insertBefore(
                "{if(configFile!=null && configFile.modified()) " +
                "{" +
                "   invalidateSessionFactory();" +
                "}" +
                "Iterator i = monitoredResources.values().iterator();" +
                "while (i.hasNext())" +
                "{MonitoredResource resource = (MonitoredResource) i.next();" +
                "   if(resource.modified()) " +
                "   { " +
                "       invalidateSessionFactory(); " +
                "   } " +
                "}}"
            );

        ctClass.getDeclaredMethod("readMappingElements", stringType)
            .insertAfter("{" +
                         "final URL _url = getClass().getClassLoader().getResource(moduleConfig);"+
                         "if(_url!=null) {" +
                         "monitoredResources.put(moduleConfig," +
                         "new MonitoredResource(ResourceUtil.asResource(_url), true));}}"
            );
    }
}
