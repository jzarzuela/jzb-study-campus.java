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
package com.gargoylesoftware.htmlunit.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringEscapeUtils;

import com.gargoylesoftware.htmlunit.Page;

/**
 * Wrapper for the HTML element "input".
 *
 * @version $Revision: 2905 $
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author David K. Taylor
 * @author <a href="mailto:cse@dynabean.de">Christian Sell</a>
 * @author Daniel Gredler
 * @author Ahmed Ashour
 */
public class HtmlSubmitInput extends HtmlInput {

    private static final long serialVersionUID = -615974535731910492L;

    /**
     * Value to use if no specified <tt>value</tt> attribute.
     */
    private static final String DEFAULT_VALUE = "Submit Query";
    
    /**
     * Creates an instance.
     *
     * @param namespaceURI the URI that identifies an XML namespace
     * @param qualifiedName the qualified name of the element type to instantiate
     * @param page the page that contains this element
     * @param attributes the initial attributes
     */
    HtmlSubmitInput(final String namespaceURI, final String qualifiedName, final HtmlPage page,
            final Map<String, HtmlAttr> attributes) {
        super(namespaceURI, qualifiedName, page, attributes);
        if (getPage().getWebClient().getBrowserVersion().isIE() && !isAttributeDefined("value")) {
            setAttributeValue("value", DEFAULT_VALUE);
        }
    }

    /**
     * This method will be called if there either wasn't an onclick handler or there was
     * but the result of that handler was true. This is the default behavior of clicking
     * the element. The default implementation returns the current page - subclasses
     * requiring different behavior (like {@link HtmlSubmitInput}) will override this
     * method.
     *
     * @param defaultPage the default page to return if the action does not
     * load a new page.
     * @return the page that is currently loaded after execution of this method
     * @throws IOException if an IO error occurred
     */
    @Override
    protected Page doClickAction(final Page defaultPage) throws IOException {
        final HtmlForm form = getEnclosingForm();
        if (form != null) {
            return form.submit(this);
        }
        return super.doClickAction(defaultPage);
    }

    /**
     * {@inheritDoc} This method <b>does nothing</b> for submit input elements.
     * @see SubmittableElement#reset()
     */
    @Override
    public void reset() {
        // Empty.
    }

    /**
     * {@inheritDoc} Returns "Submit Query" if <tt>value</tt> attribute is not defined.
     */
    @Override
    public String asText() {
        String text = super.asText();
        if (text == ATTRIBUTE_NOT_DEFINED) {
            text = DEFAULT_VALUE;
        }
        return text;
    }

    /**
     * {@inheritDoc} Doesn't print the attribute if it is <tt>value="Submit Query"</tt>.
     */
    @Override
    protected void printOpeningTagContentAsXml(final PrintWriter printWriter) {
        printWriter.print(getTagName());

        for (final HtmlAttr attribute : getAttributesCollection()) {
            if (!attribute.getNodeName().equals("value") || !attribute.getHtmlValue().equals(DEFAULT_VALUE)) {
                printWriter.print(" ");
                final String name = attribute.getNodeName();
                printWriter.print(name);
                printWriter.print("=\"");
                printWriter.print(StringEscapeUtils.escapeXml(attribute.getNodeValue()));
                printWriter.print("\"");
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * Returns "Submit Query" if <tt>name</tt> attribute is defined and <tt>value</tt> attribute is not defined.
     */
    @Override
    public NameValuePair[] getSubmitKeyValuePairs() {
        if (getNameAttribute().length() != 0 && !isAttributeDefined("value")) {
            return new NameValuePair[]{new NameValuePair(getNameAttribute(), DEFAULT_VALUE)};
        }
        return super.getSubmitKeyValuePairs();
    }
}
