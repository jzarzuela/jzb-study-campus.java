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
package com.gargoylesoftware.htmlunit;

import java.io.IOException;
import java.io.Serializable;

import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

/**
 * The default implementation of PageCreator. Designed to be extended for easier
 * handling of new content types. Just check the content type in createPage()
 * and call super(createPage()) if your custom type isn't found. There are
 * also protected createXXXXPage() methods for creating the Page types HtmlUnit
 * already knows about for your custom content types.
 *
 * <p />
 * Following table shows the type of {@Page} created depending on the content type:<br>
 * <br>
 *  <table border="1" width="50%">
 *    <tr>
 *      <th>Content type</th>
 *      <th>Type of page</th>
 *    </tr>
 *    <tr>
 *      <td>text/html<br/>
 *      text/xhtml<br/>
 *      *xhtml+xml
 *      </td>
 *      <td>{@link HtmlPage}</td>
 *    </tr>
 *    <tr>
 *      <td>text/xml<br/>
 *      application/xml<br/>
 *      text/vnd.wap.wml<br/>
 *      *+xml
 *      </td>
 *      <td>{@link XmlPage}</td>
 *    </tr>
 *    <tr>
 *      <td>text/javascript<br/>
 *      application/x-javascript
 *      </td>
 *      <td>{@link JavaScriptPage}</td>
 *    </tr>
 *    <tr>
 *      <td>text/*</td>
 *      <td>{@link TextPage}</td>
 *    </tr>
 *    <tr>
 *      <td>Anything else</td>
 *      <td>{@link UnexpectedPage}</td>
 *    </tr>
 *  </table>
 *
 * @version $Revision: 2910 $
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author <a href="mailto:cse@dynabean.de">Christian Sell</a>
 * @author <a href="mailto:yourgod@users.sourceforge.net">Brad Clarke</a>
 * @author Marc Guillemot
 * @author Ahmed Ashour
 */
public class DefaultPageCreator implements PageCreator, Serializable  {

    private static final long serialVersionUID = -4420355214574495577L;

    /**
     * Create an instance.
     */
    public DefaultPageCreator() {
    }

    /**
     * Create a Page object for the specified web response.
     *
     * @param webResponse the response from the server
     * @param webWindow the window that this page will be loaded into
     * @exception IOException If an io problem occurs
     * @return the new page object
     */
    public Page createPage(
            final WebResponse webResponse,
            final WebWindow webWindow)
        throws
            IOException {
        final String contentType = webResponse.getContentType().toLowerCase();
        final Page newPage;

        final String pageType = determinePageType(contentType);
        if (pageType.equals("html")) {
            newPage = createHtmlPage(webResponse, webWindow);
        }
        else if (pageType.equals("javascript")) {
            newPage = createJavaScriptPage(webResponse, webWindow);
        }
        else if (pageType.equals("xml")) {
            newPage = createXmlPage(webResponse, webWindow);
        }
        else if (pageType.equals("text")) {
            newPage = createTextPage(webResponse, webWindow);
        }
        else {
            newPage = createUnexpectedPage(webResponse, webWindow);
        }
        return newPage;
    }

    /**
     * Creates an HtmlPage for this WebResponse.
     *
     * @param webResponse the page's source
     * @param webWindow the WebWindow to place the HtmlPage in
     * @return the newly created HtmlPage
     * @throws IOException if the page could not be created
     */
    protected HtmlPage createHtmlPage(final WebResponse webResponse, final WebWindow webWindow) throws IOException {
        final HtmlPage newPage;
        newPage = HTMLParser.parse(webResponse, webWindow);
        return newPage;
    }

    /**
     * Creates a JavaScriptPage for this WebResponse.
     *
     * @param webResponse the page's source
     * @param webWindow the WebWindow to place the JavaScriptPage in
     * @return the newly created JavaScriptPage
     */
    protected JavaScriptPage createJavaScriptPage(final WebResponse webResponse, final WebWindow webWindow) {
        final JavaScriptPage newPage;
        newPage = new JavaScriptPage(webResponse, webWindow);
        webWindow.setEnclosedPage(newPage);
        return newPage;
    }

    /**
     * Creates a TextPage for this WebResponse.
     *
     * @param webResponse the page's source
     * @param webWindow the WebWindow to place the TextPage in
     * @return the newly created TextPage
     */
    protected TextPage createTextPage(final WebResponse webResponse, final WebWindow webWindow) {
        final TextPage newPage;
        newPage = new TextPage(webResponse, webWindow);
        webWindow.setEnclosedPage(newPage);
        return newPage;
    }

    /**
     * Creates an UnexpectedPage for this WebResponse.
     *
     * @param webResponse the page's source
     * @param webWindow the WebWindow to place the UnexpectedPage in
     * @return the newly created UnexpectedPage
     */
    protected UnexpectedPage createUnexpectedPage(final WebResponse webResponse, final WebWindow webWindow) {
        final UnexpectedPage newPage;
        newPage = new UnexpectedPage(webResponse, webWindow);
        webWindow.setEnclosedPage(newPage);
        return newPage;
    }

    /**
     * Creates an XmlPage for this WebResponse.
     *
     * @param webResponse the page's source
     * @param webWindow the WebWindow to place the TextPage in
     * @return the newly created TextPage
     * @throws IOException if the page could not be created
     */
    protected XmlPage createXmlPage(final WebResponse webResponse, final WebWindow webWindow) throws IOException {
        final XmlPage newPage = new XmlPage(webResponse, webWindow);
        webWindow.setEnclosedPage(newPage);
        return newPage;
    }

    /**
     * Determines the kind of page to create from the content type.
     * @param contentType the content type to evaluate
     * @return "xml", "html", "javascript", "text" or "unknown"
     */
    protected String determinePageType(final String contentType) {
        if (contentType.equals("text/html") || contentType.equals("text/xhtml")) {
            return "html";
        }
        else if (contentType.endsWith("xhtml+xml")) {
            //Should create a validated XML document but for now just make what we can
            return "html";
        }
        else if (contentType.equals("text/javascript") || contentType.equals("application/x-javascript")) {
            return "javascript";
        }
        else if (contentType.equals("text/xml") || contentType.equals("application/xml")
                || contentType.matches(".*\\+xml")
                || contentType.equals("text/vnd.wap.wml")) {
            return "xml";
        }
        else if (contentType.startsWith("text/")) {
            return "text";
        }
        else {
            return "unknown";
        }
    }
}
