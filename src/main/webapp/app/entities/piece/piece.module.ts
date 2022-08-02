import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PieceComponent } from './list/piece.component';
import { PieceDetailComponent } from './detail/piece-detail.component';
import { PieceUpdateComponent } from './update/piece-update.component';
import { PieceDeleteDialogComponent } from './delete/piece-delete-dialog.component';
import { PieceRoutingModule } from './route/piece-routing.module';

@NgModule({
  imports: [SharedModule, PieceRoutingModule],
  declarations: [PieceComponent, PieceDetailComponent, PieceUpdateComponent, PieceDeleteDialogComponent],
})
export class PieceModule {}
