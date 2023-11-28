package regist.practice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class user_info {
    @Id
    @GeneratedValue//(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)  //DBÄ®·³ÀÌ¸§ÀÌ what //notnull
    private Long id;
    private String identity;
    private String pass;
    private String api_key;
    private String sec_key;
}

