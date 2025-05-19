package org.example.ratingservice.repository;

import org.example.ratingservice.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    List<Rating> findByUserIdAndProductId(int userId, int productId);
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.productId = :productId")
    Double findAverageRatingByProductId(@Param("productId") int productId);
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.productId = :productId AND (:hasPicture IS NULL OR r.hasPicture = :hasPicture)")
    Double findAverageRatingByProductId(@Param("productId") int productId, @Param("hasPicture") Boolean hasPicture);
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.productId = :productId AND (:hasPicture IS NULL OR r.hasPicture = :hasPicture)")
    Long countByProductIdAndHasPicture(@Param("productId") int productId, @Param("hasPicture") Boolean hasPicture);

}

