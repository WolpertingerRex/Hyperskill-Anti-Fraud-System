package antifraud.persistance;

import antifraud.business.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findAllByDateBetweenAndNumber(@Param("startDate")  LocalDateTime dateStart,  @Param("endDate") LocalDateTime dateEnd, @Param("number") String number);
    List<Transaction> findAllByNumber(String number);
}
