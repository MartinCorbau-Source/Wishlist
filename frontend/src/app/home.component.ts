import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { WishlistApi } from './wishlist-api.service';

@Component({
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <section class="hero">
      <p class="eyebrow">A little less guessing</p>
      <h1>Make wishes.<br><em>Keep the surprise.</em></h1>
      <p class="intro">Collect the things you'd love. Friends can quietly choose a gift without you ever seeing what is taken.</p>
      @if (hasList) {
        <a class="button primary" routerLink="/my-list">Open my wishlist</a>
        <button class="link-button" (click)="startAnother()">Create another</button>
      } @else {
        <form (ngSubmit)="create()" class="create-form">
          <label for="owner">What should we call you?</label>
          <div class="input-row">
            <input id="owner" name="owner" [(ngModel)]="ownerName" maxlength="80" required placeholder="Your name">
            <button class="button primary" [disabled]="busy() || !ownerName.trim()">Create my list</button>
          </div>
          @if (error()) { <p class="error">{{ error() }}</p> }
        </form>
      }
    </section>
    <section class="steps">
      <article><b>01</b><h2>Add your wishes</h2><p>Links, notes, sizes—anything that helps.</p></article>
      <article><b>02</b><h2>Share one link</h2><p>Send it to family and friends.</p></article>
      <article><b>03</b><h2>Stay surprised</h2><p>Reservations are invisible to you.</p></article>
    </section>
  `
})
export class HomeComponent {
  private api = inject(WishlistApi); private router = inject(Router);
  ownerName = ''; busy = signal(false); error = signal('');
  get hasList() { return !!localStorage.getItem('wishlistOwnerKey'); }
  create() {
    this.busy.set(true); this.error.set('');
    this.api.create(this.ownerName).subscribe({
      next: value => { localStorage.setItem('wishlistOwnerKey', value.ownerKey); this.router.navigateByUrl('/my-list'); },
      error: () => { this.error.set('We could not create your list. Please try again.'); this.busy.set(false); }
    });
  }
  startAnother() { localStorage.removeItem('wishlistOwnerKey'); location.reload(); }
}
