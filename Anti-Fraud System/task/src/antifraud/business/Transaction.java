package antifraud.business;

import antifraud.business.creditcard.CreditCard;
import antifraud.business.creditcard.CreditCardConstraint;
import antifraud.business.ip.IPConstraint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("transactionId")
    private long id;

    @Min(value = 1, message = "Amount must be greater than 0")
    private long amount;

    @NotBlank
    @IPConstraint
    private String ip;

    @NotBlank
    @CreditCardConstraint
    private String number;

    @NotBlank
    private String region;

    private LocalDateTime date;

    private String result;

    private String feedback = "";

    @ManyToOne
    @JoinColumn(name = "card_id")
    @JsonIgnore
    private CreditCard card;

}
