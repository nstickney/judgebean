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

import is.stma.beanpoll.poller.AbstractPoller;
import is.stma.beanpoll.poller.PollerFactory;
import is.stma.beanpoll.data.PollRepo;
import is.stma.beanpoll.model.*;
import is.stma.beanpoll.rules.PollRules;
import is.stma.beanpoll.service.*;
import is.stma.beanpoll.service.parameterizer.DNSParameterizer;
import is.stma.beanpoll.util.EMProducer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(Arquillian.class)
public class DNSPollerTest {

    @Inject
    private ContestService contestService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private TeamService teamService;

    private Contest testContest;

    private Poll testPoll;

    private Team testTeam;
    private Team checkTeam;

    private Resource testResource;

    @Deployment
    public static Archive<?> createTestArchive() {

        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeDependencies().resolve().withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "HTTPPollerTest.war")
                .addPackages(true, Poll.class.getPackage(),
                        PollRepo.class.getPackage(),
                        PollService.class.getPackage(),
                        PollRules.class.getPackage(),
                        DNSParameterizer.class.getPackage(),
                        AbstractPoller.class.getPackage(),
                        EMProducer.class.getPackage())
                .addClass(TestUtility.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/apache-deltaspike.properties")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("test-ds.xml") // Deploy test datasource
                .addAsLibraries(files); // Add necessary stuff from pom.xml
    }

    @Before
    public void setUp() {
        if (null == testContest) {
            testContest = TestUtility.makeContest();
            testContest = contestService.create(testContest);
            List<Team> teams = new ArrayList<>();
            if (null == testTeam) {
                testTeam = TestUtility.makeTeam(testContest);
                testTeam.setFlag("httpbin.org.herokudns.com");
                testTeam = teamService.create(testTeam);
                teams.add(testTeam);
            }
            if (null == checkTeam) {
                checkTeam = TestUtility.makeTeam(testContest);
                checkTeam.setFlag("adobe-idp-site-verification=e7f6f050-70cd-41a6-8ace-2ca8dcabbd26");
                checkTeam = teamService.create(checkTeam);
                teams.add(checkTeam);
            }
            testContest.setTeams(teams);
            testContest = contestService.update(testContest);
        }
        if (null == testResource) {
            testResource = TestUtility.makeResource(testContest, ResourceType.DNS);
            testResource.setAddress("9.9.9.9");
            testResource.setPort(53);
            testResource = resourceService.create(testResource);
        }
    }

    @Test
    public void testPollWorks() {
        AbstractPoller poller = PollerFactory.getPoller(testResource);
        testPoll = poller.poll();
        Assert.assertTrue(testPoll.getResults().contains("129.62.3.230"));
        Assert.assertEquals(testPoll.getTeam(), checkTeam);
    }

    @Test
    public void testNoTeamGetsPoints() {
        Resource checkResource = TestUtility.makeResource(testContest, ResourceType.DNS);
        checkResource = resourceService.create(checkResource);
        AbstractPoller poller = PollerFactory.getPoller(checkResource);
        testPoll = poller.poll();
        Assert.assertTrue(testPoll.getResults().startsWith("ERROR"));
        Assert.assertNull(testPoll.getTeam());
    }
}
