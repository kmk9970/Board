package regist.practice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import regist.practice.domain.TransactionHistory;
import regist.practice.domain.Comment;
import regist.practice.domain.Content;
import regist.practice.domain.user_info;
import regist.practice.repository.BoardRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public void writeContent(Content content){
        boardRepository.save(content);
    }

    public void editContent(int id, String texts,String user_id){
        Content content= boardRepository.findById(id);
        if(!content.getWriter().equals(user_id)){
            return;
        }

        content.setTexts(texts);

        LocalDateTime now=LocalDateTime.now();
        String formattedDate=now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        content.setUpdateDate(formattedDate);

        boardRepository.edit(content);
    }

    public void deleteContent(int id, String user_id) {
        Content content = boardRepository.findById(id);
        if(!content.getWriter().equals(user_id)) {
            return;
        }
        boardRepository.delete(id);
    }

    public List<Content> getAllContents() {
        return boardRepository.findAll();
    }

    public Content getContent(int id) {
        return boardRepository.findById(id);
    }

    public void saveComment(Comment comment, int id){
        boardRepository.registComment(comment, boardRepository.findById(id));
    }

    public void deleteComment(int id, String user_id){
        Comment comment=boardRepository.findByCommentId(id);
        if(comment.getWriter().equals(user_id)){
            boardRepository.deleteComment(id,comment.getContent());
        }
    }

    public int findContentId(int comment_id){
        return boardRepository.findContentId(comment_id);
    }

    public void goodContent(int id)
    {
        Content content = boardRepository.findById(id);
        if(content != null)
        {
            content.setGood(content.getGood() + 1);
            boardRepository.edit(content);
        }
    }



    public void excelDataSave(TransactionHistory t){
        boardRepository.excelDataSave(t);
    }

    public List<TransactionHistory> GetexcelData(String type){
        return boardRepository.getDatafromExcel(type);
    }

    public List<TransactionHistory> GetDataByDate(String type, String date1, String date2){
        return boardRepository.getExcelByDate(type,date1,date2);
    }

    public List<TransactionHistory> requestProftandLoss(String date,String type){
        return boardRepository.getProfitandLoss(date,type);
    }

    public List<TransactionHistory> getUserTrade(String user_id){
        return boardRepository.getUserTrade(user_id);
    }


}
