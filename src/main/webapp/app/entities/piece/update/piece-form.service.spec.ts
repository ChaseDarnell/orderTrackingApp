import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../piece.test-samples';

import { PieceFormService } from './piece-form.service';

describe('Piece Form Service', () => {
  let service: PieceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PieceFormService);
  });

  describe('Service methods', () => {
    describe('createPieceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPieceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            serial: expect.any(Object),
            model: expect.any(Object),
            desc: expect.any(Object),
            manu: expect.any(Object),
            notesp: expect.any(Object),
            ordersBelongeds: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });

      it('passing IPiece should create a new form with FormGroup', () => {
        const formGroup = service.createPieceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            serial: expect.any(Object),
            model: expect.any(Object),
            desc: expect.any(Object),
            manu: expect.any(Object),
            notesp: expect.any(Object),
            ordersBelongeds: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });
    });

    describe('getPiece', () => {
      it('should return NewPiece for default Piece initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPieceFormGroup(sampleWithNewData);

        const piece = service.getPiece(formGroup) as any;

        expect(piece).toMatchObject(sampleWithNewData);
      });

      it('should return NewPiece for empty Piece initial value', () => {
        const formGroup = service.createPieceFormGroup();

        const piece = service.getPiece(formGroup) as any;

        expect(piece).toMatchObject({});
      });

      it('should return IPiece', () => {
        const formGroup = service.createPieceFormGroup(sampleWithRequiredData);

        const piece = service.getPiece(formGroup) as any;

        expect(piece).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPiece should not enable id FormControl', () => {
        const formGroup = service.createPieceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPiece should disable id FormControl', () => {
        const formGroup = service.createPieceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
