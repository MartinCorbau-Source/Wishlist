package com.example.wishlist.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Reservation {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "item_id", nullable = false, unique = true)
  private WishItem item;
  @Column(nullable = false, length = 80)
  private String reservedBy;
  @Column(length = 500)
  private String note;
  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  protected Reservation() {}
  public Reservation(WishItem item, String reservedBy, String note) {
    this.item = item; this.reservedBy = reservedBy; this.note = note;
  }
  public String getReservedBy() { return reservedBy; }
  public String getNote() { return note; }
}
