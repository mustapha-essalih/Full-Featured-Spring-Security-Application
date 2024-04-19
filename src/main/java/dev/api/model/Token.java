package dev.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Token {

    @Id  
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

  @Column(unique = true)
  private String token;

    private Date createdAt;

    private Date expiresAt;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
        nullable = false,
        name = "user_id"
    )
    private User user;
}
