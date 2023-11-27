package regist.practice;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateMemberForm {
    @NotEmpty(message ="아이디를 입력주세요")  //DB에서 NotNULL과 비슷힌 기느
    private String makeId;

    @NotEmpty(message ="비밀번호를 입력해주세요")  //DB에서 NotNULL과 비슷힌 기느
    private String makePass;

    @NotEmpty(message ="비밀번호를 재입력해주세요")  //DB에서 NotNULL과 비슷힌 기느
    private String confirm;

    @NotEmpty
    private String api_key;
    @NotEmpty
    private String sec_key;
}
