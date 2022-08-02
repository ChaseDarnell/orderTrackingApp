import dayjs from 'dayjs/esm';

import { StatusO } from 'app/entities/enumerations/status-o.model';

import { IOrder, NewOrder } from './order.model';

export const sampleWithRequiredData: IOrder = {
  id: 47761,
  driver: 'firewall web-enabled',
  pickUpDate: dayjs('2022-08-01T18:48'),
  rOrderNum: 'Franc',
};

export const sampleWithPartialData: IOrder = {
  id: 57558,
  driver: 'Cambridgeshire back-end violet',
  pickUpDate: dayjs('2022-08-02T05:27'),
  deliveryDate: dayjs('2022-08-02T10:28'),
  rOrderNum: 'International',
  statusO: StatusO['TBR'],
  scan: '../fake-data/blob/hipster.png',
  scanContentType: 'unknown',
};

export const sampleWithFullData: IOrder = {
  id: 82907,
  driver: 'Future',
  pickUpDate: dayjs('2022-08-02T14:54'),
  repairDate: dayjs('2022-08-01T22:10'),
  deliveryDate: dayjs('2022-08-01T16:32'),
  rOrderNum: 'Ford Director',
  invOrderNum: 'Fresh',
  statusO: StatusO['REPAIRED'],
  noteso: 'Creative Wooden',
  scan: '../fake-data/blob/hipster.png',
  scanContentType: 'unknown',
};

export const sampleWithNewData: NewOrder = {
  driver: 'Shoes revolutionary',
  pickUpDate: dayjs('2022-08-02T00:19'),
  rOrderNum: 'Operative',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
