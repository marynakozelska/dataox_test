package com.example.dataox.repo;

import com.example.dataox.entities.Client;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("SELECT c FROM Client c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE CONCAT('%', LOWER(CAST(:name as string)), '%')) AND " +
            "(:email IS NULL OR LOWER(c.email) LIKE CONCAT('%', LOWER(CAST(:email as string)), '%')) AND " +
            "(:address IS NULL OR LOWER(c.address) LIKE CONCAT('%', LOWER(CAST(:address as string)), '%'))")
    List<Client> searchClients(@Param("name") String name,
                               @Param("email") String email,
                               @Param("address") String address);

    @Query("SELECT c FROM Client c WHERE c.profit BETWEEN :minProfit AND :maxProfit")
    List<Client> findByProfitRange(@Param("minProfit") BigDecimal minProfit,
                                   @Param("maxProfit") BigDecimal maxProfit);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Client c WHERE c.id = :id")
    Optional<Client> findByIdForUpdate(@Param("id") Long id);
}