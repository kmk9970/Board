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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import regist.practice.CreateMemberForm;
import regist.practice.LoginForm;
import regist.practice.domain.Comment;
import regist.practice.domain.Content;
import regist.practice.domain.TransactionHistory;
import regist.practice.domain.user_info;
import regist.practice.service.BoardService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 홈 화면
    @GetMapping(value = "home")
    public String home(Model model) {
        model.addAttribute("contents", boardService.getAllContents());
        return "home";
    }

    // 글 쓰기 화면
    @GetMapping("/content/write")
    public String writePage() {
        return "write-page";
    }

    // 글 쓰기
    @PostMapping("/content/write") //여기에 유저아이디 불러와야함. 코드삽입완료
    public String writeContent(Content content, HttpServletRequest request) {
        HttpSession session = request.getSession();
        content.setWriter(String.valueOf(session.getAttribute("ID")));//로그인한 아이디를 글쓴이로
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        content.setUpdateDate(formattedDate);

        boardService.writeContent(content);
        return "redirect:/home";
    }

    // 글 보기 화면
    @GetMapping("/content/{id}")
    public String showContent(@PathVariable int id, Model model) {
        model.addAttribute("content", boardService.getContent(id));
        return "content-page";
    }

    // 글 수정
    @PostMapping("/content/{id}")
    public String editContent(@PathVariable int id, Content content, HttpServletRequest request) {
        HttpSession session = request.getSession();
        boardService.editContent(id, content.getTexts(), String.valueOf(session.getAttribute("ID")));
        return "redirect:/home";
    }

    // 글 삭제
    @PostMapping("/content/delete/{id}")
    public String deleteContent(@PathVariable int id, Content content, HttpServletRequest request) {
        HttpSession session = request.getSession();
        boardService.deleteContent(id, String.valueOf(session.getAttribute("ID")));
        return "redirect:/home";
    }

    //댓글 저장
    @PostMapping("/content/addComment/{id}")
    public String addComment(@PathVariable int id, HttpServletRequest request, String commentText) {
        HttpSession session = request.getSession();
        Comment comment = new Comment();
        comment.setWriter(String.valueOf(session.getAttribute("ID")));
        comment.setText(commentText);
        boardService.saveComment(comment, id);
        return "redirect:/content/{id}";
    }
    //댓글삭제
    @PostMapping("/content/deleteComment/{id}") //comment_id를 받아서 삭제하는 함수
    public String deleteComment(@PathVariable int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        boardService.deleteComment(id, String.valueOf(session.getAttribute("ID")));

        return "redirect:/home";
    }

    //좋아요
    @PostMapping("/content/like/{id}")
    public String likeContent(@PathVariable int id) {
        boardService.goodContent(id);
        return "redirect:/content/{id}";
    }

//    @GetMapping(value = {"hello"})
//    public void dbtest(){
//        boardService.Testing();
//        Content content= new Content();
//        content=boardService.getContent(3);
//        List<Comment> test=content.getComments();
//        System.out.println(content.getId()+", "+content.getTitle()+", "+content.getTexts());
//        for(int i=0;i<test.size();i++){
//            System.out.println(i+":"+test.get(i));
//        }
//    }

    public List<TransactionHistory> getUserTrade(String userId){
        return boardService.getUserTrade(userId);
    }

}

