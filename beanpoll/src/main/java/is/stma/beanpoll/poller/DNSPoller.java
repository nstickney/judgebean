/*
 * Copyright 2018 Nathaniel Stickney
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package is.stma.beanpoll.poller;

import is.stma.beanpoll.model.Parameter;
import is.stma.beanpoll.model.Poll;
import is.stma.beanpoll.model.Resource;
import is.stma.beanpoll.model.Team;
import is.stma.beanpoll.service.parameterizer.DNSParameterizer;
import is.stma.beanpoll.util.DNSUtility;
import org.xbill.DNS.Type;

import java.util.List;

public class DNSPoller extends AbstractPoller {

    private String query = DNSParameterizer.DNS_DEFAULT_QUERY;
    private String recordType = DNSParameterizer.DNS_DEFAULT_RECORD_TYPE;
    private String tcpString = DNSParameterizer.DNS_DEFAULT_TCP;
    private String recursiveString = DNSParameterizer.DNS_DEFAULT_RECURSIVE;
    private String expected = DNSParameterizer.DNS_DEFAULT_EXPECTED;

    private int type = Type.A;

    private boolean tcp = false;
    private boolean recursive = true;

    DNSPoller(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Poll doPoll() {

        convertStrings();

        Poll newPoll = new Poll();
        newPoll.setResource(resource);

        // Do the poll
        newPoll.setResults(DNSUtility.lookup(resource.getAddress(), resource.getPort(), query,
                type, tcp, recursive, resource.getTimeout(), false));

        // Check if it worked, and if it did, score the poll
        if (newPoll.getResults().contains(expected)) {

            // No need to check flags if only one team is assigned
            if (1 == resource.getTeams().size()) {
                newPoll.setTeam(resource.getTeams().get(0));
                newPoll.setScore(resource.getPointValue());
            } else {

                // Check the flags
                newPoll.setResults(newPoll.getResults() + ", flag: " +
                        DNSUtility.lookup(resource.getAddress(), resource.getPort(), query, Type.TXT, tcp, recursive, resource.getTimeout(), false));

                List<Team> possibleTeams = resource.getTeams();

                // If no teams are assigned to this resource, it's fair game
                if (possibleTeams.isEmpty()) {
                    possibleTeams = resource.getContest().getTeams();
                }

                List<Team> scoringTeams;
                scoringTeams = checkTeams(possibleTeams, newPoll.getResults());

                if (1 == scoringTeams.size()) {
                    newPoll.setTeam(scoringTeams.get(0));
                    newPoll.setScore(resource.getPointValue());
                }
            }
        }
        return newPoll;
    }

    @Override
    void setParameters() {

        // Set values based on the resource parameters
        for (Parameter p : resource.getParameters()) {
            switch (p.getTag()) {
                case DNSParameterizer.DNS_QUERY:
                    query = p.getValue();
                    break;
                case DNSParameterizer.DNS_RECORD_TYPE:
                    recordType = p.getValue();
                    break;
                case DNSParameterizer.DNS_TCP:
                    tcpString = p.getValue();
                    break;
                case DNSParameterizer.DNS_RECURSIVE:
                    recursiveString = p.getValue();
                    break;
                case DNSParameterizer.DNS_EXPECTED:
                    expected = p.getValue();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Set the object's typed parameters from its String parameters
     */
    private void convertStrings() {

        tcp = tcpString.equals(Parameter.TRUE);
        recursive = recursiveString.equals(Parameter.TRUE);

        switch (recordType) {
            case "A":
                type = Type.A;
                break;
            case "AAAA":
                type = Type.AAAA;
                break;
            case "CNAME":
                type = Type.CNAME;
                break;
            case "DNAME":
                type = Type.DNAME;
                break;
            case "MX":
                type = Type.MX;
                break;
            case "NS":
                type = Type.NS;
                break;
            case "PTR":
                type = Type.PTR;
                break;
            case "SOA":
                type = Type.SOA;
                break;
            case "TXT":
                type = Type.TXT;
                break;
            default:
                break;
        }

    }
}