/*
 * Copyright 2018 Nathaniel Stickney
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package is.stma.beanpoll.test;

import is.stma.beanpoll.util.EMProducer;
import is.stma.beanpoll.util.HTTPUtility;
import is.stma.beanpoll.util.LogProducer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(Arquillian.class)
public class HTTPUtilityTest {

    @Deployment
    public static Archive<?> createTestArchive() {

        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeDependencies().resolve().withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "testDNSUtility.war")
                .addClasses(HTTPUtility.class, EMProducer.class, LogProducer.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("test-ds.xml") // Deploy test datasource
                .addAsLibraries(files); // Add necessary stuff from pom.xml
    }

    @Test
    public void testHTTPRequest() {
        String response = HTTPUtility.get("http://httpbin.org/", 80, 3);
        Assert.assertTrue(response.contains("BONUSPOINTS"));
    }

    @Test
    public void testHTTPWithoutProtocol() {
        String response = HTTPUtility.get("httpbin.org/", 80, 3);
        Assert.assertTrue(response.contains("BONUSPOINTS"));
    }

    @Test
    public void testHTTPSRequest() {
        String response = HTTPUtility.get("https://www.baylor.edu/", 443, 3);
        Assert.assertTrue(response.contains("Baylor"));
    }

    @Test
    public void testHTTPSSelfSigned() {
        String response = HTTPUtility.get("https://self-signed.badssl.com/", 443, 3);
        Assert.assertTrue(response.contains("self-signed"));
    }

    @Test
    public void testHTTPSBadCert() {
        String response = HTTPUtility.get("https://expired.badssl.com//", 443, 3);
        Assert.assertTrue(response.startsWith("ERROR"));
    }

    @Test
    public void testHTTPNoSuchPage() {
        String response = HTTPUtility.get("https://httpbin.org/", 80, 3);
        Assert.assertTrue(response.startsWith("ERROR"));
    }
}
