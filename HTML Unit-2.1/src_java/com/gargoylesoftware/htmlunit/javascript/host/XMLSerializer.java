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

import java.util.Map;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.javascript.SimpleScriptable;
import com.gargoylesoftware.htmlunit.xml.XmlAttr;
import com.gargoylesoftware.htmlunit.xml.XmlElement;

/**
 * A JavaScript object for XMLSerializer.
 *
 * @version $Revision: 2836 $
 * @author Ahmed Ashour
 */
public class XMLSerializer extends SimpleScriptable {

    private static final long serialVersionUID = -934136191731299896L;

    /**
     * JavaScript constructor.
     */
    public void jsConstructor() {
        // Empty.
    }

    /**
     * The subtree rooted by the specified element is serialized to a string.
     * @param root the root of the subtree to be serialized (this may be any node, even a document)
     * @return the serialized string
     */
    public String jsxFunction_serializeToString(final Node root) {
        final StringBuilder buffer = new StringBuilder();
        final boolean isIE = getBrowserVersion().isIE();
        toXml(1, root.getDomNodeOrDie(), buffer, isIE);
        if (isIE) {
            buffer.append('\r').append('\n');
        }
        return buffer.toString();
    }

    private void toXml(final int indent, final DomNode node, final StringBuilder buffer, final boolean isIE) {
        buffer.append('<').append(node.getNodeName());
        if (node instanceof XmlElement) {
            final Map<String, XmlAttr> attributes = ((XmlElement) node).getAttributesMap();
            for (final String name : attributes.keySet()) {
                final XmlAttr attrib = attributes.get(name);
                buffer.append(' ').append(attrib.getQualifiedName()).append('=')
                    .append('"').append(attrib.getValue()).append('"');
            }
        }
        boolean startTagClosed = false;
        for (final DomNode child : node.getChildren()) {
            if (!startTagClosed) {
                buffer.append('>');
                startTagClosed = true;
            }
            switch (child.getNodeType()) {
                case org.w3c.dom.Node.ELEMENT_NODE:
                    toXml(indent + 1, child, buffer, isIE);
                    break;

                case org.w3c.dom.Node.TEXT_NODE:
                    final String value = child.getNodeValue();
                    if (isIE && value.trim().length() == 0) {
                        buffer.append('\r').append('\n');
                        final DomNode sibling = child.getNextSibling();
                        if (sibling != null && sibling.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            for (int i = 0; i < indent; i++) {
                                buffer.append('\t');
                            }
                        }
                    }
                    else {
                        buffer.append(child.getNodeValue());
                    }
                    break;

                default:
                        
            }
        }
        if (!startTagClosed) {
            buffer.append('/').append('>');
        }
        else {
            buffer.append('<').append('/').append(node.getNodeName()).append('>');
        }
    }

}
