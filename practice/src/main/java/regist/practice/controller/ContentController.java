package regist.practice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import regist.practice.domain.Comment;
import regist.practice.domain.Content;
import regist.practice.service.ContentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    // 홈 화면
    @GetMapping(value = {"", "/"})
    public String home(Model model) {
        model.addAttribute("contents", contentService.getAllContents());
        return "home";
    }

    // 글 쓰기 화면
    @GetMapping("/content/write")
    public String writePage() {
        return "write-page";
    }

    // 글 쓰기
    @PostMapping("/content/write")
    public String writeContent(Content content) {
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") );
        content.setUpdateDate( formattedDate );

        contentService.writeContent(content);
        return "redirect:/";
    }

    // 글 보기 화면
    @GetMapping("/content/{id}")
    public String showContent(@PathVariable int id, Model model) {
        model.addAttribute("content", contentService.getContent(id));
        return "content-page";
    }

    // 글 수정
    @PostMapping("/content/{id}")
    public String editContent(@PathVariable int id, Content content) {
        contentService.editContent(id, content.getTexts(), content.getPassword());
        return "redirect:/";
    }

    // 글 삭제
    @PostMapping("/content/delete/{id}")
    public String deleteContent(@PathVariable int id, Content content) {
        contentService.deleteContent(id, content.getPassword());
        return "redirect:/";
    }

//    @GetMapping(value = {"hello"})
//    public void dbtest(){
//        contentService.Testing();
//        Content content= new Content();
//        content=contentService.getContent(3);
//        List<Comment> test=content.getComments();
//        System.out.println(content.getId()+", "+content.getTitle()+", "+content.getTexts());
//        for(int i=0;i<test.size();i++){
//            System.out.println(i+":"+test.get(i));
//        }
//    }

    @PostMapping("/content/like/{id}")
    public String likeContent(@PathVariable int id)
    {
        contentService.goodContent(id);
        return "redirect:/content/{id}";
    }

    @PostMapping("/content/addComment/{id}")
    public String addComment(@PathVariable int id, String commentWriter, String commentText)
    {
        Comment comment=new Comment();
        comment.setWriter(commentWriter);
        comment.setText(commentText);
        System.out.println("holy !!"+comment.getWriter());
        System.out.println("moly !!" +comment.getText());
        contentService.saveComment(comment,id);
        return "redirect:/";
    }


}