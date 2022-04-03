package antifraud.presentation.controllers;


import antifraud.business.Transaction;
import antifraud.business.creditcard.CreditCardConstraint;
import antifraud.presentation.requests.FeedbackRequest;
import antifraud.presentation.responses.TransactionResult;
import antifraud.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/antifraud")
@Validated
public class TransactionController {


    @Autowired
    TransactionService service;


    @PostMapping("/transaction")
    public TransactionResult makeTransaction(@RequestBody @Valid Transaction transaction) {
        return service.processTransaction(transaction);
    }
    @PutMapping("/transaction")
    public Transaction addFeedback(@RequestBody @Valid FeedbackRequest request){
        return service.addFeedback(request.getTransactionId(), request.getFeedback());
    }

    @GetMapping("/history")
    public List<Transaction> getAllTransactions(){
        return service.getAllTransactions();
    }

    @GetMapping("/history/{number}")
    public List<Transaction> getTransactions(@PathVariable @CreditCardConstraint String number){
        List<Transaction> transactions = service.getTransactions(number);
        if(transactions.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return transactions;
    }


}
