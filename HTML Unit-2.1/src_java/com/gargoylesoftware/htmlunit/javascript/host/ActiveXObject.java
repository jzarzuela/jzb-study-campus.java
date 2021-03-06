/*
 * Copyright (c) 2002-2008 Gargoyle Software Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include the following acknowledgment:
 *
 *       "This product includes software developed by Gargoyle Software Inc.
 *        (http://www.GargoyleSoftware.com/)."
 *
 *    Alternately, this acknowledgment may appear in the software itself, if
 *    and wherever such third-party acknowledgments normally appear.
 * 4. The name "Gargoyle Software" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact info@GargoyleSoftware.com.
 * 5. Products derived from this software may not be called "HtmlUnit", nor may
 *    "HtmlUnit" appear in their name, without prior written permission of
 *    Gargoyle Software Inc.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GARGOYLE
 * SOFTWARE INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gargoylesoftware.htmlunit.javascript.host;

import java.lang.reflect.Method;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.javascript.SimpleScriptable;
import com.gargoylesoftware.htmlunit.javascript.configuration.ClassConfiguration;
import com.gargoylesoftware.htmlunit.javascript.configuration.JavaScriptConfiguration;

/**
 * This is the host object that allows JavaScript to instantiate java objects via the ActiveXObject
 * constructor. This host object enables a person to emulate ActiveXObjects in JavaScript with java
 * objects. See the <code>WebClient</code> class to see how ActiveXObject string parameter specifies
 * which java class is instantiated.
 *
 * @see com.gargoylesoftware.htmlunit.WebClient
 * @version $Revision: 2829 $
 * @author <a href="mailto:bcurren@esomnie.com">Ben Curren</a>
 * @author Ahmed Ashour
 */
public class ActiveXObject extends SimpleScriptable {
    private static final long serialVersionUID = 7327032075131452226L;

    /**
     * The default constructor.
     */
    public ActiveXObject() {
    }

    /**
     * This method searches the map specified in the <code>WebClient</code> class for the java
     * object to instantiate based on the ActiveXObject constructor String.
     * @param cx the current context
     * @param args the arguments to the ActiveXObject constructor
     * @param ctorObj the function object
     * @param inNewExpr Is new or not
     * @return the java object to allow JavaScript to access
     */
    public static Scriptable jsConstructor(
            final Context cx, final Object[] args, final Function ctorObj,
            final boolean inNewExpr) {
        if (args.length < 1 || args.length > 2) {
            throw Context.reportRuntimeError(
                    "ActiveXObject Error: constructor must have one or two String parameters.");
        }
        if (args[0] == Context.getUndefinedValue()) {
            throw Context.reportRuntimeError("ActiveXObject Error: constructor parameter is undefined.");
        }
        if (!(args[0] instanceof String)) {
            throw Context.reportRuntimeError("ActiveXObject Error: constructor parameter must be a String.");
        }
        final String activeXName = (String) args[0];

        // quick and dirty hack
        // the js configuration should probably be extended to allow to specify something like
        // <class name="XMLHttpRequest"
        //   classname="com.gargoylesoftware.htmlunit.javascript.host.XMLHttpRequest"
        //   activeX="Microsoft.XMLHTTP">
        // and to build the object from the config
        if (isXMLHttpRequest(activeXName)) {
            return buildXMLHttpRequest();
        }

        if (isXMLDocument(activeXName)) {
            return buildXMLDocument(getWindow(ctorObj).getWebWindow());
        }
        
        if (isXMLTemplate(activeXName)) {
            return buildXSLTemplate();
        }

        final Map<String, String> map = getWindow(ctorObj).getWebWindow().getWebClient().getActiveXObjectMap();
        if (map == null) {
            throw Context.reportRuntimeError("ActiveXObject Error: the map is null.");
        }
        final Object mapValue = map.get(activeXName);
        if (mapValue == null) {
            throw Context.reportRuntimeError("ActiveXObject Error: no value for " + activeXName + ".");
        }

        final String xClassString = (String) mapValue;
        Object object = null;
        try {
            final Class< ? > xClass = Class.forName(xClassString);
            object = xClass.newInstance();
        }
        catch (final Exception e) {
            throw Context.reportRuntimeError("ActiveXObject Error: failed instantiating class " + xClassString
                    + " because " + e.getMessage() + ".");
        }
        return Context.toObject(object, ctorObj);
    }

    /**
     * Indicates if the ActiveX name is one flavor of XMLHttpRequest
     * @param name the ActiveX name
     * @return <code>true</code> if this is an XMLHttpRequest
     */
    static boolean isXMLHttpRequest(String name) {
        if (name == null) {
            return false;
        }
        name = name.toLowerCase();
        return "Microsoft.XMLHTTP".equalsIgnoreCase(name) || name.startsWith("Msxml2.XMLHTTP".toLowerCase());
    }

    /**
     * Indicates if the ActiveX name is one flavor of XMLDocument
     * @param name the ActiveX name
     * @return <code>true</code> if this is an XMLDocument
     */
    static boolean isXMLDocument(String name) {
        if (name == null) {
            return false;
        }
        name = name.toLowerCase();
        return "Microsoft.XMLDOM".equalsIgnoreCase(name)
            || name.matches("msxml\\d*\\.domdocument.*")
            || name.matches("msxml\\d*\\.freethreadeddomdocument.*");
    }

    /**
     * Indicates if the ActiveX name is one flavor of XMLTemplate.
     * @param name the ActiveX name
     * @return <code>true</code> if this is an XMLTemplate
     */
    static boolean isXMLTemplate(String name) {
        if (name == null) {
            return false;
        }
        name = name.toLowerCase();
        return name.matches("msxml\\d*\\.xsltemplate.*");
    }

    private static Scriptable buildXMLHttpRequest() {
        final SimpleScriptable scriptable = new XMLHttpRequest();

        // the properties
        addProperty(scriptable, "onreadystatechange", true, true);
        addProperty(scriptable, "readyState", true, false);
        addProperty(scriptable, "responseText", true, false);
        addProperty(scriptable, "responseXML", true, false);
        addProperty(scriptable, "status", true, false);
        addProperty(scriptable, "statusText", true, false);

        // the functions
        addFunction(scriptable, "abort");
        addFunction(scriptable, "getAllResponseHeaders");
        addFunction(scriptable, "getResponseHeader");
        addFunction(scriptable, "open");
        addFunction(scriptable, "send");
        addFunction(scriptable, "setRequestHeader");

        return scriptable;
    }

    private static Scriptable buildXSLTemplate() {
        final SimpleScriptable scriptable = new XSLTemplate();

        addProperty(scriptable, "stylesheet", true, true);
        addFunction(scriptable, "createProcessor");

        return scriptable;
    }

    static XMLDocument buildXMLDocument(final WebWindow enclosingWindow) {
        final XMLDocument document = new XMLDocument(enclosingWindow);

        // the properties
        addProperty(document, "async", true, true);
        addProperty(document, "parseError", true, false);
        addProperty(document, "preserveWhiteSpace", true, true);
        addProperty(document, "xml", true, false);
        
        // the functions
        addFunction(document, "load");
        addFunction(document, "loadXML");
        addFunction(document, "selectNodes");
        addFunction(document, "selectSingleNode");
        addFunction(document, "setProperty");

        final JavaScriptConfiguration jsConfig =
            JavaScriptConfiguration.getInstance(BrowserVersion.INTERNET_EXPLORER_7_0);

        for (String className = "Document"; className.trim().length() != 0;) {
            final ClassConfiguration classConfig = jsConfig.getClassConfiguration(className);
            for (final String function : classConfig.functionKeys()) {
                addFunction(document, function);
            }
            for (final String property : classConfig.propertyKeys()) {
                addProperty(document, property,
                        classConfig.getPropertyReadMethod(property) != null,
                        classConfig.getPropertyWriteMethod(property) != null);
            }
            className = classConfig.getExtendedClass();

        }
        return document;
    }

    private static void addFunction(final SimpleScriptable scriptable,
            final String jsMethodName) {
        addFunction(scriptable, jsMethodName, "jsxFunction_" + jsMethodName);
    }
    
    private static void addFunction(final SimpleScriptable scriptable,
            final String jsMethodName, final String javaMethodName) {
        final Method javaFunction = getMethod(scriptable.getClass(), javaMethodName);
        final FunctionObject fo = new FunctionObject(null, javaFunction, scriptable);
        scriptable.defineProperty(jsMethodName, fo, READONLY);
    }

    static void addProperty(final SimpleScriptable scriptable, final String propertyName,
            final boolean isGetter, final boolean isSetter) {
        String getterName = null;
        if (isGetter) {
            getterName = "jsxGet_" + propertyName;
        }
        String setterName = null;
        if (isSetter) {
            setterName = "jsxSet_" + propertyName;
        }
        addProperty(scriptable, propertyName, getterName, setterName);
    }
    
    static void addProperty(final SimpleScriptable scriptable, final String propertyName,
            final String getterName, final String setterName) {
        scriptable.defineProperty(propertyName, null,
                getMethod(scriptable.getClass(), getterName),
                getMethod(scriptable.getClass(), setterName), PERMANENT);
    }

    /**
     * Gets the first method found of the class with the given name
     * @param clazz the class to search on
     * @param name the name of the searched method
     * @return <code>null</code> if not found
     */
    static Method getMethod(final Class < ? extends SimpleScriptable> clazz, final String name) {
        if (name == null) {
            return null;
        }
        for (final Method method : clazz.getMethods()) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    /**
     * Gets the name of the host object class.
     * @return the JavaScript class name
     */
    @Override
    public String getClassName() {
        return "ActiveXObject";
    }
}
