package com.safehouse.domain.inspector_community.repository;

import com.safehouse.domain.inspector_community.entity.InspectorsComment;
import com.safehouse.domain.inspector_community.entity.InspectorsCommunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectorsCommentRepository extends JpaRepository<InspectorsComment, Long> {
    List<InspectorsComment> findByInspectorsCommunity(InspectorsCommunity inspectorsCommunity);
}
