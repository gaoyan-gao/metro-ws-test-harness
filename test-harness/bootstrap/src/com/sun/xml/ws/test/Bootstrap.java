package com.sun.xml.ws.test;

import org.codehaus.classworlds.ClassRealm;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Entry point for setting up the classworlds.
 *
 * <p>
 * To maximize the isolation and avoid test interference, test harness
 * could should not be put into the system classloader. The bootstrap module
 * is the small code that's loaded into the system classloader.
 *
 * <p>
 * It's only job is to find all the jars that consistute the harness,
 * and create a {@link ClassRealm}, then call into it.
 *
 * @author Kohsuke Kawaguchi
 */
public class Bootstrap {
    public static void main(String[] args) throws Exception {
        File home = getHomeDirectory();
        logger.fine("test harness home is "+home);

        // system properties are ugly but easy way to communicate values to the harness main code
        System.getProperties().put("HARNESS_HOME",home);

        // create the harness realm and put everything in there
        List<URL> harness = new ArrayList<URL>();
        // extension hook to add more libraries
        File extLib = new File(home,"lib");
        if(!extLib.exists())
            throw new IllegalStateException("No such directory: "+extLib);
        for (File jar : extLib.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        })) {
            logger.info("Adding "+jar+" to the harness realm");
            harness.add(jar.toURL());
        }

        // add harness-lib.jar. Do this at the end so that overrides can take precedence.
        File libJar = new File(home,"harness-lib.jar");
        harness.add(libJar.toURL());

        ClassLoader cl = new URLClassLoader(harness.toArray(new URL[0]), null);

        // call into the main method
        Class main = cl.loadClass("com.sun.xml.ws.test.Main");
        Method mainMethod = main.getMethod("main", String[].class);
        Thread.currentThread().setContextClassLoader(cl);
        mainMethod.invoke(null,new Object[]{args});
    }

    /**
     * Determines the 'home' directory of the test harness.
     * This is used to determine where to load other files.
     */
    private static File getHomeDirectory() throws IOException {
        String res = Bootstrap.class.getClassLoader().getResource("com/sun/xml/ws/test/Bootstrap.class").toExternalForm();
        if(res.startsWith("jar:")) {
            res = res.substring(4,res.lastIndexOf('!'));
            return new File(new URL(res).getFile()).getParentFile();
        }
        throw new IllegalStateException("I can't figure out where the harness is loaded from: "+res);
    }

    private static final Logger logger = Logger.getLogger(Bootstrap.class.getName());
}
