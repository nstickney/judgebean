/*
 * Copyright 2018 Nathaniel Stickney
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package is.stma.beanpoll.service;

import is.stma.beanpoll.data.AbstractRepo;
import is.stma.beanpoll.data.ContestRepo;
import is.stma.beanpoll.model.Contest;
import is.stma.beanpoll.rules.ContestRules;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Stateless
public class ContestService extends AbstractService<Contest, AbstractRepo<Contest>,
        ContestRules> {

    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    private ContestRepo repo;

    @Inject
    private Event<Contest> event;

    @Inject
    private ContestRules rules;

    @Override
    AbstractRepo<Contest> getRepo() {
        return repo;
    }

    @Override
    Event<Contest> getEvent() {
        return event;
    }

    @Override
    ContestRules getRules() {
        return rules;
    }
}
