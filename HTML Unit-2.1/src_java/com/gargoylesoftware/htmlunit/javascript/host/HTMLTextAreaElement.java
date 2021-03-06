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

import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

/**
 * The JavaScript object that represents a textarea.
 *
 * @version $Revision: 2829 $
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author Marc Guillemot
 * @author Chris Erskine
 * @author Ahmed Ashour
 */
public class HTMLTextAreaElement extends FormField {

    private static final long serialVersionUID = 49352135575074390L;

    /**
     * Create an instance.
     */
    public HTMLTextAreaElement() {
    }

    /**
     * JavaScript constructor. This must be declared in every JavaScript file because
     * the rhino engine won't walk up the hierarchy looking for constructors.
     */
    public void jsConstructor() {
    }

    /**
     * Returns the type of this input.
     * @return the type
     */
    @Override
    public String jsxGet_type() {
        return "textarea";
    }

    /**
     * Returns the value of the "value" attribute.
     * @return the value of the "value" attribute
     */
    @Override
    public String jsxGet_value() {
        return ((HtmlTextArea) getHtmlElementOrDie()).getText();
    }

    /**
     * Sets the value of the "value" attribute.
     * @param value the new value
     */
    @Override
    public void jsxSet_value(final String value) {
        ((HtmlTextArea) getHtmlElementOrDie()).setText(value);
    }

    /**
     * Returns the textarea's default value, used if the containing form gets reset.
     * @return the textarea's default value, used if the containing form gets reset
     * @see <a href="http://msdn.microsoft.com/workshop/author/dhtml/reference/properties/defaultvalue.asp">
     * MSDN Documentation</a>
     */
    public String jsxGet_defaultValue() {
        return ((HtmlTextArea) getHtmlElementOrDie()).getDefaultValue();
    }

    /**
     * Sets the textarea's default value, used if the containing form gets reset.
     * @param defaultValue the textarea's default value, used if the containing form gets reset
     * @see <a href="http://msdn.microsoft.com/workshop/author/dhtml/reference/properties/defaultvalue.asp">
     * MSDN Documentation</a>
     */
    public void jsxSet_defaultValue(final String defaultValue) {
        ((HtmlTextArea) getHtmlElementOrDie()).setDefaultValue(defaultValue);
    }

    /**
     * Gets the value of "textLength" attribute.
     * @return the text length
     */
    public int jsxGet_textLength() {
        return jsxGet_value().length();
    }

    /**
     * Gets the value of "selectionStart" attribute.
     * @return the selection start
     */
    public int jsxGet_selectionStart() {
        return ((HtmlTextArea) getHtmlElementOrDie()).getSelectionStart();
    }

    /**
     * Sets the value of "selectionStart" attribute.
     * @param start selection start
     */
    public void jsxSet_selectionStart(final int start) {
        ((HtmlTextArea) getHtmlElementOrDie()).setSelectionStart(start);
    }

    /**
     * Gets the value of "selectionEnd" attribute.
     * @return the selection end
     */
    public int jsxGet_selectionEnd() {
        return ((HtmlTextArea) getHtmlElementOrDie()).getSelectionEnd();
    }

    /**
     * Sets the value of "selectionEnd" attribute.
     * @param end selection end
     */
    public void jsxSet_selectionEnd(final int end) {
        ((HtmlTextArea) getHtmlElementOrDie()).setSelectionEnd(end);
    }

    /**
     * Selects this element.
     */
    public void jsxFunction_select() {
        ((HtmlTextArea) getDomNodeOrDie()).select();
    }

}
