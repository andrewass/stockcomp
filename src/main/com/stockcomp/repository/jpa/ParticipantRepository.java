package com.stockcomp.repository.jpa;

import com.stockcomp.entity.contest.Contest;
import com.stockcomp.entity.contest.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query("SELECT p FROM Participant p inner join p.user u where u.username = ?1 and p.contest = ?2")
    List<Participant> findParticipantFromUsername(String username, Contest contest);

}
