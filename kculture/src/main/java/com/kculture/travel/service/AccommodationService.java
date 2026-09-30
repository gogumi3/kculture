package com.kculture.travel.service;

import com.kculture.common.geo.GeoUtils;
import com.kculture.travel.domain.Accommodation;
import com.kculture.travel.dto.AccommodationResponse;
import com.kculture.travel.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    // S10 핵심: 좌표 반경 내 숙박 검색 (바운딩박스 1차 → Haversine 정확 필터 → 거리순 → 상위 N)
    @Transactional(readOnly = true)
    public List<AccommodationResponse> findNearby(double lat, double lng, double radiusMeters,
                                                  String type, String priceRange, int limit) {
        if (radiusMeters <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "radius는 0보다 커야 합니다.");
        }

        GeoUtils.BoundingBox box = GeoUtils.boundingBox(lat, lng, radiusMeters);
        List<Accommodation> candidates = accommodationRepository.findWithinBox(
                box.minLat(), box.maxLat(), box.minLng(), box.maxLng(), type, priceRange);

        List<AccommodationResponse> result = new ArrayList<>();
        for (Accommodation a : candidates) {
            if (a.getLatitude() == null || a.getLongitude() == null) {
                continue; // 좌표 없는 숙소 제외
            }
            double dist = GeoUtils.haversineMeters(
                    lat, lng, a.getLatitude().doubleValue(), a.getLongitude().doubleValue());
            if (dist <= radiusMeters) { // 사각형 → 정확한 원형 반경으로 걸러냄
                result.add(AccommodationResponse.from(a, Math.round(dist)));
            }
        }

        result.sort(Comparator.comparingLong(AccommodationResponse::distanceMeters));
        return result.size() > limit ? result.subList(0, limit) : result;
    }

    // 리스트 뷰: 지역/유형 필터
    @Transactional(readOnly = true)
    public List<AccommodationResponse> findByRegion(String regionSido, String type) {
        return accommodationRepository.search(regionSido, type).stream()
                .map(AccommodationResponse::from)
                .toList();
    }

    // 상세
    @Transactional(readOnly = true)
    public AccommodationResponse getAccommodation(Long id) {
        Accommodation a = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "숙소를 찾을 수 없습니다."));
        return AccommodationResponse.from(a);
    }
}
