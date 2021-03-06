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

import com.gargoylesoftware.htmlunit.html.DomCharacterData;

/**
 * A JavaScript object for CharacterData.
 *
 * @version $Revision: 2829 $
 * @author David K. Taylor
 * @author Chris Erskine
 */
public class CharacterDataImpl extends Node {

    private static final long serialVersionUID = 5413850371617638797L;

    /**
     * Create an instance. JavaScript objects must have a default constructor.
     */
    public CharacterDataImpl() {
    }

    /**
     * Gets the JavaScript property "data" for this character data.
     * @return the String of data
     */
    public Object jsxGet_data() {
        final DomCharacterData domCharacterData =
            (DomCharacterData) getDomNodeOrDie();
        return domCharacterData.getData();
    }

    /**
     * Sets the JavaScript property "data" for this character data.
     * @param newValue the new String of data
     */
    public void jsxSet_data(final String newValue) {
        final DomCharacterData domCharacterData =
            (DomCharacterData) getDomNodeOrDie();
        domCharacterData.setData(newValue);
    }

    /**
     * Gets the number of character in the character data.
     * @return the number of characters
     */
    public int jsxGet_length() {
        final DomCharacterData domCharacterData =
            (DomCharacterData) getDomNodeOrDie();
        return domCharacterData.getLength();
    }

    /**
     * Append a string to character data.
     * @param arg the string to be appended to the character data
     */
    public void jsxFunction_appendData(final String arg) {
        final DomCharacterData domCharacterData =
            (DomCharacterData) getDomNodeOrDie();
        domCharacterData.appendData(arg);
    }

    /**
     * Delete characters from character data.
     * @param offset the position of the first character to be deleted
     * @param count the number of characters to be deleted
     */
    public void jsxFunction_deleteData(final int offset, final int count) {
        final DomCharacterData domCharacterData =
            (DomCharacterData) getDomNodeOrDie();
        domCharacterData.deleteData(offset, count);
    }

    /**
     * Insert a string into character data.
     * @param offset the position within the first character at which
     * the string is to be inserted.
     * @param arg the string to insert
     */
    public void jsxFunction_insertData(final int offset, final String arg) {
        final DomCharacterData domCharacterData =
            (DomCharacterData) getDomNodeOrDie();
        domCharacterData.insertData(offset, arg);
    }

    /**
     * Replace characters of character data with a string.
     * @param offset the position within the first character at which
     * the string is to be replaced.
     * @param count the number of characters to be replaced
     * @param arg the string that replaces the count characters beginning at
     * the character at offset.
     */
    public void jsxFunction_replaceData(final int offset, final int count,
        final String arg) {
        final DomCharacterData domCharacterData =
            (DomCharacterData) getDomNodeOrDie();
        domCharacterData.replaceData(offset, count, arg);
    }

    /**
     * Extract a substring from character data.
     * @param offset the position of the first character to be extracted
     * @param count the number of characters to be extracted
     * @return a string that consists of the count characters of the character
     *         data starting from the character at position offset
     */
    public String jsxFunction_substringData(final int offset,
        final int count) {
        final DomCharacterData domCharacterData =
            (DomCharacterData) getDomNodeOrDie();
        return domCharacterData.substringData(offset, count);
    }
}
