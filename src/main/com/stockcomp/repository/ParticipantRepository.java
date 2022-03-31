package com.stockcomp.repository;

import com.stockcomp.domain.contest.Contest;
import com.stockcomp.domain.contest.Participant;
import com.stockcomp.domain.contest.enums.ContestStatus;
import com.stockcomp.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query("SELECT p FROM Participant p inner join p.user u where u.username = ?1 and p.contest = ?2")
    List<Participant> findAllByUsernameAndContest(String username, Contest contest);

    @Query("SELECT p FROM Participant  p where p.contest = ?1")
    List<Participant> findAllByContest(Contest contest);

    @Query("SELECT p FROM Participant p join p.contest c where c.contestStatus  = ?1")
    List<Participant> findAllByContestStatus(ContestStatus contestStatus);

    List<Participant> findAllByUser(User user);

    List<Participant> findAllByContestOrderByTotalValueDesc(Contest contest);

    List<Participant> findAllByContestOrderByRankAsc(Contest contest);

    Participant findByContestAndUser(Contest contest, User user);

}
