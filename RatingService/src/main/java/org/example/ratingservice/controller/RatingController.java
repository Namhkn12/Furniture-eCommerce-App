package org.example.ratingservice.controller;

import org.example.ratingservice.dto.RatingStatsDTO;
import org.example.ratingservice.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/rate/{userId}/{productId}")
    public ResponseEntity<String> rate(
            @PathVariable int userId,
            @PathVariable int productId,
            @RequestParam(required = false) Boolean hasPicture,
            @RequestParam(required = false) Integer byRating) {

        ratingService.doRatingLogic(productId, userId, hasPicture, byRating);
        return ResponseEntity.ok("Rating completed!");
    }
    @GetMapping("/average-rating/{productId}")
    public ResponseEntity<RatingStatsDTO> getRatingStats(
            @PathVariable int productId,
            @RequestParam(required = false) Boolean hasPicture) {

        RatingStatsDTO stats = ratingService.getRatingStats(productId, hasPicture);
        return ResponseEntity.ok(stats);
    }
}
