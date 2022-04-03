package antifraud.business.creditcard;

import antifraud.business.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "credit_card")
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @CreditCardConstraint
    private String number;

    @JsonIgnore
    private boolean isStolen = false;

    @JsonIgnore
    private long maxAllowed = 200;

    @JsonIgnore
    private long maxManual = 1500;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "card", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude

    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setCard(this);
    }

    public CreditCard(String number) {
        this.number = number;
        maxAllowed = 200;
        maxManual = 1500;
        isStolen = false;
        transactions = new ArrayList<>();
    }
}
