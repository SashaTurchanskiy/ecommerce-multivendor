package com.alek.repository;

import com.alek.model.VerificationCode;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepo extends JpaRepository<VerificationCode, Long> {
    VerificationCode findByEmail(String email);

    VerificationCode findByEmail(String email, Sort sort);
}
