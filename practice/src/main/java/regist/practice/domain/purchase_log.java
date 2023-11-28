package regist.practice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class purchase_log {
    @Id@GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String coin_name;

    private double amount;

    private String price;

    private String trade_date;
}
