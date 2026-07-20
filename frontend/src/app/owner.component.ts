import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { OwnerWishlist } from './models';
import { WishlistApi } from './wishlist-api.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  template: `
    @if (list(); as data) {
      <section class="page-heading">
        <p class="eyebrow">{{ data.ownerName }}'s collection</p>
        <h1>My wishlist</h1>
        <p class="muted">You won't see which wishes have been reserved. That's where the magic lives.</p>
      </section>
      <section class="share-card">
        <div><span>Share this link</span><strong>{{ shareUrl }}</strong></div>
        <button class="button secondary" (click)="copy()">{{ copied() ? 'Copied!' : 'Copy link' }}</button>
      </section>
      <section class="content-grid">
        <form class="panel add-form" (ngSubmit)="add()">
          <p class="eyebrow">A new wish</p><h2>Add something lovely</h2>
          <label>What is it?<input name="name" [(ngModel)]="name" maxlength="120" required placeholder="A beautiful cookbook"></label>
          <label>Link <small>optional</small><input name="url" [(ngModel)]="url" maxlength="500" type="url" placeholder="https://…"></label>
          <label>Helpful details <small>optional</small><textarea name="description" [(ngModel)]="description" maxlength="1000" rows="4" placeholder="Edition, colour, size…"></textarea></label>
          <button class="button primary" [disabled]="saving() || !name.trim()">Add to my list</button>
          @if (error()) { <p class="error">{{ error() }}</p> }
        </form>
        <div class="wishes">
          <div class="section-title"><h2>{{ data.items.length }} {{ data.items.length === 1 ? 'wish' : 'wishes' }}</h2></div>
          @for (item of data.items; track item.id; let i = $index) {
            <article class="wish-card">
              <span class="number">{{ (i + 1).toString().padStart(2, '0') }}</span>
              <div><h3>{{ item.name }}</h3>@if(item.description){<p>{{ item.description }}</p>}@if(item.url){<a [href]="item.url" target="_blank" rel="noopener">View item ↗</a>}</div>
              <button class="icon-button" title="Remove wish" (click)="remove(item.id)">×</button>
            </article>
          } @empty { <div class="empty">Your wishlist is waiting for its first wish.</div> }
        </div>
      </section>
    } @else if (error()) {
      <section class="hero"><h1>We couldn't open your list.</h1><p class="error">{{ error() }}</p><button class="button primary" (click)="reset()">Start over</button></section>
    } @else { <div class="loading">Opening your wishlist…</div> }
  `
})
export class OwnerComponent {
  private api = inject(WishlistApi); private router = inject(Router);
  private key = localStorage.getItem('wishlistOwnerKey') ?? '';
  list = signal<OwnerWishlist | null>(null); saving = signal(false); copied = signal(false); error = signal('');
  name = ''; url = ''; description = '';
  get shareUrl() { return `${location.origin}/list/${this.list()?.shareToken}`; }
  constructor() { if (!this.key) this.router.navigateByUrl('/'); else this.load(); }
  load() { this.api.owner(this.key).subscribe({ next: x => this.list.set(x), error: () => this.error.set('The private key is missing or no longer valid.') }); }
  add() {
    this.saving.set(true); this.error.set('');
    this.api.add(this.key, { name: this.name, url: this.url, description: this.description }).subscribe({
      next: item => { this.list.update(x => x ? {...x, items: [...x.items, item]} : x); this.name=''; this.url=''; this.description=''; this.saving.set(false); },
      error: () => { this.error.set('Could not add that wish.'); this.saving.set(false); }
    });
  }
  remove(id: string) { this.api.remove(this.key, id).subscribe(() => this.list.update(x => x ? {...x, items: x.items.filter(i => i.id !== id)} : x)); }
  async copy() { await navigator.clipboard.writeText(this.shareUrl); this.copied.set(true); setTimeout(() => this.copied.set(false), 1800); }
  reset() { localStorage.removeItem('wishlistOwnerKey'); this.router.navigateByUrl('/'); }
}
