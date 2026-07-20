package com.example.wishlist.service;

import com.example.wishlist.api.ApiDtos.*;
import com.example.wishlist.api.ConflictException;
import com.example.wishlist.api.NotFoundException;
import com.example.wishlist.model.*;
import com.example.wishlist.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class WishlistService {
  private final WishlistRepository wishlists;
  private final WishItemRepository items;

  public WishlistService(WishlistRepository wishlists, WishItemRepository items) {
    this.wishlists = wishlists; this.items = items;
  }

  @Transactional
  public CreateWishlistResponse create(CreateWishlistRequest request) {
    Wishlist list = wishlists.save(new Wishlist(request.ownerName().trim()));
    return new CreateWishlistResponse(list.getOwnerKey(), list.getShareToken());
  }

  @Transactional(readOnly = true)
  public OwnerWishlist ownerView(String ownerKey) {
    Wishlist list = ownerList(ownerKey);
    return new OwnerWishlist(list.getOwnerName(), list.getShareToken(), list.getItems().stream()
        .map(i -> new OwnerItem(i.getId(), i.getName(), i.getDescription(), i.getUrl())).toList());
  }

  @Transactional
  public OwnerItem addItem(String ownerKey, AddItemRequest request) {
    Wishlist list = ownerList(ownerKey);
    WishItem item = items.save(new WishItem(list, request.name().trim(), clean(request.description()), clean(request.url())));
    return new OwnerItem(item.getId(), item.getName(), item.getDescription(), item.getUrl());
  }

  @Transactional
  public void deleteItem(String ownerKey, UUID itemId) {
    Wishlist list = ownerList(ownerKey);
    WishItem item = items.findById(itemId).filter(i -> i.getWishlist().getId().equals(list.getId()))
        .orElseThrow(() -> new NotFoundException("Wishlist item not found"));
    items.delete(item);
  }

  @Transactional(readOnly = true)
  public GuestWishlist guestView(String shareToken) {
    Wishlist list = sharedList(shareToken);
    return new GuestWishlist(list.getOwnerName(), list.getItems().stream().map(i -> {
      Reservation r = i.getReservation();
      return new GuestItem(i.getId(), i.getName(), i.getDescription(), i.getUrl(), r != null,
          r == null ? null : r.getReservedBy(), r == null ? null : r.getNote());
    }).toList());
  }

  @Transactional
  public void reserve(String shareToken, UUID itemId, ReserveRequest request) {
    Wishlist list = sharedList(shareToken);
    WishItem item = items.findWithLockById(itemId)
        .filter(i -> i.getWishlist().getId().equals(list.getId()))
        .orElseThrow(() -> new NotFoundException("Wishlist item not found"));
    if (item.getReservation() != null) throw new ConflictException("This item has already been reserved");
    item.setReservation(new Reservation(item, request.reservedBy().trim(), clean(request.note())));
  }

  private Wishlist ownerList(String key) {
    return wishlists.findByOwnerKey(key).orElseThrow(() -> new NotFoundException("Wishlist not found"));
  }
  private Wishlist sharedList(String token) {
    return wishlists.findByShareToken(token).orElseThrow(() -> new NotFoundException("Wishlist not found"));
  }
  private String clean(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}
