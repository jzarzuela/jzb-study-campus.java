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

import org.apache.commons.httpclient.NameValuePair;

/**
 * An element that can have it's values sent to the server during a form submit.
 *
 * @version $Revision: 2829 $
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author Daniel Gredler
 */
public interface SubmittableElement {

    /**
     * <p>Returns an array of {@link NameValuePair}s that are the values that will be sent
     * back to the server whenever this element's containing form is submitted.</p>
     *
     * <p>THIS METHOD IS INTENDED FOR THE USE OF THE FRAMEWORK ONLY AND SHOULD NOT
     * BE USED BY CONSUMERS OF HTMLUNIT. USE AT YOUR OWN RISK.</p>
     *
     * @return the values that will be sent back to the server whenever this element's
     *         containing form is submitted
     */
    NameValuePair[] getSubmitKeyValuePairs();

    /**
     * Returns the value of this element to the default value or checked state (usually what it was at
     * the time the page was loaded, unless it has been modified via JavaScript).
     */
    void reset();

    /**
     * Sets the default value to use when this element gets reset, if applicable.
     * @param defaultValue the default value to use when this element gets reset, if applicable
     */
    void setDefaultValue(final String defaultValue);

    /**
     * Returns the default value to use when this element gets reset, if applicable.
     * @return the default value to use when this element gets reset, if applicable
     */
    String getDefaultValue();

    /**
     * Sets the default checked state to use when this element gets reset, if applicable.
     * @param defaultChecked the default checked state to use when this element gets reset, if applicable
     */
    void setDefaultChecked(final boolean defaultChecked);

    /**
     * Returns the default checked state to use when this element gets reset, if applicable.
     * @return the default checked state to use when this element gets reset, if applicable
     */
    boolean isDefaultChecked();

}
