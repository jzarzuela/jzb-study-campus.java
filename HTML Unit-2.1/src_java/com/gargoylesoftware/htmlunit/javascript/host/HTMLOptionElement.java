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

import org.xml.sax.helpers.AttributesImpl;

import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * The JavaScript object that represents an option.
 *
 * @version $Revision: 2829 $
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author David K. Taylor
 * @author Chris Erskine
 * @author Marc Guillemot
 * @author Ahmed Ashour
 */
public class HTMLOptionElement extends HTMLElement {
    private static final long serialVersionUID = 947015932373556314L;

    /**
     * Create an instance.
     */
    public HTMLOptionElement() {
    }

    /**
     * JavaScript constructor. This must be declared in every JavaScript file because
     * the rhino engine won't walk up the hierarchy looking for constructors.
     * @param newText the text
     * @param newValue the value
     * @param defaultSelected Whether the option is initially selected
     * @param selected the current selection state of the option
     */
    public void jsConstructor(final String newText, final String newValue,
            final boolean defaultSelected, final boolean selected) {
        final HtmlPage page = (HtmlPage) getWindow().getWebWindow().getEnclosedPage();
        AttributesImpl attributes = null;
        if (defaultSelected) {
            attributes = new AttributesImpl();
            attributes.addAttribute(null, "selected", "selected", null, "selected");
        }

        final HtmlOption htmlOption = (HtmlOption) HTMLParser.getFactory(HtmlOption.TAG_NAME).createElement(
                page, HtmlOption.TAG_NAME, attributes);
        htmlOption.setSelected(selected);
        setDomNode(htmlOption);

        if (newText != null && !newText.equals("undefined")) {
            htmlOption.appendChild(new DomText(page, newText));
        }
        if (newValue != null && !newValue.equals("undefined")) {
            htmlOption.setValueAttribute(newValue);
        }
    }

    /**
     * Returns the value of the "value" property.
     * @return the value property
     */
    public String jsxGet_value() {
        return getHtmlOption().getValueAttribute();
    }

    /**
     * Sets the value of the "value" property.
     * @param newValue the value property
     */
    public void jsxSet_value(final String newValue) {
        getHtmlOption().setValueAttribute(newValue);
    }

    /**
     * Returns the value of the "text" property.
     * @return the text property
     */
    public String jsxGet_text() {
        final HtmlOption htmlOption = getHtmlOption();
        if (htmlOption.isAttributeDefined("label")) {
            return htmlOption.getLabelAttribute();
        }
        return htmlOption.asText();
    }

    private HtmlOption getHtmlOption() {
        return (HtmlOption) getHtmlElementOrNull();
    }

    /**
     * Sets the value of the "text" property.
     * @param newText the text property
     */
    public void jsxSet_text(final String newText) {
        getHtmlOption().setLabelAttribute(newText);
    }

    /**
     * Returns the value of the "selected" property.
     * @return the text property
     */
    public boolean jsxGet_selected() {
        return getHtmlOption().isSelected();
    }

    /**
     * Sets the value of the "selected" property.
     * @param selected the new selected property
     */
    public void jsxSet_selected(final boolean selected) {
        getHtmlOption().setSelected(selected);
    }
    
    /**
     * Returns the value of the "defaultSelected" property.
     * @return the text property
     */
    public boolean jsxGet_defaultSelected() {
        return getHtmlOption().isDefaultSelected();
    }

    /**
     * Returns the value of the "label" property.
     * @return the label property
     */
    public String jsxGet_label() {
        return getHtmlOption().getLabelAttribute();
    }

    /**
     * Sets the value of the "label" property.
     * @param label the new label property
     */
    public void jsxSet_label(final String label) {
        getHtmlOption().setLabelAttribute(label);
    }
}
