package com.mojh.dailybudget.expenditure.repository;

import com.mojh.dailybudget.expenditure.domain.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

}
