export interface ICustomer {
  id: number;
  name?: string | null;
  locale?: string | null;
  notesc?: string | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
