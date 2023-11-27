package regist.practice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import regist.practice.CreateMemberForm;
import regist.practice.LoginForm;
import regist.practice.domain.user_info;
import regist.practice.service.LoginService;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping (value = "/")
    public  String loginPage(Model model) {
        model.addAttribute("LoginForm", new LoginForm());

        List <user_info> userInfo = loginService.getUserData("���º�");
        for(int i =0; i<userInfo.size();i++){
            System.out.println(userInfo.get(i).getApi_key());
            System.out.println(userInfo.get(i).getSec_key());
        }
        return "loginPage";
    }
    @PostMapping(value = "/")
    public void postLoginPage(HttpServletRequest request){
        HttpSession session = request.getSession(false); // ������ ������ ���� �������� ����
        if (session != null) {
            session.invalidate(); // ���� ����
        }
    }

    @GetMapping (value = "/login")
    public String getMainPage(HttpServletRequest request) throws JSONException, IOException {

        //���� �̸��� �ִ� �ڵ�
        //�����ͺ��̽��� ����� ���μ��� api�� �ҷ��� ���μ��� �ٸ��� ����
        //service.getCoinState()

        HttpSession session = request.getSession();
        String userId = String.valueOf(session.getAttribute("ID"));
        List <user_info> userInfo = loginService.getUserData(userId);
        //ȸ������ ������ ����ϴ� �ڵ�


        long beforeTime = System.currentTimeMillis(); //�ڵ� ���� ���� �ð� �޾ƿ���

//������ �ڵ� �߰�
        try {
            //���� ������ ���� �ҷ����� �ڵ�
            // myCoin =  h.executeAnsConcurrently(coinN);

        }catch (Exception e){
            System.out.println("��������������"+e);
        }
        long afterTime = System.currentTimeMillis(); // �ڵ� ���� �Ŀ� �ð� �޾ƿ���
        long secDiffTime = (afterTime - beforeTime)/1000; //�� �ð��� �� ���
        System.out.println("�ð�����(m) : "+secDiffTime);
        return "mainPage";
    }
    @PostMapping (value = "/login")
    //public String  postMainPage(user_info u , BindingResult result, HttpServletRequest request){
    public String  postMainPage(@Valid LoginForm form , BindingResult result, HttpServletRequest request){
        if (result.hasErrors()) {
            System.out.println(result.hasErrors());
            return "redirect:/";
        }
        List<user_info> loginCheck = loginService.loginCheck(form.getIdentity(), form.getPass());
        //�α��� Ȯ�� : ���̵�,����� ��ġ�ϸ� loginCheck ����� 1 , ��ġ���� ������ 0
        if(loginCheck.size() == 0 ){
            System.out.println("here error");
            //�α��� ������ ��ġ ���� ������ �α��� ����
            return "redirect:/";
        }
        String identity = loginService.getUserData(form.getIdentity()).get(0).getIdentity();
        HttpSession session = request.getSession();// HttpServletRequest request�̰� ���ڷ� �ְ� �̰Ŷ�
        session.setAttribute("ID", form.getIdentity()); //����
        String userId = String.valueOf(session.getAttribute("ID")); //�̰� ������ �α����� ���̵� ȣ�Ⱑ��
        System.out.println("�α��� ���� : "+userId);
        return "redirect:/login";
    }

    @GetMapping (value="/createMember")
    public  String createMember(Model model) {
        model.addAttribute("CreateMemberForm",new CreateMemberForm());
        return "createMember";
    }

    @PostMapping (value="/createMember")
    public String postCreateMember(@Valid CreateMemberForm form, BindingResult result){
        if (result.hasErrors()) {
            //serviceTest.aaa();
            System.out.println(result.hasErrors());
            return "create";
        }

        //��й�ȣ�� ��й�ȣ ��Ȯ�� ��ġ���� ������ �ٽ�
        if (!form.getMakePass().equals(form.getConfirm())) {
            return "create";
        }

        //�ߺ��Ǵ� ���̵� ������ �ٽ�
//        List<user> idInfo = service.checkDupleId();
//        for (int i = 0; i < idInfo.size(); i++) {
//            if (form.getMakeId().equals(idInfo.get(i).getIdentity())) {
//                return "redirect:/createMember";
//            }
//        }

        //������ insert �ڵ�
        user_info u = new user_info();  //��ƼƼ ��ü ����
        u.setIdentity(form.getMakeId());
        u.setPass(form.getMakePass());
        u.setApi_key(form.getApi_key());
        u.setSec_key(form.getSec_key());
        loginService.registMember(u);
        return "redirect:/";
    }
}
