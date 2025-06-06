package io.github.jotabrc.repository;

import io.github.jotabrc.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByUuid(String uuid);

    boolean existsByUuid(String uuid);
}
