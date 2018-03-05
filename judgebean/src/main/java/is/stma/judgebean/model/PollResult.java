package is.stma.judgebean.model;

import javax.enterprise.inject.Model;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Model
public class PollResult extends AEntity {

    /* Fields --------------------------------------------------------------- */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column
    @DecimalMin("-1.00")
    @DecimalMax("1.00")
    @Digits(integer = 1, fraction = 2)
    private BigDecimal points;

    /* Overrides ------------------------------------------------------------ */
    @Override
    public String getName() {
        return "Result: " + poll.getScoreable().getName() + ": " + getId();
    }

    /* Getters and Setters -------------------------------------------------- */

    /* Methods -------------------------------------------------------------- */
}