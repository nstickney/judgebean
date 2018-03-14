package is.stma.judgebean.beanpoll.model;

public abstract class ComparableByContest extends AbstractEntity implements Comparable<ComparableByContest> {

    @Override
    public boolean equals(Object o) {
        return null != o &&
                o.getClass().equals(getClass()) &&
                equalByUUID((ComparableByContest) o);
    }

    @Override
    public int hashCode() {
        String hashCodeString = getId() + getDisplayName();
        return hashCodeString.hashCode();
    }

    abstract Contest getContest();

    public String getDisplayName() {
        if (null != getContest()) {
            return getContest().getName() + ": " + getName();
        }
        return getName();
    }

    public String getLook() {
        if (null == getContest()) {
            return "warning";
        } else if (getContest().isRunning()) {
            return "success";
        }
        return "default";
    }

    int compare(ComparableByContest e1, ComparableByContest e2) {

        // If both are assigned to contests, decide based on which contest is running
        if (null != e1.getContest() && null != e2.getContest()) {

            // If e1's contest is running (but not e2's), it is lower, and vice versa
            if (e1.getContest().isRunning() && ! e2.getContest().isRunning()) {
                return -1;
            } else if ( ! e1.getContest().isRunning() && e2.getContest().isRunning()) {
                return 1;
            }
        }

        // If only one is not assigned to a contest, that one is lower
        if (null == e1.getContest() && null != e2.getContest()) {
            return -1;
        } else if (null != e1.getContest() && null == e2.getContest()) {
            return 1;
        }

        int byDisplayName = e1.getDisplayName().compareToIgnoreCase(e2.getDisplayName());

        // If no other criteria have decided the issue, decide based on UUID
        if (0 == byDisplayName) {
            return e1.getId().compareTo(e2.getId());
        }
        return byDisplayName;
    }

}
