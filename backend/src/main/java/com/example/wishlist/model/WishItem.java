package com.example.wishlist.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class WishItem {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Wishlist wishlist;
  @Column(nullable = false, length = 120)
  private String name;
  @Column(length = 1000)
  private String description;
  @Column(length = 500)
  private String url;
  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();
  @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
  private Reservation reservation;

  protected WishItem() {}
  public WishItem(Wishlist wishlist, String name, String description, String url) {
    this.wishlist = wishlist; this.name = name; this.description = description; this.url = url;
  }
  public UUID getId() { return id; }
  public Wishlist getWishlist() { return wishlist; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public String getUrl() { return url; }
  public Instant getCreatedAt() { return createdAt; }
  public Reservation getReservation() { return reservation; }
  public void setReservation(Reservation reservation) { this.reservation = reservation; }
}
