import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  template: `
    <header><a routerLink="/" class="brand">wishful<span>✦</span></a></header>
    <main><router-outlet /></main>
    <footer>Made for thoughtful surprises.</footer>
  `
})
export class AppComponent {}
