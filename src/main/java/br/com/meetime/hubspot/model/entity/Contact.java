package br.com.meetime.hubspot.model.entity;


import jakarta.persistence.*;
import lombok.*;

@Table(name = "contact", schema = "public")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long contactId;
    @Column(name = "email")
    private String email;
    @Column(name = "first_name", length = 25, nullable = false)
    private String firstName;
    @Column(name = "last_name", length = 25, nullable = false)
    private String lastName;
}