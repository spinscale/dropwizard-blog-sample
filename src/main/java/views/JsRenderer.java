package views;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.collect.Lists;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;
import org.mozilla.javascript.tools.shell.Global;

public class JsRenderer {
    // TODO: Is this threadsafe? I have no clue yet :-)
    private static ScriptableObject globalScope;

    public static void init(String requireJsPath, List<String> javaScripts) {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(9);

            try {
                globalScope = context.initStandardObjects();
                String[] names = new String[]{"readFile"};
                globalScope.defineFunctionProperties(names, Global.class, ScriptableObject.DONTENUM);
                loadRequireJsScripts(context, globalScope, requireJsPath);

                int i = 0;
                for (String javaScripthPath : javaScripts) {
                    File file = new File(javaScripthPath);
                    InputStream is = new FileInputStream(file);
                    Reader inputStreamReader = new InputStreamReader(is, "UTF-8");
                    context.evaluateReader(globalScope, inputStreamReader, "init" + i++ + ".js", 0, null);
                }

                context.evaluateString(globalScope, "var moment = require('moment.min.js');", "init-moment-amd.js", 0, null);
            } finally {
                Context.exit();
            }
        } catch (IOException ex) {
            throw new RuntimeException(" ERROR : Unable to load engine resource: ", ex);
        }
    }

    // Copied out from the main method of rhino
    private static void loadRequireJsScripts(Context context, Scriptable globalScope, String requireJsPath) {
        RequireBuilder rb = new RequireBuilder();
        List<String> modulePath = Lists.newArrayList(requireJsPath);

        List<URI> uris = new ArrayList<URI>();
        if (modulePath != null) {
            for (String path : modulePath) {
                try {
                    URI uri = new URI(path);
                    if (!uri.isAbsolute()) {
                        // call resolve("") to canonify the path
                        uri = new File(path).toURI().resolve("");
                    }
                    if (!uri.toString().endsWith("/")) {
                        // make sure URI always terminates with slash to
                        // avoid loading from unintended locations
                        uri = new URI(uri + "/");
                    }
                    uris.add(uri);
                } catch (URISyntaxException usx) {
                    throw new RuntimeException(usx);
                }
            }
        }
        rb.setModuleScriptProvider(
                new SoftCachingModuleScriptProvider(
                        new UrlModuleSourceProvider(uris, null)));
        Require require = rb.createRequire(context, globalScope);
        require.install(globalScope);
    }

    public static String renderTemplate(String templateSource, Object jsonContext) {
        Context context = Context.enter();

        try {
            long id = System.currentTimeMillis();
            globalScope.put("templateSource"+id, globalScope, templateSource);
            globalScope.put("jsonContext"+id, globalScope, jsonContext);

            try {
                String js = String.format("(Handlebars.compile(templateSource%s))(JSON.parse(jsonContext%s))", id, id);
                return (String) context.evaluateString(globalScope, js, "HandlebarsCompiler", 0, null);
            } catch (JavaScriptException e) {
                // Fail hard on any compile time error for dust templates
                throw new RuntimeException(e);
            }
        } finally {
            Context.exit();
        }
    }
}