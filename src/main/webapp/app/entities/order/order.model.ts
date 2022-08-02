import dayjs from 'dayjs/esm';
import { ICustomer } from 'app/entities/customer/customer.model';
import { IPiece } from 'app/entities/piece/piece.model';
import { StatusO } from 'app/entities/enumerations/status-o.model';

export interface IOrder {
  id: number;
  driver?: string | null;
  pickUpDate?: dayjs.Dayjs | null;
  repairDate?: dayjs.Dayjs | null;
  deliveryDate?: dayjs.Dayjs | null;
  rOrderNum?: string | null;
  invOrderNum?: string | null;
  statusO?: StatusO | null;
  noteso?: string | null;
  scan?: string | null;
  scanContentType?: string | null;
  customer?: Pick<ICustomer, 'id'> | null;
  piecesIns?: Pick<IPiece, 'id'>[] | null;
}

export type NewOrder = Omit<IOrder, 'id'> & { id: null };
