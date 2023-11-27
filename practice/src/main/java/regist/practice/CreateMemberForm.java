package regist.practice;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateMemberForm {
    @NotEmpty(message ="���̵� �Է��ּ���")  //DB���� NotNULL�� ����� ���
    private String makeId;

    @NotEmpty(message ="��й�ȣ�� �Է����ּ���")  //DB���� NotNULL�� ����� ���
    private String makePass;

    @NotEmpty(message ="��й�ȣ�� ���Է����ּ���")  //DB���� NotNULL�� ����� ���
    private String confirm;

    @NotEmpty
    private String api_key;
    @NotEmpty
    private String sec_key;
}
