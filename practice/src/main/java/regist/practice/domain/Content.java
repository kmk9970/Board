package regist.practice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "content")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name="title")
    private String title;

    @Column(name="texts")
    private String texts;

    @Column(name="writer")
    private String writer;

    @Column(name="updateDate")
    private String updateDate;

    @OneToMany(mappedBy = "content", orphanRemoval = true)
    private List<Comment> comments=new ArrayList<Comment>();

    @Column(name="good")
    private int good = 0;

}
