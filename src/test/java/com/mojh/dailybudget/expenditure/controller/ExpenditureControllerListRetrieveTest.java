package com.mojh.dailybudget.expenditure.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojh.dailybudget.auth.JwtFixture;
import com.mojh.dailybudget.category.CategoryFixture;
import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.common.exception.ErrorCode;
import com.mojh.dailybudget.common.web.ApiResponse;
import com.mojh.dailybudget.expenditure.ExpenditureFixture;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.expenditure.dto.response.ExpenditureListResponse;
import com.mojh.dailybudget.expenditure.repository.ExpenditureRepository;
import com.mojh.dailybudget.member.MemberFixture;
import com.mojh.dailybudget.member.domain.Member;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.mojh.dailybudget.expenditure.ExpenditureTestUtils.expenditureListToExpectedList;
import static com.mojh.dailybudget.expenditure.ExpenditureTestUtils.filteredExpenditureList;
import static com.mojh.dailybudget.expenditure.ExpenditureTestUtils.totalExpenditureAmount;
import static com.mojh.dailybudget.expenditure.ExpenditureTestUtils.totalExpenditureByCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("/db/member-expenditure-list.sql")
@DisplayName("지출 목록 조회 API 통합 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExpenditureControllerListRetrieveTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ExpenditureRepository expenditureRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final String URL = "/api/expenditures";

    private final String BEGIN_DATE = "beginDate";
    private final String END_DATE = "endDate";
    private final String MIN_AMOUNT = "minAmount";
    private final String MAX_AMOUNT = "maxAmount";
    private final String CATEGORY = "category";

    static Member member1;
    static Member member2;
    static Member member3;

    static String accessToken1;
    static String accessToken2;
    static String accessToken3;

    static Map<CategoryType, Category> categoryMap;
    static List<Expenditure> allExpenditureList;

    @BeforeAll
    static void beforeAll() {
        member1 = MemberFixture.MEMBER1();
        member2 = MemberFixture.MEMBER2();
        member3 = MemberFixture.MEMBER3();

        accessToken1 = JwtFixture.ACCESS_TOKEN_MEMBER1;
        accessToken2 = JwtFixture.ACCESS_TOKEN_MEMBER2;
        accessToken3 = JwtFixture.ACCESS_TOKEN_MEMBER3;

        categoryMap = CategoryFixture.CATEGORY;
    }

    @BeforeEach
    void beforeEach() {
        allExpenditureList = expenditureRepository.findAll();
    }




}
