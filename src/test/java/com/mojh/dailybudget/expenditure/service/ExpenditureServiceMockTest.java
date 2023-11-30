package com.mojh.dailybudget.expenditure.service;

import com.mojh.dailybudget.category.CategoryFixture;
import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.category.service.CategorySerivce;
import com.mojh.dailybudget.common.exception.DailyBudgetAppException;
import com.mojh.dailybudget.common.exception.ErrorCode;
import com.mojh.dailybudget.common.tmp.TestUilts;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureListRetrieveRequest;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureUpdateRequest;
import com.mojh.dailybudget.expenditure.dto.response.ExpenditureListResponse;
import com.mojh.dailybudget.expenditure.dto.response.ExpenditureSummaryResponse;
import com.mojh.dailybudget.expenditure.repository.ExpenditureRepository;
import com.mojh.dailybudget.member.domain.Member;
import com.mojh.dailybudget.member.MemberFixture;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mojh.dailybudget.category.domain.CategoryType.EDUCATION;
import static com.mojh.dailybudget.category.domain.CategoryType.FOOD;
import static com.mojh.dailybudget.category.domain.CategoryType.SHOPPING;
import static com.mojh.dailybudget.category.domain.CategoryType.UNCATEGORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("지출 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ExpenditureServiceMockTest {

    @Mock
    ExpenditureRepository expenditureRepository;

    @Mock
    CategorySerivce categorySerivce;

    @InjectMocks
    ExpenditureService expenditureService;

    static Member member1;
    static Member member2;
    static Member member3;
    static Map<CategoryType, Category> categoryMap;
    static List<Expenditure> expenditureListFixture;

    @BeforeAll
    static void beforeAll() {
        member1 = MemberFixture.MEMBER1();
        member2 = MemberFixture.MEMBER2();
        member3 = MemberFixture.MEMBER3();
        categoryMap = CategoryFixture.CATEGORY;
        expenditureListFixture = new ArrayList<>();

        Expenditure expenditure;

        for (int idx = 1; idx <= 5; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 02, 10 + idx, 30);
            expenditure = Expenditure.builder()
                                     .member(member1)
                                     .category(categoryMap.get(FOOD))
                                     .amount(10000L * idx)
                                     .memo("음식 " + idx)
                                     .excludeFromTotal(false)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureListFixture.add(expenditure);
        }

        for (int idx = 6; idx <= 7; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 03, 10 + idx, 30);
            expenditure = Expenditure.builder()
                                     .member(member1)
                                     .category(categoryMap.get(EDUCATION))
                                     .amount(20000L * idx)
                                     .memo("강의 구매 " + idx)
                                     .excludeFromTotal(false)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureListFixture.add(expenditure);
        }

        for (int idx = 8; idx <= 9; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 06, 10 + idx, 30);
            expenditure = Expenditure.builder()
                                     .member(member2)
                                     .category(categoryMap.get(UNCATEGORIZED))
                                     .amount(10000L * idx)
                                     .memo("기타 " + idx)
                                     .excludeFromTotal(false)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureListFixture.add(expenditure);
        }

        for (int idx = 10; idx <= 11; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 07, 10 + idx, 30);
            expenditure = Expenditure.builder()
                                     .member(member2)
                                     .category(categoryMap.get(UNCATEGORIZED))
                                     .amount(10000L * idx)
                                     .memo("기타 " + idx)
                                     .excludeFromTotal(true)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureListFixture.add(expenditure);
        }

        for (int idx = 12; idx <= 15; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 9, 10, idx);
            expenditure = Expenditure.builder()
                                     .member(member2)
                                     .category(categoryMap.get(SHOPPING))
                                     .amount(1000L * idx)
                                     .memo("쇼핑 " + idx)
                                     .excludeFromTotal(false)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureListFixture.add(expenditure);
        }

    }



    private Expenditure captureExpenditure(Expenditure expenditure) {
        return Expenditure.builder()
                          .category(expenditure.getCategory())
                          .amount(expenditure.getAmount())
                          .memo(expenditure.getMemo())
                          .excludeFromTotal(expenditure.getExcludeFromTotal())
                          .expenditureAt(expenditure.getExpenditureAt())
                          .build();
    }

    private List<Expenditure> filteredExpenditureList(List<Expenditure> expenditureList, Member member,
                                                      LocalDateTime beginDate, LocalDateTime endDate,
                                                      Long minAmount, Long maxAmount, String category) {
        return expenditureList.stream()
                              .filter(e -> e.getMember().equals(member))
                              .filter(e -> category == null || e.getCategoryType().toString().equals(category))
                              .filter(e -> !e.getExpenditureAt().isBefore(beginDate)
                                      && !e.getExpenditureAt().isAfter(endDate))
                              .filter(e -> minAmount <= e.getAmount() && e.getAmount() <= maxAmount)
                              .sorted(Comparator.comparing(Expenditure::getExpenditureAt).reversed())
                              .collect(Collectors.toList());
    }

    /**
     * 카테고리 별 지출 합계 계산
     * @return
     */
    private Map<CategoryType, Long> totalExpenditureByCategory(List<Expenditure> expenditureList) {
        return expenditureList.stream()
                              .filter(expenditure -> !expenditure.getExcludeFromTotal())
                              .collect(Collectors.groupingBy(Expenditure::getCategoryType,
                                      Collectors.summingLong(Expenditure::getAmount)));
    }

    /**
     * 카테고리 별 지출 금액을 합친 총 지출 금액 계산
     * @param totalExpenditureByCategory
     * @return
     */
    private Long totalExpenditureAmount(Map<CategoryType, Long> totalExpenditureByCategory) {
        return totalExpenditureByCategory.values().stream()
                                         .mapToLong(Long::longValue)
                                         .sum();
    }

    /**
     * 지출 목록 데이터 검증을 위해 비교할 필드들만 설정한 지출 목록 예상 tuple list
     * @param expenditureList
     * @return
     */
    private List<Tuple> expenditureListToExpectedList(List<Expenditure> expenditureList) {
        return expenditureList.stream()
                              .map(expenditure -> tuple(
                                      expenditure.getCategoryType(),
                                      expenditure.getAmount(),
                                      expenditure.getExpenditureAt()))
                              .collect(Collectors.toList());
    }

    @Test
    @DisplayName("조회 조건에 맞는 지출 목록이 없을 때 지출 합계가 0원이고 비어있는 지출 목록이 반환된다.")
    void retrieveExpenditureList_emptyList() {
        // given: 지출 정보가 없는 member3에 대한 지출 목록 조회 하도록 설정
        Member member = member3;
        List<Expenditure> expenditureList = Collections.EMPTY_LIST;
        Map<CategoryType, Long> expenditureByCategory = totalExpenditureByCategory(expenditureList);
        Long totalAmount = 0L;
        ExpenditureListRetrieveRequest request = new ExpenditureListRetrieveRequest(LocalDateTime.of(2023, 11, 01, 1, 30),
                LocalDateTime.of(2023, 11, 28, 15, 30), 0L, 500000L, null);

        given(expenditureRepository.retrieveExpenditureList(member, request.getCategory(), request.getBeginDate(),
                request.getEndDate(), request.getMinAmount(), request.getMaxAmount())).willReturn(expenditureList);

        // when
        ExpenditureListResponse result = expenditureService.retrieveExpenditureList(request, member);

        // then
        assertAll(
                () -> assertThat(result.getTotalAmount()).isEqualTo(totalAmount),
                () -> result.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(result.getExpenditureList()).isEmpty()
        );
    }

    @Test
    @DisplayName("카테고리를 지정하지 않고 지출 목록을 조회하면 모든 카테고리의 지출 목록이 최신순으로 조회된다.")
    void retrieveExpenditureList_categoryNull() {
        // given
        Member member = member1;
        LocalDateTime beginDate = LocalDateTime.of(2023, 11, 1, 1, 30);
        LocalDateTime endDate = LocalDateTime.of(2023, 11, 5, 15, 30);
        Long minAmount = 3000L;
        Long maxAmount = 1000000L;
        String category = null;
        ExpenditureListRetrieveRequest request
                = new ExpenditureListRetrieveRequest(beginDate, endDate, minAmount, maxAmount, category);
        List<Expenditure> expenditureList = filteredExpenditureList(expenditureListFixture, member,
                request.getBeginDate(), request.getEndDate(), request.getMinAmount(), request.getMaxAmount(), category);

        Map<CategoryType, Long> expenditureByCategory = totalExpenditureByCategory(expenditureList);
        Long totalAmount = totalExpenditureAmount(expenditureByCategory);
        List<Tuple> expected = expenditureListToExpectedList(expenditureList);

        given(expenditureRepository.retrieveExpenditureList(member, request.getCategory(), request.getBeginDate(),
                request.getEndDate(), request.getMinAmount(), request.getMaxAmount())).willReturn(expenditureList);

        // when
        ExpenditureListResponse result = expenditureService.retrieveExpenditureList(request, member);

        // then
        assertAll(
                () -> assertThat(result.getTotalAmount()).isEqualTo(totalAmount),
                () -> result.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(result.getExpenditureList().size()).isEqualTo(7),
                () -> assertThat(result.getExpenditureList())
                        .extracting(e -> e.getCategory(), e -> e.getAmount(), e -> e.getExpenditureAt())
                        .containsExactlyElementsOf(expected)
        );
    }

    @Test
    @DisplayName("기간 내의 지출 목록을 조회한다.")
    void retrieveExpenditureList_period() {
        // given: 기간 외에 필드 최소, 최대 금액과 카테고리는 null로 default 값 적용
        // 금액은 0 ~ 1조, category는 전체 카테고리 조회
        Member member = member2;
        LocalDateTime beginDate = LocalDateTime.of(2023, 11, 5, 1, 30);
        LocalDateTime endDate = LocalDateTime.of(2023, 11, 8, 10, 30);
        Long minAmount = null;
        Long maxAmount = null;
        String category = null;
        ExpenditureListRetrieveRequest request
                = new ExpenditureListRetrieveRequest(beginDate, endDate, minAmount, maxAmount, category);
        List<Expenditure> expenditureList = filteredExpenditureList(expenditureListFixture, member,
                request.getBeginDate(), request.getEndDate(), request.getMinAmount(), request.getMaxAmount(), category);

        Map<CategoryType, Long> expenditureByCategory = totalExpenditureByCategory(expenditureList);
        Long totalAmount = totalExpenditureAmount(expenditureByCategory);
        List<Tuple> expected = expenditureListToExpectedList(expenditureList);

        given(expenditureRepository.retrieveExpenditureList(member, request.getCategory(), request.getBeginDate(),
                request.getEndDate(), request.getMinAmount(), request.getMaxAmount())).willReturn(expenditureList);

        // when
        ExpenditureListResponse result = expenditureService.retrieveExpenditureList(request, member);

        // then:
        assertAll(
                () -> assertThat(result.getTotalAmount()).isEqualTo(totalAmount),
                () -> result.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(result.getExpenditureList().size()).isEqualTo(4),
                () -> assertThat(result.getExpenditureList())
                        .extracting(e -> e.getCategory(), e -> e.getAmount(), e -> e.getExpenditureAt())
                        .containsExactlyElementsOf(expected)
        );
    }

    @Test
    @DisplayName("최소, 최대 금액 범위 내의 지출 목록을 조회한다.")
    void retrieveExpenditureList_amountRange() {
        // given
        Member member = member2;
        LocalDateTime beginDate = LocalDateTime.of(2023, 11, 1, 1, 30);
        LocalDateTime endDate = LocalDateTime.of(2023, 11, 20, 15, 30);
        Long minAmount = 2500L;
        Long maxAmount = 20000L;
        String category = null;
        ExpenditureListRetrieveRequest request
                = new ExpenditureListRetrieveRequest(beginDate, endDate, minAmount, maxAmount, category);
        List<Expenditure> expenditureList = filteredExpenditureList(expenditureListFixture, member,
                request.getBeginDate(), request.getEndDate(), request.getMinAmount(), request.getMaxAmount(), category);

        Map<CategoryType, Long> expenditureByCategory = totalExpenditureByCategory(expenditureList);
        Long totalAmount = totalExpenditureAmount(expenditureByCategory);
        List<Tuple> expected = expenditureListToExpectedList(expenditureList);

        given(expenditureRepository.retrieveExpenditureList(member, request.getCategory(), request.getBeginDate(),
                request.getEndDate(), request.getMinAmount(), request.getMaxAmount())).willReturn(expenditureList);

        // when
        ExpenditureListResponse result = expenditureService.retrieveExpenditureList(request, member);

        // then
        assertAll(
                () -> assertThat(result.getTotalAmount()).isEqualTo(totalAmount),
                () -> result.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(result.getExpenditureList().size()).isEqualTo(4),

                () -> assertThat(result.getExpenditureList())
                        .extracting(e -> e.getCategory(), e -> e.getAmount(), e -> e.getExpenditureAt())
                        .containsExactlyElementsOf(expected)
        );
    }

    @Test
    @DisplayName("특정 카테고리의 지출 목록을 조회한다.")
    void retrieveExpenditureList_categoryType() {
        // given: FOOD 만 조회
        Member member = member1;
        LocalDateTime beginDate = LocalDateTime.of(2023, 11, 1, 1, 30);
        LocalDateTime endDate = LocalDateTime.of(2023, 11, 20, 15, 30);
        Long minAmount = null;
        Long maxAmount = null;
        String category = FOOD.toString();
        ExpenditureListRetrieveRequest request
                = new ExpenditureListRetrieveRequest(beginDate, endDate, minAmount, maxAmount, category);
        List<Expenditure> expenditureList = filteredExpenditureList(expenditureListFixture, member,
                request.getBeginDate(), request.getEndDate(), request.getMinAmount(), request.getMaxAmount(), category);

        Map<CategoryType, Long> expenditureByCategory = totalExpenditureByCategory(expenditureList);
        Long totalAmount = totalExpenditureAmount(expenditureByCategory);
        List<Tuple> expected = expenditureListToExpectedList(expenditureList);

        given(expenditureRepository.retrieveExpenditureList(member, request.getCategory(), request.getBeginDate(),
                request.getEndDate(), request.getMinAmount(), request.getMaxAmount())).willReturn(expenditureList);

        // when
        ExpenditureListResponse result = expenditureService.retrieveExpenditureList(request, member);

        // then
        assertAll(
                () -> assertThat(result.getTotalAmount()).isEqualTo(totalAmount),
                () -> result.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(result.getExpenditureList().size()).isEqualTo(5),

                () -> assertThat(result.getExpenditureList())
                        .extracting(e -> e.getCategory(), e -> e.getAmount(), e -> e.getExpenditureAt())
                        .containsExactlyElementsOf(expected)
        );
    }

    @Test
    @DisplayName("지출 목록을 조회할 때 합계 제외를 선택한 지출은 목록에는 포함되지만 지출 합계에서는 제외된다.")
    void retrieveExpenditureList_excludeFromTotal() {
        // given: excludeFromTotal = true인 지출 포함
        Member member = member2;
        LocalDateTime beginDate = LocalDateTime.of(2023, 11, 1, 1, 30);
        LocalDateTime endDate = LocalDateTime.of(2023, 11, 29, 15, 30);
        Long minAmount = null;
        Long maxAmount = 500000L;
        String category = null;
        ExpenditureListRetrieveRequest request
                = new ExpenditureListRetrieveRequest(beginDate, endDate, minAmount, maxAmount, category);
        List<Expenditure> expenditureList = filteredExpenditureList(expenditureListFixture, member,
                request.getBeginDate(), request.getEndDate(), request.getMinAmount(), request.getMaxAmount(), category);

        Map<CategoryType, Long> expenditureByCategory = totalExpenditureByCategory(expenditureList);
        Long totalAmount = totalExpenditureAmount(expenditureByCategory);
        List<Tuple> expected = expenditureListToExpectedList(expenditureList);

        given(expenditureRepository.retrieveExpenditureList(member, request.getCategory(), request.getBeginDate(),
                request.getEndDate(), request.getMinAmount(), request.getMaxAmount())).willReturn(expenditureList);

        // when
        ExpenditureListResponse result = expenditureService.retrieveExpenditureList(request, member);

        // then
        assertAll(
                () -> assertThat(result.getTotalAmount()).isEqualTo(totalAmount),
                () -> result.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(result.getExpenditureList().size()).isEqualTo(8),
                () -> assertThat(result.getExpenditureList())
                        .extracting(e -> e.getCategory(), e -> e.getAmount(), e -> e.getExpenditureAt())
                        .containsExactlyElementsOf(expected)
        );
    }

    @Test
    @DisplayName("지출 정보를 수정할 때 수정 요청한 필드만 변경한다.")
    void updateExpenditure_patch_true() {
        // given: 카테고리, 금액, 메모 변경 요청하도록 설정, 카테고리는 기존 값과 동일
        Expenditure expenditure = expenditureListFixture.get(0);
        Long expenditureId = expenditure.getId();
        Expenditure captured = captureExpenditure(expenditure);
        ExpenditureUpdateRequest requet = ExpenditureUpdateRequest.builder()
                                                                  .category(expenditure.getCategoryType())
                                                                  .amount(expenditure.getAmount() + 10000L)
                                                                  .memo(expenditure.getMemo() + " 수정")
                                                                  .build();
        given(categorySerivce.findByCategoryType(requet.getCategory())).willReturn(expenditure.getCategory());
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(expenditure));

        // when
        boolean result = expenditureService.updateExpenditure(requet, member1, expenditureId);

        // then: category는 기존의 값과 같았고, 지출 일시와 합계 제외는 수정 요청을 하지 않아서 변경되지 않는다.
        // 수정 요청한 금액과 메모만 변경됨.
        assertAll(
                () -> assertThat(result).isTrue(),
                () -> assertThat(expenditure.getCategory()).isEqualTo(captured.getCategory()),
                () -> assertThat(expenditure.getAmount()).isEqualTo(requet.getAmount()),
                () -> assertThat(expenditure.getMemo()).isEqualTo(requet.getMemo()),
                () -> assertThat(expenditure.getExcludeFromTotal()).isEqualTo(captured.getExcludeFromTotal()),
                () -> assertThat(expenditure.getExpenditureAt()).isEqualTo(captured.getExpenditureAt()),
                () -> verify(categorySerivce).findByCategoryType(requet.getCategory()),
                () -> verify(expenditureRepository).findById(expenditureId)
        );
    }

    @Test
    @DisplayName("지출 정보를 수정할 때 변경할 필드가 없으면 요청이 실패한 것은 아니나 patch 결과는 false.")
    void updateExpenditure_patch_false() {
        // given: 카테고리 수정 요청을 했지만 기존의 값과 같을 때 변경할 필드 없음
        Expenditure expenditure = expenditureListFixture.get(0);
        Long expenditureId = expenditure.getId();
        Expenditure captured = captureExpenditure(expenditure);
        ExpenditureUpdateRequest requet = ExpenditureUpdateRequest.builder()
                                                                  .category(FOOD)
                                                                  .build();
        given(categorySerivce.findByCategoryType(requet.getCategory())).willReturn(expenditure.getCategory());
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(expenditure));

        // when
        boolean result = expenditureService.updateExpenditure(requet, member1, expenditureId);

        // then: 카테고리는 기존 값과 변경 요청값이 동일하고, 이외의 필드는 수정 요청을 하지 않아 변경한 필드가 없음
        assertAll(
                () -> assertThat(result).isFalse(),
                () -> assertThat(expenditure.getCategory()).isEqualTo(captured.getCategory()),
                () -> assertThat(expenditure.getAmount()).isEqualTo(captured.getAmount()),
                () -> assertThat(expenditure.getMemo()).isEqualTo(captured.getMemo()),
                () -> assertThat(expenditure.getExcludeFromTotal()).isEqualTo(captured.getExcludeFromTotal()),
                () -> assertThat(expenditure.getExpenditureAt()).isEqualTo(captured.getExpenditureAt()),
                () -> verify(categorySerivce).findByCategoryType(requet.getCategory()),
                () -> verify(expenditureRepository).findById(expenditureId)
        );
    }

    @Test
    @DisplayName("내가 작성하지 않은 다른 사람의 지출 정보를 수정할 때 EXPENDITURE_MEMBER_MISMATCH 예외가 발생한다.")
    void updateExpenditure_throw_expenditureMemberMismatch() {
        // given
        Expenditure expenditure = expenditureListFixture.get(0);
        Long expenditureId = expenditure.getId();
        Member otherMember = MemberFixture.MEMBER2();
        ExpenditureUpdateRequest requet = ExpenditureUpdateRequest.builder()
                                                                  .category(FOOD)
                                                                  .amount(30000L)
                                                                  .build();
        given(categorySerivce.findByCategoryType(requet.getCategory())).willReturn(expenditure.getCategory());
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(expenditure));

        // when
        DailyBudgetAppException ex = assertThrows(DailyBudgetAppException.class, () -> {
            expenditureService.updateExpenditure(requet, otherMember, expenditureId);
        });

        // then
        assertAll(
                () -> verify(categorySerivce).findByCategoryType(requet.getCategory()),
                () -> verify(expenditureRepository).findById(expenditureId),
                () -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EXPENDITURE_MEMBER_MISMATCH)
        );
    }

    @Test
    @DisplayName("지출 정보를 삭제한다.")
    void deleteExpenditure_success() {
        // given
        Expenditure expenditure = expenditureListFixture.get(0);
        Long expenditureId = expenditure.getId();
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(expenditure));

        // when
        expenditureService.deleteExpenditure(member1, expenditureId);

        // then: category는 기존의 값과 같았고, 지출 일시와 합계 제외는 수정 요청을 하지 않아서 변경되지 않는다.
        // 수정 요청한 금액과 메모만 변경됨.
        assertAll(
                () -> verify(expenditureRepository).findById(expenditureId),
                () -> verify(expenditureRepository).delete(expenditure)
        );
    }

    @Test
    @DisplayName("삭제하려는 지출 정보가 없을 때 EXPENDITURE_NOT_FOUND 예외가 발생한다.")
    void deleteExpenditure_throw_expenditureNotFound() {
        // given
        Expenditure expenditure = expenditureListFixture.get(0);
        Long expenditureId = expenditure.getId();
        Long otherId = expenditureId + 1000L;
        given(expenditureRepository.findById(otherId)).willReturn(Optional.empty());

        // when
        DailyBudgetAppException ex = assertThrows(DailyBudgetAppException.class, () -> {
            expenditureService.deleteExpenditure(member1, otherId);
        });

        // then
        assertAll(
                () -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EXPENDITURE_NOT_FOUND),
                () -> verify(expenditureRepository).findById(otherId),
                () -> verify(expenditureRepository, never()).findById(expenditureId),
                () -> verify(expenditureRepository, never()).delete(expenditure)
        );
    }

    @Test
    @DisplayName("내가 작성하지 않은 다른 유저의 지출 정보는 삭제할 때 EXPENDITURE_MEMBER_MISMATCH 예외가 발생한다.")
    void deleteExpenditure_throw_expenditureMemberMismatch() {
        // given
        Expenditure expenditure = expenditureListFixture.get(0);
        Long expenditureId = expenditure.getId();
        Member otherMember = MemberFixture.MEMBER2();
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(expenditure));

        // when
        DailyBudgetAppException ex = assertThrows(DailyBudgetAppException.class, () -> {
            expenditureService.deleteExpenditure(otherMember, expenditureId);
        });

        // then
        assertAll(
                () -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EXPENDITURE_MEMBER_MISMATCH),
                () -> verify(expenditureRepository).findById(expenditureId),
                () -> verify(expenditureRepository, never()).delete(expenditure)
        );
    }
}