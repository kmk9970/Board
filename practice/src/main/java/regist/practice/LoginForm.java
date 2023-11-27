package regist.practice;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {
    @NotEmpty(message ="아이디를 입력주세요")  //DB에서 NotNULL과 비슷힌 기능
    private String identity;

    @NotEmpty(message ="비밀번호를 입력해주세요")  //DB에서 NotNULL과 비슷힌 기능
    private String pass;
}

