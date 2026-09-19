package com.kculture.travel.service;

import com.kculture.travel.domain.Place;
import com.kculture.travel.dto.PlaceResponse;
import com.kculture.travel.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    // 지역/카테고리 optional 필터 목록
    @Transactional(readOnly = true)
    public List<PlaceResponse> findPlaces(String regionSido, String category) {
        return placeRepository.search(regionSido, category).stream()
                .map(PlaceResponse::from)
                .toList();
    }

    // 이름 검색
    @Transactional(readOnly = true)
    public List<PlaceResponse> searchPlaces(String keyword) {
        return placeRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(PlaceResponse::from)
                .toList();
    }

    // 상세
    @Transactional(readOnly = true)
    public PlaceResponse getPlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "장소를 찾을 수 없습니다."));
        return PlaceResponse.from(place);
    }
}
