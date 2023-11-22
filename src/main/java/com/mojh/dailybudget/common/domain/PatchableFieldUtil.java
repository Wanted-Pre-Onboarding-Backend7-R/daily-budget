package com.mojh.dailybudget.common.domain;

import com.mojh.dailybudget.common.exception.DailyBudgetAppException;
import com.mojh.dailybudget.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Entity patch 작업을 위한 중복 코드 생성을 줄이는 util class.
 * <p>
 * ref = https://blog.gangnamunni.com/post/Annotation-Reflection-Entity-update/
 */
@Slf4j
public final class PatchableFieldUtil {

    /**
     * 요청한 값에 따라 entity의 필드를 변경한다.
     * @param targetObj 변경 대상인 기존 리소스
     * @param requestObj 변경 요청 데이터
     * @return
     * @param <T>
     */
    public static <T> boolean patch(T targetObj, T requestObj) {
        if (requestObj == null) {
            return false;
        }

        if (targetObj.getClass() != requestObj.getClass()) {
            throw new DailyBudgetAppException(ErrorCode.COM_PATCH_FAILED);
        }

        // Lambda 안에서 updated 를 접근하기 위한 AtomicReference
        final AtomicReference<Boolean> updated = new AtomicReference<>(false);

        List<String> patchedList = new LinkedList<>();

        // 대상 클래스의 모든 Super class의 모든 field에 대해 지정된 콜백 호출(조건에 해당하는 필드만)
        ReflectionUtils.doWithFields(targetObj.getClass(),
                field -> {
                    // 해당 값을 true로 설정해야 private, protected 같이 제한된 데이터 접근 가능
                    field.setAccessible(true);
                    Object oldValue = field.get(targetObj);
                    Object newValue = field.get(requestObj);
                    boolean canPatch = false;
                    final Patchable annotation = field.getAnnotation(Patchable.class);

                    if (annotation.ignoreNull()) {
                        // 기본 값, 요청 데이터가 null이면 변경 x
                        if (PatchableFieldUtil.canUpdate(oldValue, newValue)) {
                            canPatch = true;
                        }
                    } else {
                        // 요청 데이터가 null이어도 변경 가능
                        if (PatchableFieldUtil.canUpdateAlbeitNull(oldValue, newValue)) {
                            canPatch = true;
                        }
                    }

                    if (canPatch) {
                        field.set(targetObj, newValue);
                        updated.set(true);
                        patchedList.add(String.format("%s : %s -> %s", field.getName(), oldValue, newValue));
                    }

                },
                field -> {
                    // 콜백을 적용할 필드를 결정하는 필터 로직
                    final Annotation annotation = field.getAnnotation(Patchable.class);
                    return annotation != null;
                });

        if (updated.get()) {
            log.info(String.join("\n", patchedList));
        }

        return updated.get();
    }

    /**
     * 변경 요청 데이터가 null이 아니고 기존 데이터와 다르면 변경 가능
     * @param to 기존 데이터
     * @param from 변경 요청 데이터
     * @return 변경 가능 여부
     * @param <T>
     */
    public static <T> boolean canUpdate(T to, T from) {
        if (from != null && !from.equals(to)) {
            return true;
        }
        return false;
    }

    /**
     * 변경 요청 데이터가 null이어도 기존 데이터가 null이 아니면 변경 가능
     * <p>
     * 변경 요청 데이터가 null이 아닌 값일 때에는 기존 데이터와 다를 때 변경 가능
     * @param to 기존 데이터
     * @param from 변경 요청 데이터
     * @return 변경 가능 여부
     * @param <T>
     */
    public static <T> boolean canUpdateAlbeitNull(T to, T from) {
        if (from == null) {
            return to != null;
        }
        return !from.equals(to);
    }

}
