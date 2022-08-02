import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPiece, NewPiece } from '../piece.model';

export type PartialUpdatePiece = Partial<IPiece> & Pick<IPiece, 'id'>;

export type EntityResponseType = HttpResponse<IPiece>;
export type EntityArrayResponseType = HttpResponse<IPiece[]>;

@Injectable({ providedIn: 'root' })
export class PieceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/pieces');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(piece: NewPiece): Observable<EntityResponseType> {
    return this.http.post<IPiece>(this.resourceUrl, piece, { observe: 'response' });
  }

  update(piece: IPiece): Observable<EntityResponseType> {
    return this.http.put<IPiece>(`${this.resourceUrl}/${this.getPieceIdentifier(piece)}`, piece, { observe: 'response' });
  }

  partialUpdate(piece: PartialUpdatePiece): Observable<EntityResponseType> {
    return this.http.patch<IPiece>(`${this.resourceUrl}/${this.getPieceIdentifier(piece)}`, piece, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPiece>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPiece[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPieceIdentifier(piece: Pick<IPiece, 'id'>): number {
    return piece.id;
  }

  comparePiece(o1: Pick<IPiece, 'id'> | null, o2: Pick<IPiece, 'id'> | null): boolean {
    return o1 && o2 ? this.getPieceIdentifier(o1) === this.getPieceIdentifier(o2) : o1 === o2;
  }

  addPieceToCollectionIfMissing<Type extends Pick<IPiece, 'id'>>(
    pieceCollection: Type[],
    ...piecesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const pieces: Type[] = piecesToCheck.filter(isPresent);
    if (pieces.length > 0) {
      const pieceCollectionIdentifiers = pieceCollection.map(pieceItem => this.getPieceIdentifier(pieceItem)!);
      const piecesToAdd = pieces.filter(pieceItem => {
        const pieceIdentifier = this.getPieceIdentifier(pieceItem);
        if (pieceCollectionIdentifiers.includes(pieceIdentifier)) {
          return false;
        }
        pieceCollectionIdentifiers.push(pieceIdentifier);
        return true;
      });
      return [...piecesToAdd, ...pieceCollection];
    }
    return pieceCollection;
  }
}
