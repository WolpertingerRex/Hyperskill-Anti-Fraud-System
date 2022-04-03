package antifraud.service;

import antifraud.business.creditcard.CreditCard;
import antifraud.persistance.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CreditCardService {
    @Autowired
    private CreditCardRepository repository;

    public CreditCard saveStolenCard(CreditCard card) {
           if (exists(card.getNumber())) {
               CreditCard fromDb = repository.findByNumber(card.getNumber()).get();
               card.setId(fromDb.getId());

               if (fromDb.isStolen())
                    throw new ResponseStatusException(HttpStatus.CONFLICT);
           }
        return repository.save(card);
    }

    public CreditCard saveCard(CreditCard card) {
        return repository.save(card);
    }


    public boolean exists(String number) {
        Optional<CreditCard> fromDb = repository.findByNumber(number);
        return fromDb.isPresent();
    }

    public CreditCard getCard(String number) {
        Optional<CreditCard> fromDb = repository.findByNumber(number);
        return fromDb.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void deleteCardFromBlackList(String number) {
        if (!exists(number)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        CreditCard fromDb = repository.findByNumber(number).get();
        if(!fromDb.isStolen()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        fromDb.setStolen(false);
        repository.save(fromDb);
    }

    public List<CreditCard> getStolenCards() {
        Iterable<CreditCard> all = repository.findAll();
        return StreamSupport.stream(all.spliterator(), false).filter(CreditCard::isStolen).collect(Collectors.toList());
    }
}

