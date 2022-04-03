package antifraud.presentation.requests;

import antifraud.presentation.responses.TransactionResultType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {
    private long transactionId;

    @ValueOfEnum(enumClass = TransactionResultType.class)
    private String feedback;
}
