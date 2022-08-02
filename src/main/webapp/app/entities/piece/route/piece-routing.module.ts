import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PieceComponent } from '../list/piece.component';
import { PieceDetailComponent } from '../detail/piece-detail.component';
import { PieceUpdateComponent } from '../update/piece-update.component';
import { PieceRoutingResolveService } from './piece-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const pieceRoute: Routes = [
  {
    path: '',
    component: PieceComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PieceDetailComponent,
    resolve: {
      piece: PieceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PieceUpdateComponent,
    resolve: {
      piece: PieceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PieceUpdateComponent,
    resolve: {
      piece: PieceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(pieceRoute)],
  exports: [RouterModule],
})
export class PieceRoutingModule {}
