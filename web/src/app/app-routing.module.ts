import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PlayerComponent} from "./player/player.component";
import {BoardComponent} from "./board/board.component";

const routes: Routes = [
  {path: '', component: PlayerComponent, pathMatch: 'full'},
  {path: 'game', component: BoardComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
