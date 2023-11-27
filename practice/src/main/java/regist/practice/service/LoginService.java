package regist.practice.service;

import regist.practice.repository.LoginRepository;
import regist.practice.domain.user_info;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {
    private final LoginRepository loginRepository;

    public void registMember(user_info u){
        loginRepository.registMember(u);
    }


    public List<user_info> getUserData(String id){
        return loginRepository.getUserData(id);
    }

    public List<user_info> loginCheck (String identity, String pass){
        return loginRepository.loginCheck(identity,pass);
    }



}
