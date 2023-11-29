package com.mojh.dailybudget.expenditure.service;

import com.mojh.dailybudget.category.CategoryFixture;
import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.category.service.CategorySerivce;
import com.mojh.dailybudget.common.exception.DailyBudgetAppException;
import com.mojh.dailybudget.common.exception.ErrorCode;
import com.mojh.dailybudget.common.tmp.TestUilts;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureUpdateRequest;
import com.mojh.dailybudget.expenditure.repository.ExpenditureRepository;
import com.mojh.dailybudget.member.domain.Member;
import com.mojh.dailybudget.member.MemberFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mojh.dailybudget.category.domain.CategoryType.EDUCATION;
import static com.mojh.dailybudget.category.domain.CategoryType.FOOD;
import static com.mojh.dailybudget.category.domain.CategoryType.SHOPPING;
import static com.mojh.dailybudget.category.domain.CategoryType.UNCATEGORIZED;
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

    static Member member;
    static Member member2;
    static Member member3;
    static Map<CategoryType, Category> categoryMap;
    static List<Expenditure> expenditureList;

    @BeforeAll
    static void beforeAll() {
        member = MemberFixture.MEMBER1();
        member2 = MemberFixture.MEMBER2();
        member3 = MemberFixture.MEMBER3();
        categoryMap = CategoryFixture.CATEGORY;
        expenditureList = new ArrayList<>();

        Expenditure expenditure;

        for (int idx = 1; idx <= 5; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 02, 10 + idx, 30);
            expenditure = Expenditure.builder()
                                     .member(member)
                                     .category(categoryMap.get(FOOD))
                                     .amount(10000L * idx)
                                     .memo("음식 " + idx)
                                     .excludeFromTotal(false)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureList.add(expenditure);
        }

        for (int idx = 6; idx <= 7; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 03, 10 + idx, 30);
            expenditure = Expenditure.builder()
                                     .member(member)
                                     .category(categoryMap.get(EDUCATION))
                                     .amount(20000L * idx)
                                     .memo("강의 구매 " + idx)
                                     .excludeFromTotal(false)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureList.add(expenditure);
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
            expenditureList.add(expenditure);
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
            expenditureList.add(expenditure);
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
            expenditureList.add(expenditure);
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

    @Test
    @DisplayName("지출 정보를 수정할 때 수정 요청한 필드만 변경한다.")
    void updateExpenditure_patch_true() {
        // given: 카테고리, 금액, 메모 변경 요청하도록 설정, 카테고리는 기존 값과 동일
        Expenditure expenditure = expenditureList.get(0);
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
        boolean result = expenditureService.updateExpenditure(requet, member, expenditureId);

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
        Expenditure expenditure = expenditureList.get(0);
        Long expenditureId = expenditure.getId();
        Expenditure captured = captureExpenditure(expenditure);
        ExpenditureUpdateRequest requet = ExpenditureUpdateRequest.builder()
                                                                  .category(FOOD)
                                                                  .build();
        given(categorySerivce.findByCategoryType(requet.getCategory())).willReturn(expenditure.getCategory());
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(expenditure));

        // when
        boolean result = expenditureService.updateExpenditure(requet, member, expenditureId);

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
        Expenditure expenditure = expenditureList.get(0);
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
        Expenditure expenditure = expenditureList.get(0);
        Long expenditureId = expenditure.getId();
        given(expenditureRepository.findById(expenditureId)).willReturn(Optional.of(expenditure));

        // when
        expenditureService.deleteExpenditure(member, expenditureId);

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
        Expenditure expenditure = expenditureList.get(0);
        Long expenditureId = expenditure.getId();
        Long otherId = expenditureId + 1000L;
        given(expenditureRepository.findById(otherId)).willReturn(Optional.empty());

        // when
        DailyBudgetAppException ex = assertThrows(DailyBudgetAppException.class, () -> {
            expenditureService.deleteExpenditure(member, otherId);
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
        Expenditure expenditure = expenditureList.get(0);
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