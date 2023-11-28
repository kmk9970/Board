package regist.practice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class user_rank {
    @Id@Column(name = "user_id", nullable = false)
    private String user_id;

    private Double win_rate;

    private Double profit_rate;
}
