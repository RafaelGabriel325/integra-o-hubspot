package br.com.meetime.hubspot.repository;

import br.com.meetime.hubspot.model.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookRepository extends JpaRepository<Contact, Long> {
}