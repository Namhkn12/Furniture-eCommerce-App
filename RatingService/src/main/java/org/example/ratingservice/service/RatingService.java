package org.example.ratingservice.service;

import org.example.ratingservice.dto.ProductDTO;
import org.example.ratingservice.dto.RatingStatsDTO;
import org.example.ratingservice.dto.UserDTO;
import org.example.ratingservice.model.Rating;
import org.example.ratingservice.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Rating> doRatingLogic(int productId, int userId, Boolean hasPicture, Integer byRating) {
        ProductDTO product = getProductById(productId);
        UserDTO user = getUserById(userId);

        List<Rating> ratings = ratingRepository.findByUserIdAndProductId(userId, productId);

        // Lọc theo yêu cầu
        Stream<Rating> stream = ratings.stream();

        if (Boolean.TRUE.equals(hasPicture)) {
            stream = stream.filter(Rating::getHasPicture);
        }

        if (byRating != null) {
            stream = stream.filter(r -> r.getRating() == byRating);
        }

        return stream.collect(Collectors.toList());
    }

    private ProductDTO getProductById(int productId) {
        return restTemplate.getForObject("http://localhost:8082/products/" + productId, ProductDTO.class);
    }

    private UserDTO getUserById(int userId) {
        return restTemplate.getForObject("http://localhost:8081/users/" + userId, UserDTO.class);
    }
    public Double getAverageRatingForProduct(int productId) {
        Double avg = ratingRepository.findAverageRatingByProductId(productId);
        return avg != null ? avg : 0.0;
    }
    public RatingStatsDTO getRatingStats(int productId, Boolean hasPicture) {
        Double avg = ratingRepository.findAverageRatingByProductId(productId, hasPicture);
        Long count = ratingRepository.countByProductIdAndHasPicture(productId, hasPicture);

        return new RatingStatsDTO(avg != null ? avg : 0.0, count != null ? count : 0);
    }

}
