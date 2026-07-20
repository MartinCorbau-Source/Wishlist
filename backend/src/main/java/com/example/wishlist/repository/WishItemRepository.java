package com.example.wishlist.repository;

import com.example.wishlist.model.WishItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface WishItemRepository extends JpaRepository<WishItem, UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<WishItem> findWithLockById(UUID id);
}
