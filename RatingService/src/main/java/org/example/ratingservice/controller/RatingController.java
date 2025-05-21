package org.example.ratingservice.controller;

import org.example.ratingservice.model.Rating;
import org.example.ratingservice.repository.RatingRepository;
import org.example.ratingservice.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class RatingController {

    @Autowired
    private RatingService ratingService;
    @Autowired
    private RatingRepository ratingRepository;
    @GetMapping("/ratings")
    public ResponseEntity<List<Rating>> getRatingsByProduct(
            @RequestParam int productId) {
        List<Rating> ratings = ratingService.findByProductId(productId);
        return ResponseEntity.ok(ratings);
    }
    @PostMapping
    public ResponseEntity<Rating> createRating(@RequestBody Rating rating) {
        rating.setDate(LocalDate.now().toString()); // tự động gán ngày hiện tại nếu cần
        Rating savedRating = ratingRepository.save(rating);
        return ResponseEntity.ok(savedRating);
    }
    @GetMapping("/average/{productId}")
    public ResponseEntity<Double> getAverageRating(@PathVariable int productId) {
        Double average = ratingService.getAverageRatingByProductId(productId);
        return ResponseEntity.ok(average);
    }
    @GetMapping("/count/{productId}")
    public ResponseEntity<Long> getRatingCount(@PathVariable int productId) {
        Long count = ratingService.getRatingCountByProductId(productId);
        return ResponseEntity.ok(count);
    }
}




//    @GetMapping("/ratings/average")
//    public ResponseEntity<Double> getAverageRating(
//            @RequestParam int productId,
//            @RequestParam(required = false) Boolean hasPicture) {
//        Double average;
//        if (hasPicture == null) {
//            average = ratingService.findAverageRatingByProductId(productId);
//        } else {
//            average = ratingService.findAverageRatingByProductId(productId, hasPicture);
//        }
//        return ResponseEntity.ok(average);
//    }
//    @GetMapping("/ratings/count")
//    public ResponseEntity<Long> countRatings(
//            @RequestParam int productId,
//            @RequestParam(required = false) Boolean hasPicture) {
//        Long count = ratingService.countByProductIdAndHasPicture(productId, hasPicture);
//        return ResponseEntity.ok(count);
//    }

//    @GetMapping("/rate/{userId}/{productId}")
//    public ResponseEntity<String> rate(
//            @PathVariable int userId,
//            @PathVariable int productId,
//            @RequestParam(required = false) Boolean hasPicture,
//            @RequestParam(required = false) Integer byRating) {
//
//        ratingService.doRatingLogic(productId, userId, hasPicture, byRating);
//        return ResponseEntity.ok("Rating completed!");
//    }
//    @GetMapping("/average-rating/{productId}")
//    public ResponseEntity<RatingStatsDTO> getRatingStats(
//            @PathVariable int productId,
//            @RequestParam(required = false) Boolean hasPicture) {
//
//        RatingStatsDTO stats = ratingService.getRatingStats(productId, hasPicture);
//        return ResponseEntity.ok(stats);
//    }
