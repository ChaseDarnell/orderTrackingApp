import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOrder, NewOrder } from '../order.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrder for edit and NewOrderFormGroupInput for create.
 */
type OrderFormGroupInput = IOrder | PartialWithRequiredKeyOf<NewOrder>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOrder | NewOrder> = Omit<T, 'pickUpDate' | 'repairDate' | 'deliveryDate'> & {
  pickUpDate?: string | null;
  repairDate?: string | null;
  deliveryDate?: string | null;
};

type OrderFormRawValue = FormValueOf<IOrder>;

type NewOrderFormRawValue = FormValueOf<NewOrder>;

type OrderFormDefaults = Pick<NewOrder, 'id' | 'pickUpDate' | 'repairDate' | 'deliveryDate' | 'piecesIns'>;

type OrderFormGroupContent = {
  id: FormControl<OrderFormRawValue['id'] | NewOrder['id']>;
  driver: FormControl<OrderFormRawValue['driver']>;
  pickUpDate: FormControl<OrderFormRawValue['pickUpDate']>;
  repairDate: FormControl<OrderFormRawValue['repairDate']>;
  deliveryDate: FormControl<OrderFormRawValue['deliveryDate']>;
  rOrderNum: FormControl<OrderFormRawValue['rOrderNum']>;
  invOrderNum: FormControl<OrderFormRawValue['invOrderNum']>;
  statusO: FormControl<OrderFormRawValue['statusO']>;
  noteso: FormControl<OrderFormRawValue['noteso']>;
  scan: FormControl<OrderFormRawValue['scan']>;
  scanContentType: FormControl<OrderFormRawValue['scanContentType']>;
  customer: FormControl<OrderFormRawValue['customer']>;
  piecesIns: FormControl<OrderFormRawValue['piecesIns']>;
};

export type OrderFormGroup = FormGroup<OrderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrderFormService {
  createOrderFormGroup(order: OrderFormGroupInput = { id: null }): OrderFormGroup {
    const orderRawValue = this.convertOrderToOrderRawValue({
      ...this.getFormDefaults(),
      ...order,
    });
    return new FormGroup<OrderFormGroupContent>({
      id: new FormControl(
        { value: orderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      driver: new FormControl(orderRawValue.driver, {
        validators: [Validators.required],
      }),
      pickUpDate: new FormControl(orderRawValue.pickUpDate, {
        validators: [Validators.required],
      }),
      repairDate: new FormControl(orderRawValue.repairDate),
      deliveryDate: new FormControl(orderRawValue.deliveryDate),
      rOrderNum: new FormControl(orderRawValue.rOrderNum, {
        validators: [Validators.required],
      }),
      invOrderNum: new FormControl(orderRawValue.invOrderNum),
      statusO: new FormControl(orderRawValue.statusO),
      noteso: new FormControl(orderRawValue.noteso),
      scan: new FormControl(orderRawValue.scan),
      scanContentType: new FormControl(orderRawValue.scanContentType),
      customer: new FormControl(orderRawValue.customer),
      piecesIns: new FormControl(orderRawValue.piecesIns ?? []),
    });
  }

  getOrder(form: OrderFormGroup): IOrder | NewOrder {
    return this.convertOrderRawValueToOrder(form.getRawValue() as OrderFormRawValue | NewOrderFormRawValue);
  }

  resetForm(form: OrderFormGroup, order: OrderFormGroupInput): void {
    const orderRawValue = this.convertOrderToOrderRawValue({ ...this.getFormDefaults(), ...order });
    form.reset(
      {
        ...orderRawValue,
        id: { value: orderRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrderFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      pickUpDate: currentTime,
      repairDate: currentTime,
      deliveryDate: currentTime,
      piecesIns: [],
    };
  }

  private convertOrderRawValueToOrder(rawOrder: OrderFormRawValue | NewOrderFormRawValue): IOrder | NewOrder {
    return {
      ...rawOrder,
      pickUpDate: dayjs(rawOrder.pickUpDate, DATE_TIME_FORMAT),
      repairDate: dayjs(rawOrder.repairDate, DATE_TIME_FORMAT),
      deliveryDate: dayjs(rawOrder.deliveryDate, DATE_TIME_FORMAT),
    };
  }

  private convertOrderToOrderRawValue(
    order: IOrder | (Partial<NewOrder> & OrderFormDefaults)
  ): OrderFormRawValue | PartialWithRequiredKeyOf<NewOrderFormRawValue> {
    return {
      ...order,
      pickUpDate: order.pickUpDate ? order.pickUpDate.format(DATE_TIME_FORMAT) : undefined,
      repairDate: order.repairDate ? order.repairDate.format(DATE_TIME_FORMAT) : undefined,
      deliveryDate: order.deliveryDate ? order.deliveryDate.format(DATE_TIME_FORMAT) : undefined,
      piecesIns: order.piecesIns ?? [],
    };
  }
}
