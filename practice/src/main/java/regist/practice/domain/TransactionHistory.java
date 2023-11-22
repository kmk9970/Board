package regist.practice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TransactionHistory {
    @Id
    @GeneratedValue//(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)  //DB칼럼이름이 what //notnull
    private Long id;
    private String user; //거래한 회원 아이디
    private String  coinName; //코인 이름
    private  String amount;  //코인 거래수량
    private String state;// 매수, 매도 상태
    private String price; //체결된 코인가격
    private String fee;  // 수수료
    private String afterTrade; //거래후 내 자산

}
