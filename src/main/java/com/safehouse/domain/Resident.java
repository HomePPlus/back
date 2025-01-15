package com.safehouse.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "residents")
public class Resident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long residentId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

