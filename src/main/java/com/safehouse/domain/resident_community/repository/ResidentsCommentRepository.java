package com.safehouse.domain.resident_community.repository;

import com.safehouse.domain.resident_community.entity.ResidentsComment;
import com.safehouse.domain.resident_community.entity.ResidentsCommunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidentsCommentRepository extends JpaRepository<ResidentsComment, Long> {
    List<ResidentsComment> findByResidentCommunity(ResidentsCommunity community);
}
