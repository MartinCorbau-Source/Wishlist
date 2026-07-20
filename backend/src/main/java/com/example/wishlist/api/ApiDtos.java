package com.example.wishlist.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public final class ApiDtos {
  private ApiDtos() {}

  public record CreateWishlistRequest(@NotBlank @Size(max=80) String ownerName) {}
  public record CreateWishlistResponse(String ownerKey, String shareToken) {}
  public record AddItemRequest(
      @NotBlank @Size(max=120) String name,
      @Size(max=1000) String description,
      @Size(max=500) String url) {}
  public record ReserveRequest(
      @NotBlank @Size(max=80) String reservedBy,
      @Size(max=500) String note) {}

  // Deliberately contains no reservation information.
  public record OwnerItem(UUID id, String name, String description, String url) {}
  public record OwnerWishlist(String ownerName, String shareToken, List<OwnerItem> items) {}

  public record GuestItem(
      UUID id, String name, String description, String url, boolean reserved,
      String reservedBy, String note) {}
  public record GuestWishlist(String ownerName, List<GuestItem> items) {}
}
