/*
 * Copyright 2018 Nathaniel Stickney
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package is.stma.beanpoll.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Resource extends AbstractComparableByContest {

    public static final String DNS = "DNS";
    public static final String HTTP = "HTTP";

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private ResourceType type = ResourceType.DNS;

    @Column(nullable = false)
    private String address = "ns1.baylor.edu";

    @Column(nullable = false)
    private int port = 53;

    @Column(nullable = false)
    private int timeout = 3;

    @Column(nullable = false)
    private boolean available = false;

    @Column(nullable = false)
    private int pointValue = 1;

    @OneToMany(mappedBy = "resource")
    private List<is.stma.beanpoll.model.Parameter> parameters = new ArrayList<>();

    @ManyToOne
    private Contest contest;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ResourceTeams",
            joinColumns = {@JoinColumn(name = "resource_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "team_id", referencedColumnName = "id")})
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "resource")
    private List<Poll> polls = new ArrayList<>();

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLook() {
        if (available) {
            if (null == contest || !contest.isRunning()) {
                return "warning";
            }
            return "success";
        } else if (null != contest && contest.isRunning()) {
            return "warning";
        }
        return "default";
    }

    @Override
    public int compareTo(AbstractComparableByContest o) {
        return compare(this, o);
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean scoring) {
        this.available = scoring;
    }

    public int getPointValue() {
        return pointValue;
    }

    public void setPointValue(int weight) {
        this.pointValue = weight;
    }

    public List<is.stma.beanpoll.model.Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<is.stma.beanpoll.model.Parameter> parameters) {
        this.parameters = parameters;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public void addTeam(Team team) {
        if (null != team && !teams.contains(team)) {
            teams.add(team);
        }
    }

    public void removeTeam(Team team) {
        if (null != team && teams.contains(team)) {
            teams.remove(team);
        }
    }

    public List<Poll> getPolls() {
        return polls;
    }

    public void setPolls(List<Poll> polls) {
        this.polls = polls;
    }

    public boolean isUp() {
        if (contest.isEnabled() && contest.isRunning() && !polls.isEmpty()) {
            List<Poll> toSort = new ArrayList<>(polls);
            Collections.sort(toSort);
            return !toSort.get(toSort.size() - 1).getResults().startsWith("ERROR: ");
        }
        return false;
    }

    public boolean ownedBy(Team team) {
        if (contest.isEnabled() && contest.isRunning() && !polls.isEmpty()) {
            List<Poll> toSort = new ArrayList<>(polls);
            Collections.sort(toSort);
            Team owner = toSort.get(toSort.size() - 1).getTeam();
            return null != owner && owner.equalByUUID(team);
        }
        return false;
    }

    public LocalDateTime getLastCheck() {
        if (!polls.isEmpty()) {
            return polls.get(polls.size() - 1).getTimestamp();
        }
        return null;
    }

    public String getLookFor(Team team) {
        if (isUp() && ownedBy(team)) {
            return "success";
        }
        return "danger";
    }
}
