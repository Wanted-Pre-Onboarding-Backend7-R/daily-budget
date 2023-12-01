package com.mojh.dailybudget.expenditure.service;

import com.mojh.dailybudget.category.CategoryFixture;
import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.category.service.CategorySerivce;
import com.mojh.dailybudget.common.exception.DailyBudgetAppException;
import com.mojh.dailybudget.common.exception.ErrorCode;
import com.mojh.dailybudget.expenditure.ExpenditureFixture;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureListRetrieveRequest;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureUpdateRequest;
import com.mojh.dailybudget.expenditure.dto.response.ExpenditureListResponse;
import com.mojh.dailybudget.expenditure.dto.response.ExpenditureResponse;
import com.mojh.dailybudget.expenditure.repository.ExpenditureRepository;
import com.mojh.dailybudget.member.MemberFixture;
import com.mojh.dailybudget.member.domain.Member;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mojh.dailybudget.category.domain.CategoryType.FOOD;
import static com.mojh.dailybudget.expenditure.ExpenditureTestUtils.captureExpenditure;
import static com.mojh.dailybudget.expenditure.ExpenditureTestUtils.expenditureListToExpectedList;
import static com.mojh.dailybudget.expenditure.ExpenditureTestUtils.filteredExpenditureList;
import static com.mojh.dailybudget.expenditure.ExpenditureTestUtils.totalExpenditureAmount;
import static com.mojh.dailybudget.expenditure.ExpenditureTestUtils.totalExpenditureByCategory;
import static org.assertj.core.api.Assertions.assertThat;
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
        expenditureListFixture = ExpenditureFixture.EXPENDITURE_LIST();
    }


    @Test
    @DisplayName("지출의 상세 내용을 조회한다.")
    void retrieveExpenditure() {
        // given
        Member member = member1;
        Expenditure expenditure = expenditureListFixture.get(0);
        Long expenditureId = expenditure.getId();
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(expenditure));

        // when
        ExpenditureResponse actual = expenditureService.retrieveExpenditure(member, expenditureId);

        // then
        assertAll(
                () -> verify(expenditureRepository).findById(expenditureId),
                () -> assertThat(actual.getCategory()).isEqualTo(expenditure.getCategoryType()),
                () -> assertThat(actual.getAmount()).isEqualTo(expenditure.getAmount()),
                () -> assertThat(actual.getMemo()).isEqualTo(expenditure.getMemo()),
                () -> assertThat(actual.getExcludeFromTotal()).isEqualTo(expenditure.getExcludeFromTotal()),
                () -> assertThat(actual.getExpenditureAt()).isEqualTo(expenditure.getExpenditureAt())
        );
    }

    @Test
    @DisplayName("상세 조회 하려는 지출 정보가 없을 때 EXPENDITURE_NOT_FOUND 예외가 발생한다.")
    void retrieveExpenditure_throw_expenditureNotFound() {
        // given
        Member member = member1;
        Expenditure expenditure = expenditureListFixture.get(0);
        Long expenditureId = expenditure.getId();
        Long otherExpenditureId = expenditureId + 10000L;
        given(expenditureRepository.findById(otherExpenditureId)).willReturn(Optional.empty());

        // when
        DailyBudgetAppException ex = assertThrows(DailyBudgetAppException.class, () -> {
            expenditureService.retrieveExpenditure(member, otherExpenditureId);
        });

        // then
        assertAll(
                () -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EXPENDITURE_NOT_FOUND),
                () -> verify(expenditureRepository).findById(otherExpenditureId),
                () -> verify(expenditureRepository, never()).findById(expenditureId)
        );
    }

    @Test
    @DisplayName("다른 사람의 지출 정보를 상세 조회할 때 EXPENDITURE_MEMBER_MISMATCH 예외가 발생한다.")
    void retrieveExpenditure_throw_expenditureMemberMismatch() {
        // given: member2가 member1의 지출 정보를 조회하도록 설정
        Expenditure expenditure = expenditureListFixture.get(0);
        Long expenditureId = expenditure.getId();
        Member member = expenditure.getMember();
        Member otherMember = MemberFixture.MEMBER2();
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(expenditure));

        // when
        DailyBudgetAppException ex = assertThrows(DailyBudgetAppException.class, () -> {
            expenditureService.retrieveExpenditure(otherMember, expenditureId);
        });

        // then
        assertAll(
                () -> assertThat(member).isNotEqualTo(otherMember),
                () -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EXPENDITURE_MEMBER_MISMATCH),
                () -> verify(expenditureRepository).findById(expenditureId)
        );
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
        ExpenditureListResponse actual = expenditureService.retrieveExpenditureList(request, member);

        // then
        assertAll(
                () -> assertThat(actual.getTotalAmount()).isEqualTo(totalAmount),
                () -> actual.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(actual.getExpenditureList()).isEmpty()
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
        ExpenditureListResponse actual = expenditureService.retrieveExpenditureList(request, member);

        // then
        assertAll(
                () -> assertThat(actual.getTotalAmount()).isEqualTo(totalAmount),
                () -> actual.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(actual.getExpenditureList().size()).isEqualTo(7),
                () -> assertThat(actual.getExpenditureList())
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
        ExpenditureListResponse actual = expenditureService.retrieveExpenditureList(request, member);

        // then:
        assertAll(
                () -> assertThat(actual.getTotalAmount()).isEqualTo(totalAmount),
                () -> actual.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(actual.getExpenditureList().size()).isEqualTo(4),
                () -> assertThat(actual.getExpenditureList())
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
        ExpenditureListResponse actual = expenditureService.retrieveExpenditureList(request, member);

        // then
        assertAll(
                () -> assertThat(actual.getTotalAmount()).isEqualTo(totalAmount),
                () -> actual.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(actual.getExpenditureList().size()).isEqualTo(4),

                () -> assertThat(actual.getExpenditureList())
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
        ExpenditureListResponse actual = expenditureService.retrieveExpenditureList(request, member);

        // then
        assertAll(
                () -> assertThat(actual.getTotalAmount()).isEqualTo(totalAmount),
                () -> actual.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(actual.getExpenditureList().size()).isEqualTo(5),

                () -> assertThat(actual.getExpenditureList())
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
        ExpenditureListResponse actual = expenditureService.retrieveExpenditureList(request, member);

        // then
        assertAll(
                () -> assertThat(actual.getTotalAmount()).isEqualTo(totalAmount),
                () -> actual.getTotalExpenditureByCategory().entrySet().stream()
                            .forEach(entry -> assertThat(entry.getValue()).isEqualTo(expenditureByCategory.get(entry.getKey()))),
                () -> assertThat(actual.getExpenditureList().size()).isEqualTo(8),
                () -> assertThat(actual.getExpenditureList())
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
        boolean actual = expenditureService.updateExpenditure(requet, member1, expenditureId);

        // then: category는 기존의 값과 같았고, 지출 일시와 합계 제외는 수정 요청을 하지 않아서 변경되지 않는다.
        // 수정 요청한 금액과 메모만 변경됨.
        assertAll(
                () -> assertThat(actual).isTrue(),
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
        boolean actual = expenditureService.updateExpenditure(requet, member1, expenditureId);

        // then: 카테고리는 기존 값과 변경 요청값이 동일하고, 이외의 필드는 수정 요청을 하지 않아 변경한 필드가 없음
        assertAll(
                () -> assertThat(actual).isFalse(),
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