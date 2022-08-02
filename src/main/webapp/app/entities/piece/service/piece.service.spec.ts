import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPiece } from '../piece.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../piece.test-samples';

import { PieceService } from './piece.service';

const requireRestSample: IPiece = {
  ...sampleWithRequiredData,
};

describe('Piece Service', () => {
  let service: PieceService;
  let httpMock: HttpTestingController;
  let expectedResult: IPiece | IPiece[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PieceService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Piece', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const piece = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(piece).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Piece', () => {
      const piece = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(piece).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Piece', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Piece', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Piece', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPieceToCollectionIfMissing', () => {
      it('should add a Piece to an empty array', () => {
        const piece: IPiece = sampleWithRequiredData;
        expectedResult = service.addPieceToCollectionIfMissing([], piece);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(piece);
      });

      it('should not add a Piece to an array that contains it', () => {
        const piece: IPiece = sampleWithRequiredData;
        const pieceCollection: IPiece[] = [
          {
            ...piece,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPieceToCollectionIfMissing(pieceCollection, piece);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Piece to an array that doesn't contain it", () => {
        const piece: IPiece = sampleWithRequiredData;
        const pieceCollection: IPiece[] = [sampleWithPartialData];
        expectedResult = service.addPieceToCollectionIfMissing(pieceCollection, piece);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(piece);
      });

      it('should add only unique Piece to an array', () => {
        const pieceArray: IPiece[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const pieceCollection: IPiece[] = [sampleWithRequiredData];
        expectedResult = service.addPieceToCollectionIfMissing(pieceCollection, ...pieceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const piece: IPiece = sampleWithRequiredData;
        const piece2: IPiece = sampleWithPartialData;
        expectedResult = service.addPieceToCollectionIfMissing([], piece, piece2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(piece);
        expect(expectedResult).toContain(piece2);
      });

      it('should accept null and undefined values', () => {
        const piece: IPiece = sampleWithRequiredData;
        expectedResult = service.addPieceToCollectionIfMissing([], null, piece, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(piece);
      });

      it('should return initial array if no Piece is added', () => {
        const pieceCollection: IPiece[] = [sampleWithRequiredData];
        expectedResult = service.addPieceToCollectionIfMissing(pieceCollection, undefined, null);
        expect(expectedResult).toEqual(pieceCollection);
      });
    });

    describe('comparePiece', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePiece(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePiece(entity1, entity2);
        const compareResult2 = service.comparePiece(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePiece(entity1, entity2);
        const compareResult2 = service.comparePiece(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePiece(entity1, entity2);
        const compareResult2 = service.comparePiece(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
