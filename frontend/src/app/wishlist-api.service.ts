import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { GuestWishlist, Item, OwnerWishlist } from './models';

@Injectable({ providedIn: 'root' })
export class WishlistApi {
  private http = inject(HttpClient);
  create(ownerName: string) {
    return this.http.post<{ownerKey: string; shareToken: string}>('/api/wishlists', { ownerName });
  }
  owner(ownerKey: string) { return this.http.get<OwnerWishlist>(`/api/owner/${ownerKey}`); }
  add(ownerKey: string, item: Omit<Item, 'id'>) {
    return this.http.post<Item>(`/api/owner/${ownerKey}/items`, item);
  }
  remove(ownerKey: string, itemId: string) {
    return this.http.delete<void>(`/api/owner/${ownerKey}/items/${itemId}`);
  }
  shared(token: string) { return this.http.get<GuestWishlist>(`/api/shared/${token}`); }
  reserve(token: string, itemId: string, reservedBy: string, note: string) {
    return this.http.post<void>(`/api/shared/${token}/items/${itemId}/reservation`, { reservedBy, note });
  }
}
