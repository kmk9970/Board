package regist.practice.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import regist.practice.domain.Comment;
import regist.practice.domain.Content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ContentRepository {
    private final EntityManager EM;
    private static Map<Integer, Content> contents=new HashMap<>();
    private static int sequence=0;

    public void save(Content content) {
//        content.setId(++sequence);
//        contents.put(content.getId(), content);
        EM.persist(content);
    }

    public void edit(Content content) {
//        contents.put(id, content);
//        Content target=EM.find(Content.class,id);

        EM.merge(content);
    }

    public void delete(int id) {
//        contents.remove(id);
        Content content = EM.find(Content.class,id);
        if(content!=null){
            EM.remove(content);
        }
    }

    public List<Content> findAll() {
//        return new ArrayList<>(contents.values());
//        return EM.createQuery("SELECT c FROM Content c", Content.class).getResultList();
          return new ArrayList<>(EM.createQuery("SELECT c FROM Content c", Content.class).getResultList());
    }

    public Content findById(int id) {
//        return contents.get(id);
        return EM.find(Content.class,id);
    }

    public void test(){
//        Content content=new Content();
//        content.setWriter("kmk");
//        content.setTexts("hello this is test");
//        content.setPassword("1234");
//        content.setTitle("test");
//        EM.persist(content);
//
//        List<Comment> test=new ArrayList<Comment>();
//
//
//        Comment comment=new Comment();
//        comment.setWriter("hello");
//        comment.setText("this is comment test");
//        comment.setContent(content);
//
//        test.add(comment);
//
//        content.setComments(test);
//        content.getComments().add(comment);
//        EM.persist(comment);

    }
}
