package com.syu.cara.agency.service;

import com.syu.cara.agency.domain.Agency;
import com.syu.cara.agency.repository.AgencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyRepository agencyRepository;

    // 지역별 키워드 매핑
    private static final Map<String, List<String>> LOCATION_KEYWORDS;

    static {
        Map<String, List<String>> map = new HashMap<>();
        map.put("서울", Arrays.asList("서울역", "강남역", "서울"));
        map.put("부산", Arrays.asList("부산해운대", "부산서면", "부산"));
        map.put("대구", Arrays.asList("대구동성로", "대구수성구", "대구"));
        map.put("인천", Arrays.asList("인천공항", "인천부평", "인천"));
        map.put("광주", Arrays.asList("광주터미널", "광주송정역", "광주"));
        map.put("대전", Arrays.asList("대전시청", "대전복합터미널", "대전"));
        map.put("울산", Arrays.asList("울산삼산", "울산공업탑", "울산"));
        map.put("세종", Arrays.asList("세종정부청사", "세종시청", "세종"));
        map.put("경기", Arrays.asList("경기수원", "경기고양", "경기"));
        map.put("강원", Arrays.asList("강원춘천", "강원강릉", "강원"));
        map.put("충북", Arrays.asList("충북청주", "충북제천", "충북"));
        map.put("충남", Arrays.asList("충남천안", "충남아산", "충남"));
        map.put("전북", Arrays.asList("전북전주", "전북익산", "전북"));
        map.put("전남", Arrays.asList("전남여수", "전남순천", "전남"));
        map.put("경북", Arrays.asList("경북포항", "경북구미", "경북"));
        map.put("경남", Arrays.asList("경남창원", "경남진주", "경남"));
        map.put("제주", Arrays.asList("제주공항", "제주시청", "제주"));
        LOCATION_KEYWORDS = Collections.unmodifiableMap(map);
    }

    public List<Agency> getAllAgencies() {
        return agencyRepository.findAll();
    }

    public List<Agency> getAgenciesByLocation(String location) {
        System.out.println("🔍 지점 검색 요청 - 지역: " + location);

        Set<Agency> resultSet = new HashSet<>();

        // 1. 직접 키워드로 검색
        List<Agency> directResult = agencyRepository.findByKeyword(location);
        resultSet.addAll(directResult);
        System.out.println("🔍 직접 검색 결과: " + directResult.size() + "개");

        // 2. 매핑된 키워드들로 검색
        List<String> keywords = LOCATION_KEYWORDS.get(location);
        if (keywords != null) {
            for (String keyword : keywords) {
                List<Agency> keywordResult = agencyRepository.findByKeyword(keyword);
                resultSet.addAll(keywordResult);
                System.out.println("🔍 키워드 '" + keyword + "' 검색 결과: " + keywordResult.size() + "개");
            }
        }

        List<Agency> finalResult = new ArrayList<>(resultSet);
        System.out.println("🔍 최종 검색 결과: " + finalResult.size() + "개");

        // 결과 로깅
        finalResult.forEach(agency -> {
            String locationInfo = agency.getLocation() != null ? agency.getLocation() : "위치정보없음";
            System.out.println("  - " + agency.getAgencyName() + " (" + locationInfo + ")");
        });

        return finalResult;
    }
}
