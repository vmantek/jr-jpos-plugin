package com.vmantek.tools.jrplugins.jpos.cbp;

import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtField;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.javarebel.Logger;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

public class ResourceReloadCBP extends JavassistClassBytecodeProcessor
{
    private static final Logger log = LoggerFactory.getLogger("jPOS");

    @Override
    public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception
    {
        cp.importPackage("java.util");
        cp.importPackage("com.vmantek.tools.jrplugins.jpos.util");

        ctClass.addField(CtField.make("public Map monitoredResources = new HashMap();", ctClass));

        CtMethod scanMethod = ctClass.getDeclaredMethod("scan");
        scanMethod.insertBefore("ResourceReloader.checkReload(monitoredResources);");
    }
}
