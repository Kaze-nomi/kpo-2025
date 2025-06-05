package hse.payments.domains;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "accounts")
@ToString
@RequiredArgsConstructor
public class Account {

    @Getter
    @Setter
    @Id
    private int userId;

    @Column(nullable = false)
    @Setter
    private double money;

    @Version
    @Column(nullable = false)
    private Long version;

}