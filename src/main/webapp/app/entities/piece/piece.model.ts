import { IOrder } from 'app/entities/order/order.model';
import { ICustomer } from 'app/entities/customer/customer.model';

export interface IPiece {
  id: number;
  serial?: string | null;
  model?: string | null;
  desc?: string | null;
  manu?: string | null;
  notesp?: string | null;
  ordersBelongeds?: Pick<IOrder, 'id'>[] | null;
  customer?: Pick<ICustomer, 'id'> | null;
}

export type NewPiece = Omit<IPiece, 'id'> & { id: null };
