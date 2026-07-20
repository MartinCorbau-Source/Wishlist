package com.example.wishlist.api;

import com.example.wishlist.api.ApiDtos.*;
import com.example.wishlist.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class WishlistController {
  private final WishlistService service;
  public WishlistController(WishlistService service) { this.service = service; }

  @PostMapping("/wishlists") @ResponseStatus(HttpStatus.CREATED)
  public CreateWishlistResponse create(@Valid @RequestBody CreateWishlistRequest request) {
    return service.create(request);
  }
  @GetMapping("/owner/{ownerKey}")
  public OwnerWishlist ownerView(@PathVariable String ownerKey) { return service.ownerView(ownerKey); }
  @PostMapping("/owner/{ownerKey}/items") @ResponseStatus(HttpStatus.CREATED)
  public OwnerItem add(@PathVariable String ownerKey, @Valid @RequestBody AddItemRequest request) {
    return service.addItem(ownerKey, request);
  }
  @DeleteMapping("/owner/{ownerKey}/items/{itemId}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String ownerKey, @PathVariable UUID itemId) {
    service.deleteItem(ownerKey, itemId);
  }
  @GetMapping("/shared/{shareToken}")
  public GuestWishlist guestView(@PathVariable String shareToken) { return service.guestView(shareToken); }
  @PostMapping("/shared/{shareToken}/items/{itemId}/reservation") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void reserve(@PathVariable String shareToken, @PathVariable UUID itemId,
                      @Valid @RequestBody ReserveRequest request) {
    service.reserve(shareToken, itemId, request);
  }
}
