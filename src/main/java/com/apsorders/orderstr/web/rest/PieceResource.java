package com.apsorders.orderstr.web.rest;

import com.apsorders.orderstr.domain.Piece;
import com.apsorders.orderstr.repository.PieceRepository;
import com.apsorders.orderstr.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.apsorders.orderstr.domain.Piece}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PieceResource {

    private final Logger log = LoggerFactory.getLogger(PieceResource.class);

    private static final String ENTITY_NAME = "piece";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PieceRepository pieceRepository;

    public PieceResource(PieceRepository pieceRepository) {
        this.pieceRepository = pieceRepository;
    }

    /**
     * {@code POST  /pieces} : Create a new piece.
     *
     * @param piece the piece to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new piece, or with status {@code 400 (Bad Request)} if the piece has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pieces")
    public ResponseEntity<Piece> createPiece(@Valid @RequestBody Piece piece) throws URISyntaxException {
        log.debug("REST request to save Piece : {}", piece);
        if (piece.getId() != null) {
            throw new BadRequestAlertException("A new piece cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Piece result = pieceRepository.save(piece);
        return ResponseEntity
            .created(new URI("/api/pieces/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /pieces/:id} : Updates an existing piece.
     *
     * @param id the id of the piece to save.
     * @param piece the piece to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated piece,
     * or with status {@code 400 (Bad Request)} if the piece is not valid,
     * or with status {@code 500 (Internal Server Error)} if the piece couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pieces/{id}")
    public ResponseEntity<Piece> updatePiece(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Piece piece)
        throws URISyntaxException {
        log.debug("REST request to update Piece : {}, {}", id, piece);
        if (piece.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, piece.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pieceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Piece result = pieceRepository.save(piece);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, piece.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /pieces/:id} : Partial updates given fields of an existing piece, field will ignore if it is null
     *
     * @param id the id of the piece to save.
     * @param piece the piece to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated piece,
     * or with status {@code 400 (Bad Request)} if the piece is not valid,
     * or with status {@code 404 (Not Found)} if the piece is not found,
     * or with status {@code 500 (Internal Server Error)} if the piece couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pieces/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Piece> partialUpdatePiece(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Piece piece
    ) throws URISyntaxException {
        log.debug("REST request to partial update Piece partially : {}, {}", id, piece);
        if (piece.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, piece.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pieceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Piece> result = pieceRepository
            .findById(piece.getId())
            .map(existingPiece -> {
                if (piece.getSerial() != null) {
                    existingPiece.setSerial(piece.getSerial());
                }
                if (piece.getModel() != null) {
                    existingPiece.setModel(piece.getModel());
                }
                if (piece.getDesc() != null) {
                    existingPiece.setDesc(piece.getDesc());
                }
                if (piece.getManu() != null) {
                    existingPiece.setManu(piece.getManu());
                }
                if (piece.getNotesp() != null) {
                    existingPiece.setNotesp(piece.getNotesp());
                }

                return existingPiece;
            })
            .map(pieceRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, piece.getId().toString())
        );
    }

    /**
     * {@code GET  /pieces} : get all the pieces.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pieces in body.
     */
    @GetMapping("/pieces")
    public List<Piece> getAllPieces(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Pieces");
        if (eagerload) {
            return pieceRepository.findAllWithEagerRelationships();
        } else {
            return pieceRepository.findAll();
        }
    }

    /**
     * {@code GET  /pieces/:id} : get the "id" piece.
     *
     * @param id the id of the piece to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the piece, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pieces/{id}")
    public ResponseEntity<Piece> getPiece(@PathVariable Long id) {
        log.debug("REST request to get Piece : {}", id);
        Optional<Piece> piece = pieceRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(piece);
    }

    /**
     * {@code DELETE  /pieces/:id} : delete the "id" piece.
     *
     * @param id the id of the piece to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pieces/{id}")
    public ResponseEntity<Void> deletePiece(@PathVariable Long id) {
        log.debug("REST request to delete Piece : {}", id);
        pieceRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
