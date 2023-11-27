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

        List <user_info> userInfo = loginService.getUserData("조승빈");
        for(int i =0; i<userInfo.size();i++){
            System.out.println(userInfo.get(i).getApi_key());
            System.out.println(userInfo.get(i).getSec_key());
        }
        return "loginPage";
    }
    @PostMapping(value = "/")
    public void postLoginPage(HttpServletRequest request){
        HttpSession session = request.getSession(false); // 세션이 없으면 새로 생성하지 않음
        if (session != null) {
            session.invalidate(); // 세션 종료
        }
    }

    @GetMapping (value = "/login")
    public String getMainPage(HttpServletRequest request) throws JSONException, IOException {

        //코인 이름을 넣는 코드
        //데이터베이스에 저장된 코인수와 api로 불러온 코인수가 다르면 갱신
        //service.getCoinState()

        HttpSession session = request.getSession();
        String userId = String.valueOf(session.getAttribute("ID"));
        List <user_info> userInfo = loginService.getUserData(userId);
        //회원들의 코인을 출력하는 코드


        long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기

//실험할 코드 추가
        try {
            //내가 보유한 코인 불러오는 코드
            // myCoin =  h.executeAnsConcurrently(coinN);

        }catch (Exception e){
            System.out.println("ㅈㅇㅁㅈㅇ에러"+e);
        }
        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
        System.out.println("시간차이(m) : "+secDiffTime);
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
        //로그인 확인 : 아이디,비번이 일치하면 loginCheck 사이즈가 1 , 일치하지 않으면 0
        if(loginCheck.size() == 0 ){
            System.out.println("here error");
            //로그인 정보가 일치 하지 않으면 로그인 실패
            return "redirect:/";
        }
        String identity = loginService.getUserData(form.getIdentity()).get(0).getIdentity();
        HttpSession session = request.getSession();// HttpServletRequest request이걸 인자로 넣고 이거랑
        session.setAttribute("ID", form.getIdentity()); //세션
        String userId = String.valueOf(session.getAttribute("ID")); //이게 있으면 로그인한 아이디 호출가능
        System.out.println("로그인 성공 : "+userId);
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

        //비밀번호와 비밀번호 재확인 일치하지 않으면 다시
        if (!form.getMakePass().equals(form.getConfirm())) {
            return "create";
        }

        //중복되는 아이디가 있으면 다시
//        List<user> idInfo = service.checkDupleId();
//        for (int i = 0; i < idInfo.size(); i++) {
//            if (form.getMakeId().equals(idInfo.get(i).getIdentity())) {
//                return "redirect:/createMember";
//            }
//        }

        //데이터 insert 코드
        user_info u = new user_info();  //엔티티 객체 생성
        u.setIdentity(form.getMakeId());
        u.setPass(form.getMakePass());
        u.setApi_key(form.getApi_key());
        u.setSec_key(form.getSec_key());
        loginService.registMember(u);
        return "redirect:/";
    }
}
