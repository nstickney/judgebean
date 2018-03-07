package is.stma.judgebean.data;

import is.stma.judgebean.model.Team;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository(forEntity = Team.class)
public interface TeamRepo extends EntityRepository<Team, String> {

    List<Team> findAllOrderByNameAsc();

    @Query("select t from Team t where t.contest.id = ?1")
    List<Team> findByContest(String contestId);

    @Query("select t from Team t where t.name = ?1 and t.contest.name = ?2")
    Team findByNameAndContestName(String teamName, String contestName);
}