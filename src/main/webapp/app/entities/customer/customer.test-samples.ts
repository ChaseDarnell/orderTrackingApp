import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 24379,
  name: 'Operations',
  locale: 'Communications Club',
};

export const sampleWithPartialData: ICustomer = {
  id: 75018,
  name: 'Namibia Cambridgeshire',
  locale: 'Chips',
  notesc: 'Cotton payment',
};

export const sampleWithFullData: ICustomer = {
  id: 26718,
  name: 'input',
  locale: 'front-end Towels',
  notesc: 'intangible Balanced',
};

export const sampleWithNewData: NewCustomer = {
  name: 'extranet seize',
  locale: 'Facilitator',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
