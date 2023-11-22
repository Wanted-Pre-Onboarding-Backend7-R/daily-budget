package com.mojh.dailybudget.common.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PatchableFieldUtilTest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class SuperObj {
        private Boolean superNonPatchableField;

        @Patchable
        private String superPatchableIgnoreNullTrueField;

        @Patchable(ignoreNull = false)
        private Long superPatchableIgnoreNullFalseField;

        public SuperObj(Boolean superNonPatchableField, String superPatchableIgnoreNullTrueField,
                        Long superPatchableIgnoreNullFalseField) {
            this.superNonPatchableField = superNonPatchableField;
            this.superPatchableIgnoreNullTrueField = superPatchableIgnoreNullTrueField;
            this.superPatchableIgnoreNullFalseField = superPatchableIgnoreNullFalseField;
        }
    }

    @Getter
    public class SubObj extends SuperObj {
        private Boolean nonPatchableField;

        @Patchable
        private String patchableIgnoreNullTrueField;

        @Patchable(ignoreNull = false)
        private Long patchableIgnoreNullFalseField;

        public SubObj(Boolean nonPatchableField, String patchableIgnoreNullTrueField, Long patchableIgnoreNullFalseField) {
            this.nonPatchableField = nonPatchableField;
            this.patchableIgnoreNullTrueField = patchableIgnoreNullTrueField;
            this.patchableIgnoreNullFalseField = patchableIgnoreNullFalseField;
        }

        public SubObj(Boolean superNonPatchableField, String superPatchableIgnoreNullTrueField, Long superPatchableIgnoreNullFalseField,
                      Boolean nonPatchableField, String patchableIgnoreNullTrueField, Long patchableIgnoreNullFalseField) {
            super(superNonPatchableField, superPatchableIgnoreNullTrueField, superPatchableIgnoreNullFalseField);
            this.nonPatchableField = nonPatchableField;
            this.patchableIgnoreNullTrueField = patchableIgnoreNullTrueField;
            this.patchableIgnoreNullFalseField = patchableIgnoreNullFalseField;
        }
    }

    @Test
    @DisplayName("patch는 변경 가능하지 않은 필드는 변경할 수 없다.")
    void patch_nonPatchableField_false() {
        // given
        SuperObj oldObj = new SuperObj(false, "string", 100L);
        SuperObj newObj = new SuperObj(true, "string", 100L);

        // when
        boolean result = PatchableFieldUtil.patch(oldObj, newObj);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("patch는 super calss의 변경 가능한 필드도 변경할 수 있다.")
    void patch_patchableSuperClassField_true() {
        // given
        SuperObj oldObj = new SubObj(false, "oldSuperField", 1L,
                false, "sub", 10L);
        SuperObj newObj = new SubObj(true, "newSuperField", 500L,
                false, "sub", 10L);

        // when
        boolean result = PatchableFieldUtil.patch(oldObj, newObj);

        // then
        Assertions.assertAll(
                () -> assertThat(result).isTrue(),
                () -> assertThat(oldObj.superNonPatchableField).isEqualTo(false),
                () -> assertThat(oldObj.superPatchableIgnoreNullTrueField)
                        .isEqualTo(newObj.superPatchableIgnoreNullTrueField),
                () -> assertThat(oldObj.superPatchableIgnoreNullFalseField)
                        .isEqualTo(newObj.superPatchableIgnoreNullFalseField)
        );
    }


    @Test
    @DisplayName("patch는 null 변경 요청을 무시하는 변경 가능한 필드와 null이 아닌 요청 데이터와 다를 때 필드를 변경한다.")
    void patch_patchableIgnoreNullTrue_requestNotNull_requestNotEqualTarget_true() {
        // given
        SuperObj oldObj = new SuperObj(false, "old", 100L);
        SuperObj newObj = new SuperObj(false, "new", 100L);

        // when
        boolean result = PatchableFieldUtil.patch(oldObj, newObj);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("patch는 null 변경 요청을 무시하는 변경 가능한 필드와 null이 아닌 요청 데이터와 같을 때 필드를 변경하지 않는다.")
    void patch_patchableIgnoreNullTrue_requestNotNull_requestEqualTarget_false() {
        // given
        SuperObj oldObj = new SuperObj(false, "old", 100L);
        SuperObj newObj = new SuperObj(false, "old", 100L);

        // when
        boolean result = PatchableFieldUtil.patch(oldObj, newObj);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("patch는 null 변경 요청을 무시하는 변경 가능한 필드를 변경할 때 요청 데이터가 null이면 필드를 변경하지 않는다.")
    void patch_patchableIgnoreNullTrue_requestNull_false() {
        SuperObj oldObj = new SuperObj(false, "old", 100L);
        SuperObj newObj = new SuperObj(false, null, 100L);

        boolean result = PatchableFieldUtil.patch(oldObj, newObj);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("patch는 null 변경 요청을 허용하는 변경 가능한 필드와 null이 아닌 요청 데이터와 다를 때 필드를 변경한다.")
    void patch_patchableIgnoreNullFalse_requestNotNull_requestNotEqualTarget_true() {
        // given
        SuperObj oldObj = new SuperObj(false, "string", 1L);
        SuperObj newObj = new SuperObj(false, "string", 100L);

        // when
        boolean result = PatchableFieldUtil.patch(oldObj, newObj);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("patch는 null 변경 요청을 허용하는 변경 가능한 필드와 null이 아닌 요청 데이터와 같을 때 필드를 변경하지 않는다.")
    void patch_patchableIgnoreNullFalse_requestNotNull_requestEqualTarget_false() {
        // given
        SuperObj oldObj = new SuperObj(false, "string", 1L);
        SuperObj newObj = new SuperObj(false, "string", 1L);

        // when
        boolean result = PatchableFieldUtil.patch(oldObj, newObj);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("patch는 null 변경 요청을 허용하는 변경 가능한 필드가 null이 아니고 요청 데이터가 null일 때 필드를 변경한다.")
    void patch_patchableIgnoreNullFalse_requestNull_targetNotNull_true() {
        SuperObj oldObj = new SuperObj(false, "string", 1L);
        SuperObj newObj = new SuperObj(false, "string", null);

        boolean result = PatchableFieldUtil.patch(oldObj, newObj);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("patch는 null 변경 요청을 허용하는 변경 가능한 필드가 null이고 요청 데이터도 null일 때 필드를 변경하지 않는다.")
    void patch_patchableIgnoreNullFalse_requestNull_targetNull_false() {
        SuperObj oldObj = new SuperObj(false, "string", null);
        SuperObj newObj = new SuperObj(false, "string", null);

        boolean result = PatchableFieldUtil.patch(oldObj, newObj);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("patch는 null 변경 요청을 허용하는 변경 가능한 필드가 null이고 요청 데이터는 null이 아닐 때 필드를 변경한다.")
    void patch_patchableIgnoreNullFalse_requestNotNull_targetNull_true() {
        SuperObj oldObj = new SuperObj(false, "string", null);
        SuperObj newObj = new SuperObj(false, "string", 100L);

        boolean result = PatchableFieldUtil.patch(oldObj, newObj);

        assertThat(result).isTrue();
    }

}