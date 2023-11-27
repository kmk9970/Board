package regist.practice;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {
    @NotEmpty(message ="���̵� �Է��ּ���")  //DB���� NotNULL�� ����� ���
    private String identity;

    @NotEmpty(message ="��й�ȣ�� �Է����ּ���")  //DB���� NotNULL�� ����� ���
    private String pass;
}

