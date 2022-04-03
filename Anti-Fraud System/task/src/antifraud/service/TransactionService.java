package antifraud.service;

import antifraud.business.Transaction;
import antifraud.business.creditcard.CreditCard;
import antifraud.persistance.TransactionRepository;
import antifraud.presentation.responses.TransactionResult;
import antifraud.presentation.responses.TransactionResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static antifraud.presentation.responses.TransactionResultType.*;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository repository;
    @Autowired
    private CreditCardService cardService;
    @Autowired
    private IPService ipService;

    public TransactionResult processTransaction(Transaction transaction) {

        Set<String> reasons = new TreeSet<>();

        boolean allowed = false;
        boolean manual = false;
        boolean prohibited = false;
        CreditCard card;

        if (cardService.exists(transaction.getNumber())) {
            card = cardService.getCard(transaction.getNumber());
            if(card.isStolen()) {
                reasons.add("card-number");
                prohibited = true;
            }
        }

        else {
            card = new CreditCard(transaction.getNumber());
        }

        if (ipService.exists(transaction.getIp())) {
            reasons.add("ip");
            prohibited = true;
        }

        LocalDateTime end = transaction.getDate();
        LocalDateTime start = end.minusHours(1);

        List<Transaction> transactionsInLastHour = repository.findAllByDateBetweenAndNumber(start, end, transaction.getNumber());
        transactionsInLastHour.add(transaction);

        long regionsCount = transactionsInLastHour.stream().map(Transaction::getRegion).distinct().count();

        long ipCount = transactionsInLastHour.stream().map(Transaction::getIp).distinct().count();

        if (regionsCount > 2) {
            reasons.add("region-correlation");
            if (regionsCount == 3) manual = true;
            else prohibited = true;
        }
        if (ipCount > 2) {
            reasons.add("ip-correlation");
            if (ipCount == 3) manual = true;
            else prohibited = true;
        }

        long amount = transaction.getAmount();

        if (amount <= card.getMaxAllowed() && !prohibited && !manual) {
            allowed = true;
            reasons.add("none");
        }


        if (amount > card.getMaxAllowed() && amount <= card.getMaxManual() && !prohibited) {
            reasons.add("amount");
            manual = true;
        }

        if (amount > card.getMaxManual()) {
            reasons.add("amount");
            prohibited = true;
        }

        String info = String.join(", ", reasons);
        TransactionResult result;

        if(prohibited){
            transaction.setResult(PROHIBITED.name());
            result = new TransactionResult(PROHIBITED, info);
        }

        else if(allowed){
            transaction.setResult(ALLOWED.name());
            result = new TransactionResult(ALLOWED, info);
        }

        else {
            transaction.setResult(MANUAL_PROCESSING.name());
            result = new TransactionResult(MANUAL_PROCESSING, info);
        }

        card.addTransaction(transaction);
        cardService.saveCard(card);

        return result;

    }


    private long increase(long current, long value) {
        return (long) Math.ceil(0.8 * current + 0.2 * value);
    }

    private long decrease(long current, long value) {
        return (long) Math.ceil(0.8 * current - 0.2 * value);
    }

    public Transaction addFeedback(long transactionId, String feedback) {

       Transaction transaction = repository.findById(transactionId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!transaction.getFeedback().isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT);

        TransactionResultType resultType = valueOf(transaction.getResult());
        TransactionResultType feedbackType = valueOf(feedback);

        if (resultType == feedbackType) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);

        CreditCard card = cardService.getCard(transaction.getNumber());

        switch (feedbackType) {
            case ALLOWED:
                if (resultType == MANUAL_PROCESSING) {
                    card.setMaxAllowed(increase(card.getMaxAllowed(), transaction.getAmount()));
                } else if (resultType == PROHIBITED) {
                    card.setMaxManual(increase(card.getMaxManual(), transaction.getAmount()));
                    card.setMaxAllowed(increase(card.getMaxAllowed(), transaction.getAmount()));
                }
                break;
            case MANUAL_PROCESSING:
                if (resultType == ALLOWED) {
                    card.setMaxAllowed(decrease(card.getMaxAllowed(), transaction.getAmount()));
                } else if (resultType == PROHIBITED) {
                    card.setMaxManual(increase(card.getMaxManual(), transaction.getAmount()));
                }
                break;

            case PROHIBITED:
                if (resultType == ALLOWED) {
                    card.setMaxAllowed(decrease(card.getMaxAllowed(), transaction.getAmount()));
                    card.setMaxManual(decrease(card.getMaxManual(), transaction.getAmount()));
                } else if (resultType == MANUAL_PROCESSING) {
                    card.setMaxManual(decrease(card.getMaxManual(), transaction.getAmount()));
                }
                break;
        }

        cardService.saveCard(card);
        transaction.setFeedback(feedback);
        repository.save(transaction);

        return transaction;
    }

    public List<Transaction> getAllTransactions() {
        Iterable<Transaction> all = repository.findAll();
        return StreamSupport.stream(all.spliterator(), false).collect(Collectors.toList());

    }

    public List<Transaction> getTransactions(String number) {
        return repository.findAllByNumber(number);
    }
}
