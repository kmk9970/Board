package regist.practice.repository;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import regist.practice.CreateMemberForm;
import regist.practice.LoginForm;
import regist.practice.domain.*;
import regist.practice.service.BoardService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LoginRepository {
    private final EntityManager EM;
    public void registMember(user_info u) {
        EM.persist(u);
        user_rank user_rank=new user_rank();
        user_rank.setUser_id(u.getIdentity());
        EM.persist(user_rank);
    } //회원가입

    public List<user_info> loginCheck (String identity, String pass){
        return EM.createQuery("select m from user_info m where  m.identity =: identity and m.pass =: pass", user_info.class).
                setParameter("identity",identity).setParameter("pass",pass).setHint("org.hibernate.readOnly",true).getResultList();
    }

    public List<user_info> getUserData(String id){  //사용자의 정보를 가져오는 함수
        return EM.createQuery("select m from user_info m where m.identity =: identity",user_info.class)
                .setParameter("identity",id)
                .getResultList();

    }



}
