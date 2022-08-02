import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPiece } from '../piece.model';
import { PieceService } from '../service/piece.service';

@Injectable({ providedIn: 'root' })
export class PieceRoutingResolveService implements Resolve<IPiece | null> {
  constructor(protected service: PieceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPiece | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((piece: HttpResponse<IPiece>) => {
          if (piece.body) {
            return of(piece.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
