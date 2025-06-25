// PromptHistory Entity
package com.syu.cara.rentalrequest.domain;

import com.syu.cara.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prompt_history")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PromptHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private RentalRequest rentalRequest;

    private String userInput;
    private String botResponse;
    private LocalDateTime timestamp;
}