package com.mojh.dailybudget.expenditure.repository;

import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.member.domain.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenditureRepositroyCustom {

    List<Expenditure> retrieveExpenditureList(Member member, String category, LocalDateTime beginDate,
                                              LocalDateTime endDate, Long minAmount, Long maxAmount);

}
