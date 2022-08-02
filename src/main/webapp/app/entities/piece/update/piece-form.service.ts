import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPiece, NewPiece } from '../piece.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPiece for edit and NewPieceFormGroupInput for create.
 */
type PieceFormGroupInput = IPiece | PartialWithRequiredKeyOf<NewPiece>;

type PieceFormDefaults = Pick<NewPiece, 'id' | 'ordersBelongeds'>;

type PieceFormGroupContent = {
  id: FormControl<IPiece['id'] | NewPiece['id']>;
  serial: FormControl<IPiece['serial']>;
  model: FormControl<IPiece['model']>;
  desc: FormControl<IPiece['desc']>;
  manu: FormControl<IPiece['manu']>;
  notesp: FormControl<IPiece['notesp']>;
  ordersBelongeds: FormControl<IPiece['ordersBelongeds']>;
  customer: FormControl<IPiece['customer']>;
};

export type PieceFormGroup = FormGroup<PieceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PieceFormService {
  createPieceFormGroup(piece: PieceFormGroupInput = { id: null }): PieceFormGroup {
    const pieceRawValue = {
      ...this.getFormDefaults(),
      ...piece,
    };
    return new FormGroup<PieceFormGroupContent>({
      id: new FormControl(
        { value: pieceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      serial: new FormControl(pieceRawValue.serial, {
        validators: [Validators.required],
      }),
      model: new FormControl(pieceRawValue.model, {
        validators: [Validators.required],
      }),
      desc: new FormControl(pieceRawValue.desc),
      manu: new FormControl(pieceRawValue.manu, {
        validators: [Validators.required],
      }),
      notesp: new FormControl(pieceRawValue.notesp),
      ordersBelongeds: new FormControl(pieceRawValue.ordersBelongeds ?? []),
      customer: new FormControl(pieceRawValue.customer),
    });
  }

  getPiece(form: PieceFormGroup): IPiece | NewPiece {
    return form.getRawValue() as IPiece | NewPiece;
  }

  resetForm(form: PieceFormGroup, piece: PieceFormGroupInput): void {
    const pieceRawValue = { ...this.getFormDefaults(), ...piece };
    form.reset(
      {
        ...pieceRawValue,
        id: { value: pieceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PieceFormDefaults {
    return {
      id: null,
      ordersBelongeds: [],
    };
  }
}
