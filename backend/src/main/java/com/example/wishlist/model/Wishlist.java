package com.example.wishlist.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Wishlist {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  @Column(nullable = false, length = 80)
  private String ownerName;
  @Column(nullable = false, unique = true, updatable = false)
  private String ownerKey;
  @Column(nullable = false, unique = true, updatable = false)
  private String shareToken;
  @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("createdAt ASC")
  private List<WishItem> items = new ArrayList<>();

  protected Wishlist() {}
  public Wishlist(String ownerName) {
    this.ownerName = ownerName;
    this.ownerKey = UUID.randomUUID().toString();
    this.shareToken = UUID.randomUUID().toString();
  }
  public UUID getId() { return id; }
  public String getOwnerName() { return ownerName; }
  public String getOwnerKey() { return ownerKey; }
  public String getShareToken() { return shareToken; }
  public List<WishItem> getItems() { return items; }
}
