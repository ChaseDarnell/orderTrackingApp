import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PieceFormService } from './piece-form.service';
import { PieceService } from '../service/piece.service';
import { IPiece } from '../piece.model';
import { IOrder } from 'app/entities/order/order.model';
import { OrderService } from 'app/entities/order/service/order.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';

import { PieceUpdateComponent } from './piece-update.component';

describe('Piece Management Update Component', () => {
  let comp: PieceUpdateComponent;
  let fixture: ComponentFixture<PieceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let pieceFormService: PieceFormService;
  let pieceService: PieceService;
  let orderService: OrderService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PieceUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PieceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PieceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pieceFormService = TestBed.inject(PieceFormService);
    pieceService = TestBed.inject(PieceService);
    orderService = TestBed.inject(OrderService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Order query and add missing value', () => {
      const piece: IPiece = { id: 456 };
      const ordersBelongeds: IOrder[] = [{ id: 67107 }];
      piece.ordersBelongeds = ordersBelongeds;

      const orderCollection: IOrder[] = [{ id: 43535 }];
      jest.spyOn(orderService, 'query').mockReturnValue(of(new HttpResponse({ body: orderCollection })));
      const additionalOrders = [...ordersBelongeds];
      const expectedCollection: IOrder[] = [...additionalOrders, ...orderCollection];
      jest.spyOn(orderService, 'addOrderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ piece });
      comp.ngOnInit();

      expect(orderService.query).toHaveBeenCalled();
      expect(orderService.addOrderToCollectionIfMissing).toHaveBeenCalledWith(
        orderCollection,
        ...additionalOrders.map(expect.objectContaining)
      );
      expect(comp.ordersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Customer query and add missing value', () => {
      const piece: IPiece = { id: 456 };
      const customer: ICustomer = { id: 32186 };
      piece.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 92442 }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ piece });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(expect.objectContaining)
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const piece: IPiece = { id: 456 };
      const ordersBelonged: IOrder = { id: 10581 };
      piece.ordersBelongeds = [ordersBelonged];
      const customer: ICustomer = { id: 97422 };
      piece.customer = customer;

      activatedRoute.data = of({ piece });
      comp.ngOnInit();

      expect(comp.ordersSharedCollection).toContain(ordersBelonged);
      expect(comp.customersSharedCollection).toContain(customer);
      expect(comp.piece).toEqual(piece);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPiece>>();
      const piece = { id: 123 };
      jest.spyOn(pieceFormService, 'getPiece').mockReturnValue(piece);
      jest.spyOn(pieceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ piece });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: piece }));
      saveSubject.complete();

      // THEN
      expect(pieceFormService.getPiece).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(pieceService.update).toHaveBeenCalledWith(expect.objectContaining(piece));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPiece>>();
      const piece = { id: 123 };
      jest.spyOn(pieceFormService, 'getPiece').mockReturnValue({ id: null });
      jest.spyOn(pieceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ piece: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: piece }));
      saveSubject.complete();

      // THEN
      expect(pieceFormService.getPiece).toHaveBeenCalled();
      expect(pieceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPiece>>();
      const piece = { id: 123 };
      jest.spyOn(pieceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ piece });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pieceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareOrder', () => {
      it('Should forward to orderService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(orderService, 'compareOrder');
        comp.compareOrder(entity, entity2);
        expect(orderService.compareOrder).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCustomer', () => {
      it('Should forward to customerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
