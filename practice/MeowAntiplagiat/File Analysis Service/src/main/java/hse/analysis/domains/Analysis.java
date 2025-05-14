package hse.analysis.domains;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "analyzes")
@ToString
@RequiredArgsConstructor
public class Analysis {

    @Getter
    @Id
    private int fileId;

    @Column(nullable = false, length = 2000)   
    private String result;

    @Column(nullable = true)
    private String cloudLocation;

}