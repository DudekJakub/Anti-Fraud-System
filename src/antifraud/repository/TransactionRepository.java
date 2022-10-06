package antifraud.repository;

import antifraud.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    String QUERY_FIND_BY_NUMBER = "SELECT t.* FROM transactions t, credit_cards c WHERE t.credit_card_id = c.id AND c.card_number = :number ";

    @Query("select t from Transaction t where t.creditCard.cardNumber = ?1")
    List<Transaction> findByCreditCard_CardNumber(String cardNumber);

//    List<Transaction> findByCreditCard_CardNumber(String cardNumber);

    @Query(value = QUERY_FIND_BY_NUMBER + "AND date >= :givenDateMinusHour " + "AND date <= :givenDate", nativeQuery = true)
    List<Transaction> findByNumberAndDateBetween(@Param("number") String number, @Param("givenDateMinusHour") LocalDateTime givenDateMinusHour, @Param("givenDate") LocalDateTime givenDate);

    @Query(value = QUERY_FIND_BY_NUMBER, nativeQuery = true)
    List<Transaction> findByNumber(@Param("number") String number);
}
