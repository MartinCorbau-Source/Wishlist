import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { GuestItem, GuestWishlist } from './models';
import { WishlistApi } from './wishlist-api.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  template: `
    @if (list(); as data) {
      <section class="page-heading guest-heading">
        <p class="eyebrow">A wishlist from</p><h1>{{ data.ownerName }}</h1>
        <p class="muted">Choose something they'll love. They won't see what you reserve.</p>
      </section>
      <section class="guest-list">
        @for (item of data.items; track item.id; let i = $index) {
          <article class="guest-card" [class.taken]="item.reserved">
            <div class="status">{{ item.reserved ? 'Taken' : 'Available' }}</div>
            <span class="number">{{ (i + 1).toString().padStart(2, '0') }}</span>
            <div class="guest-content"><h2>{{ item.name }}</h2>@if(item.description){<p>{{ item.description }}</p>}@if(item.url){<a [href]="item.url" target="_blank" rel="noopener">View item ↗</a>}</div>
            @if (item.reserved) {
              <div class="reservation"><strong>Reserved by {{ item.reservedBy }}</strong>@if(item.note){<span>{{ item.note }}</span>}</div>
            } @else if (selected()?.id === item.id) {
              <form class="reserve-form" (ngSubmit)="reserve(item)">
                <label>Your name<input name="reservedBy" [(ngModel)]="reservedBy" maxlength="80" required placeholder="So others know"></label>
                <label>What will you take? <small>optional</small><input name="note" [(ngModel)]="note" maxlength="500" placeholder="e.g. the blue one"></label>
                <div><button type="button" class="button secondary" (click)="selected.set(null)">Cancel</button><button class="button primary" [disabled]="!reservedBy.trim() || saving()">Confirm</button></div>
              </form>
            } @else { <button class="button primary reserve-button" (click)="choose(item)">I'll take this</button> }
          </article>
        } @empty { <div class="empty">There are no wishes here yet.</div> }
        @if(error()){<p class="error">{{ error() }}</p>}
      </section>
    } @else if(error()) { <section class="hero"><h1>This wishlist isn't available.</h1><p class="error">Check that the shared link is complete.</p></section> }
    @else { <div class="loading">Unwrapping the wishlist…</div> }
  `
})
export class SharedComponent {
  private api = inject(WishlistApi); private token = inject(ActivatedRoute).snapshot.paramMap.get('token') ?? '';
  list = signal<GuestWishlist | null>(null); selected = signal<GuestItem | null>(null); saving = signal(false); error = signal('');
  reservedBy = ''; note = '';
  constructor() { this.load(); }
  load() { this.api.shared(this.token).subscribe({next: x => this.list.set(x), error: () => this.error.set('Wishlist not found.')}); }
  choose(item: GuestItem) { this.selected.set(item); this.reservedBy=''; this.note=''; this.error.set(''); }
  reserve(item: GuestItem) {
    this.saving.set(true);
    this.api.reserve(this.token, item.id, this.reservedBy, this.note).subscribe({
      next: () => { this.selected.set(null); this.saving.set(false); this.load(); },
      error: e => { this.error.set(e.status === 409 ? 'Someone just reserved this item. The list has been refreshed.' : 'Could not save the reservation.'); this.saving.set(false); this.load(); }
    });
  }
}
