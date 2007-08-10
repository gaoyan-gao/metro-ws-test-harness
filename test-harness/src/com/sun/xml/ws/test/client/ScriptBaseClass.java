package com.sun.xml.ws.test.client;

import bsh.Interpreter;
import com.sun.istack.NotNull;
import com.sun.xml.ws.test.container.DeploymentContext;
import com.sun.xml.ws.test.model.TestClient;
import com.sun.xml.ws.test.model.TestDescriptor;
import junit.framework.Assert;

import java.io.File;

/**
 * Client test script will be executed as if it's a method
 * on a sub-class of this class. IOW,
 *
 * <pre>
 * class Dummy extends {@link ScriptBaseClass} {
 *   void scriptMethod() {
 *     ... contents of the script ...
 *   }
 * }
 * </pre>
 *
 * <p>
 * Therefore all the public methods and fields are visible
 * to the script. This is a convenient place to define helper
 * convenience methods for scripts to use.
 *
 * <p>
 * Functions defined in <tt>util.bsh</tt> serves the same role.
 *
 * @author Kohsuke Kawaguchi
 */
public class ScriptBaseClass extends Assert {

    private final DeploymentContext context;
    private final TestClient client;
    private final Interpreter engine;

    public ScriptBaseClass(DeploymentContext context, Interpreter engine, TestClient client) {
        this.context = context;
        this.client = client;
        this.engine = engine;
    }

    /**
     * Loads a resource.
     *
     * @param name
     *      The resource name like "test.png" or "subdir1/subdir2/foo.xml"
     *
     * @see TestDescriptor#resources
     */
    public File resource(@NotNull String name) {
        return new File(context.descriptor.resources,name);
    }

    // more to come
}
