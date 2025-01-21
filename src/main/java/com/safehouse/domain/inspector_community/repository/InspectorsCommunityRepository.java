package com.safehouse.domain.inspector_community.repository;

import com.safehouse.domain.inspector_community.entity.InspectorsCommunity;
import com.safehouse.domain.user.entity.Inspector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InspectorsCommunityRepository extends JpaRepository<InspectorsCommunity, Long> {
    List<InspectorsCommunity> findAllByOrderByInspectorCommunityCreatedAtDesc();

    @Query("SELECT i FROM InspectorsCommunity i JOIN FETCH i.inspector insp JOIN FETCH insp.user WHERE i.inspectorPostId = :id")
    Optional<InspectorsCommunity> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT i FROM InspectorsCommunity i JOIN FETCH i.inspector insp JOIN FETCH insp.user WHERE insp.user.email = :email")
    Optional<InspectorsCommunity> findByEmail(@Param("email") String email);
}

