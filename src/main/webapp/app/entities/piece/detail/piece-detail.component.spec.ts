import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PieceDetailComponent } from './piece-detail.component';

describe('Piece Management Detail Component', () => {
  let comp: PieceDetailComponent;
  let fixture: ComponentFixture<PieceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PieceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ piece: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PieceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PieceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load piece on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.piece).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
