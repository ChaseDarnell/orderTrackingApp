import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PieceFormService, PieceFormGroup } from './piece-form.service';
import { IPiece } from '../piece.model';
import { PieceService } from '../service/piece.service';
import { IOrder } from 'app/entities/order/order.model';
import { OrderService } from 'app/entities/order/service/order.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';

@Component({
  selector: 'jhi-piece-update',
  templateUrl: './piece-update.component.html',
})
export class PieceUpdateComponent implements OnInit {
  isSaving = false;
  piece: IPiece | null = null;

  ordersSharedCollection: IOrder[] = [];
  customersSharedCollection: ICustomer[] = [];

  editForm: PieceFormGroup = this.pieceFormService.createPieceFormGroup();

  constructor(
    protected pieceService: PieceService,
    protected pieceFormService: PieceFormService,
    protected orderService: OrderService,
    protected customerService: CustomerService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOrder = (o1: IOrder | null, o2: IOrder | null): boolean => this.orderService.compareOrder(o1, o2);

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ piece }) => {
      this.piece = piece;
      if (piece) {
        this.updateForm(piece);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const piece = this.pieceFormService.getPiece(this.editForm);
    if (piece.id !== null) {
      this.subscribeToSaveResponse(this.pieceService.update(piece));
    } else {
      this.subscribeToSaveResponse(this.pieceService.create(piece));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPiece>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(piece: IPiece): void {
    this.piece = piece;
    this.pieceFormService.resetForm(this.editForm, piece);

    this.ordersSharedCollection = this.orderService.addOrderToCollectionIfMissing<IOrder>(
      this.ordersSharedCollection,
      ...(piece.ordersBelongeds ?? [])
    );
    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      piece.customer
    );
  }

  protected loadRelationshipsOptions(): void {
    this.orderService
      .query()
      .pipe(map((res: HttpResponse<IOrder[]>) => res.body ?? []))
      .pipe(
        map((orders: IOrder[]) => this.orderService.addOrderToCollectionIfMissing<IOrder>(orders, ...(this.piece?.ordersBelongeds ?? [])))
      )
      .subscribe((orders: IOrder[]) => (this.ordersSharedCollection = orders));

    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) => this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.piece?.customer))
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }
}
