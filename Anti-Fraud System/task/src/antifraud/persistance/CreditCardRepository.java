package antifraud.persistance;

import antifraud.business.creditcard.CreditCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditCardRepository extends CrudRepository<CreditCard, Long> {
    Optional<CreditCard> findByNumber(String number);
    void deleteByNumber(String number);
}
