package com.mojh.dailybudget.expenditure.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojh.dailybudget.auth.JwtFixture;
import com.mojh.dailybudget.category.CategoryFixture;
import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.common.exception.ErrorCode;
import com.mojh.dailybudget.common.web.ApiResponse;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureCreateRequest;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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

import static com.mojh.dailybudget.auth.JwtFixture.ACCESS_TOKEN_MEMBER1;
import static com.mojh.dailybudget.category.domain.CategoryType.SHOPPING;
import static com.mojh.dailybudget.common.exception.ErrorCode.EXPENDITURE_MEMBER_MISMATCH;
import static com.mojh.dailybudget.common.exception.ErrorCode.EXPENDITURE_NOT_FOUND;
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
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("/db/member-expenditure.sql")
@DisplayName("지출 API 통합 테스트")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ExpenditureControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ExpenditureRepository expenditureRepository;

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final String URL = "/api/expenditures";
    private final String GET_URL = URL + "/{expenditureId}";
    private final String DELETE_URL = URL + "/{expenditureId}";

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

    private <T> String toJson(T data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }

    @Test
    @DisplayName("지출 정보를 생성한다.")
    void createExpenditure() throws Exception {
        // given
        String accessToken = accessToken1;
        String category = SHOPPING.toString();
        Long amount = 49800L;
        String memo = "옷 구매";
        boolean excludeFromTotal = false;
        LocalDateTime expenditureAt = LocalDateTime.of(2023, 11, 15, 13, 42, 16);

        ExpenditureCreateRequest requet = new ExpenditureCreateRequest(category, amount, memo, excludeFromTotal, expenditureAt);
        String requestBody = toJson(requet);

        Long id = allExpenditureList.size() + 1L;
        String location = String.format("%s/%d", URL, id);

        // when, then
        mockMvc.perform(post(URL).header(AUTHORIZATION, accessToken)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(requestBody))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(header().string(LOCATION, location));
    }

    @Test
    @DisplayName("지출 정보를 생성할 때 필수 요청 데이터가 누락되면 COMMON_INVALID_PARAMETERS 예외가 발생한다.")
    void createExpenditure_requiredFieldsMissing_exception() throws Exception {
        // given: 요청 데이터 null로 입력되게 설정
        ErrorCode expectedErrorCode = ErrorCode.COMMON_INVALID_PARAMETERS;
        String accessToken = accessToken1;

        ExpenditureCreateRequest request = new ExpenditureCreateRequest(null, null, null, null, null);
        String requestBody = toJson(request);

        // when, then: category, amount, expenditureAt 필드가 필수이다.
        mockMvc.perform(post(URL).header(AUTHORIZATION, accessToken)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(requestBody))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expectedErrorCode.getCode()))
               .andExpect(jsonPath("$.error.message.category").exists())
               .andExpect(jsonPath("$.error.message.amount").exists())
               .andExpect(jsonPath("$.error.message.expenditureAt").exists());
    }

    @Test
    @DisplayName("지출 정보를 생성할 때 카테고리 정보가 유효하지 않으면 COMMON_INVALID_PARAMETERS 예외가 발생한다.")
    void createExpenditure_invalidCategory_exception() throws Exception {
        // given: null이 아니고 설정된 카테고리와 무관한 데이터로 카테고리 정보 입력
        ErrorCode expectedErrorCode = ErrorCode.COMMON_INVALID_PARAMETERS;
        String accessToken = accessToken1;
        String category = "invalid" + SHOPPING.toString();
        Long amount = 49800L;
        String memo = "옷 구매";
        boolean excludeFromTotal = false;
        LocalDateTime expenditureAt = LocalDateTime.of(2023, 11, 15, 13, 42, 16);

        ExpenditureCreateRequest requet = new ExpenditureCreateRequest(category, amount, memo, excludeFromTotal, expenditureAt);
        String requestBody = toJson(requet);

        // when, then
        mockMvc.perform(post(URL).header(AUTHORIZATION, accessToken)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(requestBody))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expectedErrorCode.getCode()))
               .andExpect(jsonPath("$.error.message.category").exists());
    }

    @Test
    @DisplayName("지출 정보를 생성할 때 잘못된 날짜 형식으로 지출 일시를 입력하면 COMMON_BAD_REQUEST 예외가 발생한다.")
    void createExpenditure_invalidExpenditureAt_exception() throws Exception {
        // given: 지출 일시를 포맷에 맞지 않는 문자열로 입력
        ErrorCode expectedErrorCode = ErrorCode.COMMON_BAD_REQUEST;
        String accessToken = accessToken1;
        String requestBody = "{\"category\":\"invalidSHOPPING\",\"amount\":49800,\"memo\":\"옷 구매\"," +
                             "\"excludeFromTotal\":false,\"expenditureAt\":\"invalid2023-11-15T13:42:16time\"}";

        // when, then
        mockMvc.perform(post(URL).header(AUTHORIZATION, accessToken)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(requestBody))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expectedErrorCode.getCode()))
               .andExpect(jsonPath("$.error.message").value(expectedErrorCode.getMessage()));
    }

    @Test
    @DisplayName("지출 정보를 생성할 때 최소 금액 미만의 금액이 입력되면 COMMON_INVALID_PARAMETERS 예외가 발생한다.")
    void createExpenditure_LessThanMinAmount_exception() throws Exception {
        // given: 0원 미만으로 금액 입력하도록 설정
        ErrorCode expectedErrorCode = ErrorCode.COMMON_INVALID_PARAMETERS;
        String accessToken = accessToken1;
        String category = SHOPPING.toString();
        Long amount = -1L;
        String memo = "옷 구매";
        boolean excludeFromTotal = false;
        LocalDateTime expenditureAt = LocalDateTime.of(2023, 11, 15, 13, 42, 16);

        ExpenditureCreateRequest requet = new ExpenditureCreateRequest(category, amount, memo, excludeFromTotal, expenditureAt);
        String requestBody = toJson(requet);

        // when, then
        mockMvc.perform(post(URL).header(AUTHORIZATION, accessToken)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(requestBody))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expectedErrorCode.getCode()))
               .andExpect(jsonPath("$.error.message.amount").exists());
    }

    @Test
    @DisplayName("지출 정보를 생성할 때 최대 금액을 초과하는 금액이 입력되면 COMMON_INVALID_PARAMETERS 예외가 발생한다.")
    void createExpenditure_ExceedsMaxAmount_exception() throws Exception {
        // given: 1조원 초과되는 금액이 입력되도록 설정
        ErrorCode expectedErrorCode = ErrorCode.COMMON_INVALID_PARAMETERS;
        String accessToken = accessToken1;
        String category = SHOPPING.toString();
        Long amount = 1000000000000L + 1L;
        String memo = "옷 구매";
        boolean excludeFromTotal = false;
        LocalDateTime expenditureAt = LocalDateTime.of(2023, 11, 15, 13, 42, 16);

        ExpenditureCreateRequest requet = new ExpenditureCreateRequest(category, amount, memo, excludeFromTotal, expenditureAt);
        String requestBody = toJson(requet);

        // when, then
        mockMvc.perform(post(URL).header(AUTHORIZATION, accessToken)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(requestBody))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expectedErrorCode.getCode()))
               .andExpect(jsonPath("$.error.message.amount").exists());
    }

    @Test
    @DisplayName("지출 상세 내용을 조회한다.")
    void retrieveExpenditure() throws Exception {
        // given
        Long expenditrueId = 1L;
        String accessToken = ACCESS_TOKEN_MEMBER1;
        Expenditure expected = allExpenditureList.get((int) (expenditrueId - 1));
        // 같은 시간 값이어도 포맷이 달라 비교할 때 다른 값이라고 판단하기에 string으로 바꿔서 검증
        String expectedExpenditureAt = expected.getExpenditureAt().format(FORMATTER);
        
        // when, then
        mockMvc.perform(get(GET_URL, expenditrueId).header(AUTHORIZATION, accessToken))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.response.category").value(expected.getCategoryType().toString()))
               .andExpect(jsonPath("$.response.amount").value(expected.getAmount()))
               .andExpect(jsonPath("$.response.memo").value(expected.getMemo()))
               .andExpect(jsonPath("$.response.excludeFromTotal").value(expected.getExcludeFromTotal()))
               .andExpect(jsonPath("$.response.expenditureAt").value(expectedExpenditureAt))
               .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    @DisplayName("지출 상세 내용을 조회할 때 지출이 존재하지 않으면 EXPENDITURE_NOT_FOUND 예외가 발생한다.")
    void retrieveExpenditure_expenditureNotFound_exception() throws Exception {
        // given
        Long expenditrueId = 234234507L;
        String accessToken = ACCESS_TOKEN_MEMBER1;
        ErrorCode expected = EXPENDITURE_NOT_FOUND;

        // when, then
        mockMvc.perform(get(GET_URL, expenditrueId).header(AUTHORIZATION, accessToken))
               .andDo(print())
               .andExpect(status().isNotFound())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expected.getCode()))
               .andExpect(jsonPath("$.error.message").value(expected.getMessage()));
    }

    @Test
    @DisplayName("다른 유저의 지출 정보를 조회할 때 EXPENDITURE_MEMBER_MISMATCH 예외가 발생한다.")
    void retrieveExpenditure_expenditureMemberMismatch_exception() throws Exception {
        // given: member1이 만든 지출 정보를 member2가 조회하도록 설정
        Long expenditrueId = 1L;
        String accessTokenMember2 = JwtFixture.ACCESS_TOKEN_MEMBER2;
        ErrorCode expected = EXPENDITURE_MEMBER_MISMATCH;

        // when, then
        mockMvc.perform(get(GET_URL, expenditrueId).header(AUTHORIZATION, accessTokenMember2))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expected.getCode()))
               .andExpect(jsonPath("$.error.message").value(expected.getMessage()));
    }

    @Test
    @DisplayName("조회 조건에 맞는 지출 목록이 없을 때 지출 합계가 0원이고 비어 있는 지출 목록이 조회된다.")
    void retrieveExpenditureList_emptyList() throws Exception {
        // given: 지출 정보가 없는 member3에 대한 지출 목록 조회 하도록 설정
        String accessToken = accessToken3;
        String beginDate = "2023-11-01T13:30:00";
        String endDate = "2023-11-27T15:30:00";

        // when, then
        mockMvc.perform(get(URL).header(AUTHORIZATION, accessToken)
                                .param(BEGIN_DATE, beginDate)
                                .param(END_DATE, endDate))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.response.totalAmount").value(0L))
               .andExpect(jsonPath("$.response.totalExpenditureByCategory", is(anEmptyMap())))
               .andExpect(jsonPath("$.response.expenditureList", empty()))
               .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    @DisplayName("기간 내의 지출 목록을 조회한다.")
    void retrieveExpenditureList_period() throws Exception {
        // given
        String accessToken = accessToken1;
        Member member = member1;
        String beginDate = "2023-11-01T13:30:00";
        String endDate = "2023-11-05T15:30:00";
        Long minAmount = null;
        Long maxAmount = null;
        String category = null;

        List<Expenditure> expenditureList = filteredExpenditureList(allExpenditureList, member,
                LocalDateTime.parse(beginDate, FORMATTER), LocalDateTime.parse(endDate, FORMATTER),
                minAmount, maxAmount, category);
        Map<CategoryType, Long> expenditureByCategory = totalExpenditureByCategory(expenditureList);
        Long totalAmount = totalExpenditureAmount(expenditureByCategory);
        List<Tuple> expected = expenditureListToExpectedList(expenditureList);

        // when, then
        String responseBody = mockMvc.perform(get(URL).header(AUTHORIZATION, accessToken)
                                                      .param(BEGIN_DATE, beginDate)
                                                      .param(END_DATE, endDate))
                                     .andDo(print())
                                     .andExpect(status().isOk())
                                     .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                     .andExpect(jsonPath("$.success").value(true))
                                     .andExpect(jsonPath("$.error").doesNotExist())
                                     .andReturn()
                                     .getResponse()
                                     .getContentAsString(StandardCharsets.UTF_8);

        ApiResponse<ExpenditureListResponse> actual = objectMapper.readValue(responseBody, new TypeReference<>() {});

        assertAll(
                () -> assertThat(actual.getResponse().getTotalAmount()).isEqualTo(totalAmount),
                () -> actual.getResponse().getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(actual.getResponse().getExpenditureList().size()).isEqualTo(expenditureList.size()),
                () -> assertThat(actual.getResponse().getExpenditureList())
                        .extracting(e -> e.getCategory(), e -> e.getAmount(), e -> e.getExpenditureAt())
                        .containsExactlyElementsOf(expected)
        );
    }

    @Test
    @DisplayName("시작 일시와 종료 일시를 포함한 기간 내의 지출 목록을 조회한다.")
    void retrieveExpenditureList_closedIntervalPeriod() throws Exception {
        // given
        String accessToken = accessToken1;
        Member member = member1;
        String beginDate = "2023-11-02T12:30:00";
        String endDate = "2023-11-02T15:30:00";
        Long minAmount = null;
        Long maxAmount = null;
        String category = null;

        List<Expenditure> expenditureList = filteredExpenditureList(allExpenditureList, member,
                LocalDateTime.parse(beginDate, FORMATTER), LocalDateTime.parse(endDate, FORMATTER),
                minAmount, maxAmount, category);
        Map<CategoryType, Long> expenditureByCategory = totalExpenditureByCategory(expenditureList);
        Long totalAmount = totalExpenditureAmount(expenditureByCategory);
        List<Tuple> expected = expenditureListToExpectedList(expenditureList);

        // when, then
        String responseBody = mockMvc.perform(get(URL).header(AUTHORIZATION, accessToken)
                                                      .param(BEGIN_DATE, beginDate)
                                                      .param(END_DATE, endDate))
                                     .andDo(print())
                                     .andExpect(status().isOk())
                                     .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                     .andExpect(jsonPath("$.success").value(true))
                                     .andExpect(jsonPath("$.error").doesNotExist())
                                     .andReturn()
                                     .getResponse()
                                     .getContentAsString(StandardCharsets.UTF_8);

        ApiResponse<ExpenditureListResponse> actual = objectMapper.readValue(responseBody, new TypeReference<>() {});

        assertAll(
                () -> assertThat(actual.getResponse().getTotalAmount()).isEqualTo(totalAmount),
                () -> actual.getResponse().getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(actual.getResponse().getExpenditureList().size()).isEqualTo(expenditureList.size()),
                () -> assertThat(actual.getResponse().getExpenditureList())
                        .extracting(e -> e.getCategory(), e -> e.getAmount(), e -> e.getExpenditureAt())
                        .containsExactlyElementsOf(expected)
        );
    }

    @Test
    @DisplayName("금액 범위를 지정하여 지출 목록을 조회한다.")
    void retrieveExpenditureList_amountRange() throws Exception {
        // given
        String accessToken = accessToken1;
        Member member = member1;
        String beginDate = "2023-11-01T13:30:00";
        String endDate = "2023-11-05T15:30:00";
        Long minAmount = 30000L;
        Long maxAmount = 50000L;
        String category = null;

        List<Expenditure> expenditureList = filteredExpenditureList(allExpenditureList, member,
                LocalDateTime.parse(beginDate, FORMATTER), LocalDateTime.parse(endDate, FORMATTER),
                minAmount, maxAmount, category);
        Map<CategoryType, Long> expenditureByCategory = totalExpenditureByCategory(expenditureList);
        Long totalAmount = totalExpenditureAmount(expenditureByCategory);
        List<Tuple> expected = expenditureListToExpectedList(expenditureList);

        // when, then
        String responseBody = mockMvc.perform(get(URL).header(AUTHORIZATION, accessToken)
                                                      .param(BEGIN_DATE, beginDate)
                                                      .param(END_DATE, endDate)
                                                      .param(MIN_AMOUNT, minAmount.toString())
                                                      .param(MAX_AMOUNT, maxAmount.toString()))
                                     .andDo(print())
                                     .andExpect(status().isOk())
                                     .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                     .andExpect(jsonPath("$.success").value(true))
                                     .andExpect(jsonPath("$.error").doesNotExist())
                                     .andReturn()
                                     .getResponse()
                                     .getContentAsString(StandardCharsets.UTF_8);

        ApiResponse<ExpenditureListResponse> actual = objectMapper.readValue(responseBody, new TypeReference<>() {});

        assertAll(
                () -> assertThat(actual.getResponse().getTotalAmount()).isEqualTo(totalAmount),
                () -> actual.getResponse().getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(actual.getResponse().getExpenditureList().size()).isEqualTo(expenditureList.size()),
                () -> assertThat(actual.getResponse().getExpenditureList())
                        .extracting(e -> e.getCategory(), e -> e.getAmount(), e -> e.getExpenditureAt())
                        .containsExactlyElementsOf(expected)
        );
    }

    @Test
    @DisplayName("지정한 카테고리에 대한 지출 목록을 조회한다.")
    void retrieveExpenditureList_category() throws Exception {
        // given
        String accessToken = accessToken1;
        Member member = member1;
        String beginDate = "2023-11-01T00:00:00";
        String endDate = "2023-11-29T22:30:00";
        String category = CategoryType.EDUCATION.toString();

        List<Expenditure> expenditureList = filteredExpenditureList(allExpenditureList, member,
                LocalDateTime.parse(beginDate, FORMATTER), LocalDateTime.parse(endDate, FORMATTER),
                null, null, category);
        Map<CategoryType, Long> expenditureByCategory = totalExpenditureByCategory(expenditureList);
        Long totalAmount = totalExpenditureAmount(expenditureByCategory);
        List<Tuple> expected = expenditureListToExpectedList(expenditureList);

        // when, then
        String responseBody = mockMvc.perform(get(URL).header(AUTHORIZATION, accessToken)
                                                      .param(BEGIN_DATE, beginDate)
                                                      .param(END_DATE, endDate)
                                                      .param(CATEGORY, category))
                                     .andDo(print())
                                     .andExpect(status().isOk())
                                     .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                     .andExpect(jsonPath("$.success").value(true))
                                     .andExpect(jsonPath("$.error").doesNotExist())
                                     .andReturn()
                                     .getResponse()
                                     .getContentAsString(StandardCharsets.UTF_8);

        ApiResponse<ExpenditureListResponse> actual = objectMapper.readValue(responseBody, new TypeReference<>() {});

        assertAll(
                () -> assertThat(actual.getResponse().getTotalAmount()).isEqualTo(totalAmount),
                () -> actual.getResponse().getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(actual.getResponse().getExpenditureList().size()).isEqualTo(expenditureList.size()),
                () -> assertThat(actual.getResponse().getExpenditureList())
                        .extracting(e -> e.getCategory(), e -> e.getAmount(), e -> e.getExpenditureAt())
                        .containsExactlyElementsOf(expected)
        );
    }

    @Test
    @DisplayName("지출 목록을 조회할 때 합계 제외를 선택한 지출은 목록에는 포함되지만 지출 합계에서는 제외된다.")
    void retrieveExpenditureList_excludeFromTotal() throws Exception {
        // given
        String accessToken = accessToken2;
        Member member = member2;
        String beginDate = "2023-11-01T01:30:15";
        String endDate = "2023-11-29T15:30:33";

        List<Expenditure> expenditureList = filteredExpenditureList(allExpenditureList, member,
                LocalDateTime.parse(beginDate, FORMATTER), LocalDateTime.parse(endDate, FORMATTER),
                null, null, null);
        Map<CategoryType, Long> expenditureByCategory = totalExpenditureByCategory(expenditureList);
        Long totalAmount = totalExpenditureAmount(expenditureByCategory);
        List<Tuple> expected = expenditureListToExpectedList(expenditureList);

        // when, then
        String responseBody = mockMvc.perform(get(URL).header(AUTHORIZATION, accessToken)
                                                      .param(BEGIN_DATE, beginDate)
                                                      .param(END_DATE, endDate))
                                     .andDo(print())
                                     .andExpect(status().isOk())
                                     .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                     .andExpect(jsonPath("$.success").value(true))
                                     .andExpect(jsonPath("$.error").doesNotExist())
                                     .andReturn()
                                     .getResponse()
                                     .getContentAsString(StandardCharsets.UTF_8);

        ApiResponse<ExpenditureListResponse> actual = objectMapper.readValue(responseBody, new TypeReference<>() {});

        assertAll(
                () -> assertThat(actual.getResponse().getTotalAmount()).isEqualTo(totalAmount),
                () -> actual.getResponse().getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(actual.getResponse().getExpenditureList().size()).isEqualTo(expenditureList.size()),
                () -> assertThat(actual.getResponse().getExpenditureList())
                        .extracting(e -> e.getCategory(), e -> e.getAmount(), e -> e.getExpenditureAt())
                        .containsExactlyElementsOf(expected)
        );
    }

    @Test
    @DisplayName("지출 목록을 조회할 때 기간 정보가 없으면 COMMON_INVALID_PARAMETERS 예외가 발생한다.")
    void retrieveExpenditureList_requiredQueryParameterMissing_exception() throws Exception {
        // given
        String accessToken = accessToken1;
        ErrorCode expectedErrorCode = ErrorCode.COMMON_INVALID_PARAMETERS;

        // when, then
        mockMvc.perform(get(URL).header(AUTHORIZATION, accessToken))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expectedErrorCode.getCode()))
               .andExpect(jsonPath("$.error.message.expenditureListRetrieveRequest").exists());
    }

    @Test
    @DisplayName("잘못된 날짜 형식으로 기간 내의 지출 목록을 조회하면 COMMON_INVALID_PARAMETERS 예외가 발생한다.")
    void retrieveExpenditureList_invalidDateTimeFormat_exception() throws Exception {
        // given
        String accessToken = accessToken1;
        String beginDate = "2023-11-01T13:30:00 invalid";
        String endDate = "2023-11-27T15:30:00 test";
        ErrorCode expectedErrorCode = ErrorCode.COMMON_INVALID_PARAMETERS;

        // when, then
        mockMvc.perform(get(URL).header(AUTHORIZATION, accessToken)
                                .param(BEGIN_DATE, beginDate)
                                .param(END_DATE, endDate))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expectedErrorCode.getCode()))
               .andExpect(jsonPath("$.error.message.beginDate").value(expectedErrorCode.getMessage()))
               .andExpect(jsonPath("$.error.message.endDate").value(expectedErrorCode.getMessage()));
    }

    @Test
    @DisplayName("지출 목록을 조회할 때 종료 일시가 시작 일시보다 빠르면 COMMON_INVALID_PARAMETERS 예외가 발생한다.")
    void retrieveExpenditureList_endDateIsBeforeBeginDate_exception() throws Exception {
        // given: 기간이 end < begin이 되도록 설정
        String accessToken = accessToken1;
        String beginDate = "2023-11-05T13:30:22";
        String endDate = "2023-11-03T15:43:51";
        ErrorCode expectedErrorCode = ErrorCode.COMMON_INVALID_PARAMETERS;

        // when, then
        mockMvc.perform(get(URL).header(AUTHORIZATION, accessToken)
                                .param(BEGIN_DATE, beginDate)
                                .param(END_DATE, endDate))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expectedErrorCode.getCode()))
               .andExpect(jsonPath("$.error.message.expenditureListRetrieveRequest").exists());
    }

    @Test
    @DisplayName("지출 목록을 조회할 때 최대 금액이 최소 금액보다 적으면 COMMON_INVALID_PARAMETERS 예외가 발생한다.")
    void retrieveExpenditureList_maxAmountIsLessThanMinAmount_exception() throws Exception {
        // given: 금액이 max < min 되도록 설정
        String accessToken = accessToken1;
        String beginDate = "2023-11-02T13:30:22";
        String endDate = "2023-11-10T15:43:51";
        String minAmount = "50000";
        String maxAmount = "10000";
        ErrorCode expectedErrorCode = ErrorCode.COMMON_INVALID_PARAMETERS;

        // when, then
        mockMvc.perform(get(URL).header(AUTHORIZATION, accessToken)
                                .param(BEGIN_DATE, beginDate)
                                .param(END_DATE, endDate)
                                .param(MIN_AMOUNT, minAmount)
                                .param(MAX_AMOUNT, maxAmount))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expectedErrorCode.getCode()))
               .andExpect(jsonPath("$.error.message.expenditureListRetrieveRequest").exists());
    }

    @Test
    @DisplayName("지출 목록을 조회할 때 금액 범위가 0원 이상 1조 이하가 아니면 COMMON_INVALID_PARAMETERS 예외가 발생한다.")
    void retrieveExpenditureList_invalidAmountRange_exception() throws Exception {
        // given
        String accessToken = accessToken1;
        String beginDate = "2023-11-02T13:30:22";
        String endDate = "2023-11-10T15:43:51";
        String minAmount = "-200";
        String maxAmount = "1000000000007";
        ErrorCode expectedErrorCode = ErrorCode.COMMON_INVALID_PARAMETERS;

        // when, then
        mockMvc.perform(get(URL).header(AUTHORIZATION, accessToken)
                                .param(BEGIN_DATE, beginDate)
                                .param(END_DATE, endDate)
                                .param(MIN_AMOUNT, minAmount)
                                .param(MAX_AMOUNT, maxAmount))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expectedErrorCode.getCode()))
               .andExpect(jsonPath("$.error.message.minAmount").exists())
               .andExpect(jsonPath("$.error.message.maxAmount").exists());
    }


    @Test
    @DisplayName("지출 삭제 성공")
    void deleteExpenditure() throws Exception {
        // given
        Long expenditrueId = 1L;
        String accessToken = ACCESS_TOKEN_MEMBER1;

        // when, then
        mockMvc.perform(delete(DELETE_URL, expenditrueId)
                       .header(AUTHORIZATION, accessToken))
               .andDo(print())
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않은 지출 정보를 삭제할 때 EXPENDITURE_NOT_FOUND 예외가 발생한다.")
    void deleteExpenditure_expenditureNotFound_exception() throws Exception {
        // given
        Long expenditrueId = 2342345L;
        String accessToken = ACCESS_TOKEN_MEMBER1;
        ErrorCode expected = EXPENDITURE_NOT_FOUND;

        // when, then
        mockMvc.perform(delete(DELETE_URL, expenditrueId)
                       .header(AUTHORIZATION, accessToken))
               .andDo(print())
               .andExpect(status().isNotFound())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expected.getCode()))
               .andExpect(jsonPath("$.error.message").value(expected.getMessage()));
    }

    @Test
    @DisplayName("내가 작성하지 않은 다른 유저의 지출 정보를 삭제할 때 EXPENDITURE_MEMBER_MISMATCH 예외가 발생한다.")
    void deleteExpenditure_expenditureMemberMismatch_exception() throws Exception {
        // given: member1이 만든 지출 정보를 member2가 삭제하도록 설정
        Long expenditrueId = 1L;
        String accessTokenMember2 = JwtFixture.ACCESS_TOKEN_MEMBER2;
        ErrorCode expected = EXPENDITURE_MEMBER_MISMATCH;

        // when, then
        mockMvc.perform(delete(DELETE_URL, expenditrueId)
                       .header(AUTHORIZATION, accessTokenMember2))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.response").doesNotExist())
               .andExpect(jsonPath("$.error.code").value(expected.getCode()))
               .andExpect(jsonPath("$.error.message").value(expected.getMessage()));
    }

}