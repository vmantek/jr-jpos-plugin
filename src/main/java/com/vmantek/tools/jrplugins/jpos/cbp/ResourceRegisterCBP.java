package com.vmantek.tools.jrplugins.jpos.cbp;

import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.javarebel.Logger;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

public class ResourceRegisterCBP extends JavassistClassBytecodeProcessor
{
    private static final Logger log = LoggerFactory.getLogger("jPOS");

    @Override
    public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception
    {
        cp.importPackage("java.util");
        cp.importPackage("com.vmantek.tools.jrplugins.jpos.util");


        ctClass.getDeclaredMethod("installResource",
                                  new CtClass[]{cp.get("java.lang.String"),
                                                ctClass.booleanType})
        .insertAfter("ResourceReloader.registerResource(resource);");
    }
}
