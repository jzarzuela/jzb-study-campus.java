/* Copyright 2004, Aetrion LLC.  All Rights Reserved. */

package com.aetrion.flickr;

import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.blogs.BlogsInterface;
import com.aetrion.flickr.util.IOUtilities;
import junit.framework.TestCase;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

/**
 * @author Anthony Eden
 */
public class BlogsInterfaceTest extends TestCase {

    Flickr flickr = null;

    public void setUp() throws ParserConfigurationException, IOException, FlickrException, SAXException {
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("/setup.properties");
            Properties properties = new Properties();
            properties.load(in);

            REST rest = new REST();

            flickr = new Flickr(
                properties.getProperty("apiKey"),
                properties.getProperty("secret"),
                rest
            );

            RequestContext requestContext = RequestContext.getRequestContext();

            AuthInterface authInterface = flickr.getAuthInterface();
            Auth auth = authInterface.checkToken(properties.getProperty("token"));
            requestContext.setAuth(auth);
        } finally {
            IOUtilities.close(in);
        }
    }

    public void testGetList() throws FlickrException, IOException, SAXException {
        BlogsInterface blogsInterface = flickr.getBlogsInterface();
        Collection blogs = blogsInterface.getList();
        assertNotNull(blogs);
        assertEquals(1, blogs.size());
    }

    public void testPostImage() {
        // TODO: implement this test
    }

}
