package antifraud.repository;

import antifraud.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

//    @Query(value = "SELECT * FROM transactions WHERE number = :number AND date <= :#{@transactionRepository.oneHourBefore()}", nativeQuery = true)
    @Query(value = "SELECT * FROM transactions WHERE number = :number AND date >= :givenDateMinusHour AND date < :givenDate", nativeQuery = true)
    List<Transaction> getAllOneHourOld(@Param("number") String number, @Param("givenDateMinusHour") LocalDateTime givenDateMinusHour, @Param("givenDate") LocalDateTime givenDate);

    default Instant oneHourBefore() {
        return Instant.now().minus(1, ChronoUnit.HOURS);
    }
}
