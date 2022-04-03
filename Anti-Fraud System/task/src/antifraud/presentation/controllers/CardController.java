package antifraud.presentation.controllers;

import antifraud.business.creditcard.CreditCard;
import antifraud.business.creditcard.CreditCardConstraint;
import antifraud.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud/stolencard")
@Validated
public class CardController {
    @Autowired
    private CreditCardService service;

    @GetMapping
    public List<CreditCard> getAll(){
        return service.getStolenCards();
    }

    @PostMapping
    public CreditCard saveCard(@RequestBody @Valid CreditCard card){
        card.setStolen(true);
        return service.saveStolenCard(card);
    }

    @DeleteMapping("/{number}")
    public Map<String, String> deleteCardFromBlacklist(@PathVariable @CreditCardConstraint String number){
        service.deleteCardFromBlackList(number);
        return Map.of("status", "Card " + number + " successfully removed!");
    }
}
