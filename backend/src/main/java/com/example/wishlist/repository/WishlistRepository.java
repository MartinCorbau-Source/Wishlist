package com.example.wishlist.repository;

import com.example.wishlist.model.Wishlist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
  @EntityGraph(attributePaths = "items")
  Optional<Wishlist> findByOwnerKey(String ownerKey);
  @EntityGraph(attributePaths = {"items", "items.reservation"})
  Optional<Wishlist> findByShareToken(String shareToken);
}
