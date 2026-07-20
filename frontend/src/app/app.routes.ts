import { Routes } from '@angular/router';
import { HomeComponent } from './home.component';
import { OwnerComponent } from './owner.component';
import { SharedComponent } from './shared.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'my-list', component: OwnerComponent },
  { path: 'list/:token', component: SharedComponent },
  { path: '**', redirectTo: '' }
];
