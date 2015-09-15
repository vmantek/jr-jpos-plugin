package com.vmantek.tools.jrplugins.jpos.cbp;

import org.zeroturnaround.bundled.javassist.CannotCompileException;
import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtField;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.bundled.javassist.CtNewMethod;
import org.zeroturnaround.javarebel.Logger;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

public class GenericPackagerConfigCBP extends JavassistClassBytecodeProcessor
{
    private static final Logger log = LoggerFactory.getLogger("jPOS");

    @Override
    public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception
    {
        cp.importPackage("com.vmantek.tools.jrplugins.jpos.util");
        cp.importPackage("org.zeroturnaround.javarebel.integration.util.ResourceUtil");
        cp.importPackage("org.zeroturnaround.javarebel.integration.monitor.MonitoredResource");

        ctClass.addField(CtField.make("private MonitoredResource configFile;", ctClass));

        CtMethod readFileMethod = ctClass.getDeclaredMethod("readFile",
                                                            cp.get(new String[]{"java.lang.String"}));

        readFileMethod.insertBefore("configFile=JPOSResourceMonitor.generate(filename);");

        CtClass sc = ctClass.getSuperclass();

        addReloadCode(ctClass, sc.getDeclaredMethod("pack",
                                                    cp.get(new String[]{"org.jpos.iso.ISOComponent"})));

        addReloadCode(ctClass, sc.getDeclaredMethod("unpack",
                                                    cp.get(new String[]{"org.jpos.iso.ISOComponent", "byte[]"})));

        addReloadCode(ctClass, sc.getDeclaredMethod("unpack",
                                                    cp.get(new String[]{"org.jpos.iso.ISOComponent", "java.io.InputStream"})));
    }

    private void addReloadCode(CtClass cls, CtMethod m) throws CannotCompileException
    {
        final CtMethod method = CtNewMethod.delegator(m, cls);
        method.insertBefore(
            "if(configFile!=null && configFile.modified()) {" +
            "readFile(filename);}");
        cls.addMethod(method);
    }
}
