package org.example.ratingservice.dto;

public class RatingStatsDTO {
    private double averageRating;
    private long totalCount;

    public RatingStatsDTO(double averageRating, long totalCount) {
        this.averageRating = averageRating;
        this.totalCount = totalCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public long getTotalCount() {
        return totalCount;
    }
}
