package boot.jpa.batch.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Hero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;

    @CreatedDate
    private LocalDateTime createdDate;

    @Builder
    public Hero(Long id, String name, int age, LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.createdDate = createdDate;
    }
}
