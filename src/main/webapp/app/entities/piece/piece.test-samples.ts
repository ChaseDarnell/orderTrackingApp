import { IPiece, NewPiece } from './piece.model';

export const sampleWithRequiredData: IPiece = {
  id: 55316,
  serial: 'Concrete Future array',
  model: 'Inverse',
  manu: 'wireless Salad',
};

export const sampleWithPartialData: IPiece = {
  id: 46699,
  serial: 'California Buckinghamshire',
  model: 'SDD Car task-force',
  desc: 'Account',
  manu: 'Plastic input withdrawal',
};

export const sampleWithFullData: IPiece = {
  id: 7238,
  serial: 'heuristic Strategist',
  model: 'calculating',
  desc: 'Nebraska payment',
  manu: 'Planner Grocery',
  notesp: 'Cambridgeshire Beauty override',
};

export const sampleWithNewData: NewPiece = {
  serial: 'Small cultivate Account',
  model: 'Global',
  manu: 'Steel Burg',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
