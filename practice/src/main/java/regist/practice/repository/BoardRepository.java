package regist.practice.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import regist.practice.domain.TransactionHistory;
import regist.practice.domain.Comment;
import regist.practice.domain.Content;
import regist.practice.domain.user_info;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepository {
    private final EntityManager EM;

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

    public void registComment(Comment comment, Content content){
        if(content.getComments()==null){
            content.setComments(new ArrayList<Comment>());
        }
        comment.setContent(content);
        EM.persist(comment);

        content.getComments().add(comment);
        EM.merge(content);



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

    public Comment findByCommentId(int id) {
//        return contents.get(id);
        return EM.find(Comment.class,id);
    }


    //댓글을 삭제하는 함수 content를 받아서 commentList에서 입력받은 comment를 없애자
    public void deleteComment(int comment_id, Content content){
        Comment comment=EM.find(Comment.class,comment_id);
        content.getComments().remove(comment);
        EM.persist(content);
        EM.remove(comment);
    }

    public void excelDataSave(TransactionHistory test){
        EM.persist(test);
    }

    public List<TransactionHistory> getDatafromExcel(String test){
        return new ArrayList<>(EM.createQuery("SELECT e FROM TransactionHistory e WHERE e.state = :test", TransactionHistory.class)
                .setParameter("test", test)
                .getResultList());
    }

    public List<TransactionHistory> getExcelByDate(String test,String date1,String date2){
        return new ArrayList<>(EM.createQuery("SELECT e FROM TransactionHistory e WHERE e.state = :test AND e.datetime BETWEEN :date1 AND :date2", TransactionHistory.class)
                .setParameter("test", test)
                .setParameter("date1", date1)
                .setParameter("date2",date2)
                .getResultList());
    }
    //입력받은 시점까지의 사용한 금액+팔아서 번 금액을 리턴하는 함수
    public List<TransactionHistory> getProfitandLoss(String date,String type){//여기서 조금더 편하게 할 수 있지 않을까?
        return new ArrayList<>(
                EM.createQuery("SELECT e FROM TransactionHistory e WHERE e.datetime>= :date AND e.state=:type",TransactionHistory.class)
                .setParameter("date",date)
                .setParameter("type",type)
                .getResultList());
    }



    public List<TransactionHistory> getUserTrade(String user_id){
        return EM.createQuery("selecet m from transcation_history where m.user=:user_id order by m.coinDate",TransactionHistory.class)
                .setParameter("user_id",user_id)
                .getResultList();
    }


}
