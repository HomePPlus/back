package com.safehouse.domain.resident_community.repository;

import com.safehouse.domain.resident_community.entity.ResidentsCommunity;
import com.safehouse.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ResidentsCommunityRepository.java
@Repository
public interface ResidentsCommunityRepository extends JpaRepository<ResidentsCommunity, Long> {
    List<ResidentsCommunity> findAllByOrderByCommunityCreatedAtDesc();
    List<ResidentsCommunity> findByUser(User user);

    @Query("SELECT rc FROM ResidentsCommunity rc JOIN FETCH rc.user WHERE rc.communityPostId = :postId")
    Optional<ResidentsCommunity> findByIdWithUser(@Param("postId") Long postId);
}

