package com.apsorders.orderstr.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.apsorders.orderstr.IntegrationTest;
import com.apsorders.orderstr.domain.Order;
import com.apsorders.orderstr.domain.Piece;
import com.apsorders.orderstr.repository.PieceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PieceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PieceResourceIT {

    private static final String DEFAULT_SERIAL = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_MANU = "AAAAAAAAAA";
    private static final String UPDATED_MANU = "BBBBBBBBBB";

    private static final String DEFAULT_NOTESP = "AAAAAAAAAA";
    private static final String UPDATED_NOTESP = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pieces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PieceRepository pieceRepository;

    @Mock
    private PieceRepository pieceRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPieceMockMvc;

    private Piece piece;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Piece createEntity(EntityManager em) {
        Piece piece = new Piece().serial(DEFAULT_SERIAL).model(DEFAULT_MODEL).desc(DEFAULT_DESC).manu(DEFAULT_MANU).notesp(DEFAULT_NOTESP);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        piece.getOrdersBelongeds().add(order);
        return piece;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Piece createUpdatedEntity(EntityManager em) {
        Piece piece = new Piece().serial(UPDATED_SERIAL).model(UPDATED_MODEL).desc(UPDATED_DESC).manu(UPDATED_MANU).notesp(UPDATED_NOTESP);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createUpdatedEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        piece.getOrdersBelongeds().add(order);
        return piece;
    }

    @BeforeEach
    public void initTest() {
        piece = createEntity(em);
    }

    @Test
    @Transactional
    void createPiece() throws Exception {
        int databaseSizeBeforeCreate = pieceRepository.findAll().size();
        // Create the Piece
        restPieceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(piece)))
            .andExpect(status().isCreated());

        // Validate the Piece in the database
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeCreate + 1);
        Piece testPiece = pieceList.get(pieceList.size() - 1);
        assertThat(testPiece.getSerial()).isEqualTo(DEFAULT_SERIAL);
        assertThat(testPiece.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testPiece.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testPiece.getManu()).isEqualTo(DEFAULT_MANU);
        assertThat(testPiece.getNotesp()).isEqualTo(DEFAULT_NOTESP);
    }

    @Test
    @Transactional
    void createPieceWithExistingId() throws Exception {
        // Create the Piece with an existing ID
        piece.setId(1L);

        int databaseSizeBeforeCreate = pieceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPieceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(piece)))
            .andExpect(status().isBadRequest());

        // Validate the Piece in the database
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSerialIsRequired() throws Exception {
        int databaseSizeBeforeTest = pieceRepository.findAll().size();
        // set the field null
        piece.setSerial(null);

        // Create the Piece, which fails.

        restPieceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(piece)))
            .andExpect(status().isBadRequest());

        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModelIsRequired() throws Exception {
        int databaseSizeBeforeTest = pieceRepository.findAll().size();
        // set the field null
        piece.setModel(null);

        // Create the Piece, which fails.

        restPieceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(piece)))
            .andExpect(status().isBadRequest());

        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkManuIsRequired() throws Exception {
        int databaseSizeBeforeTest = pieceRepository.findAll().size();
        // set the field null
        piece.setManu(null);

        // Create the Piece, which fails.

        restPieceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(piece)))
            .andExpect(status().isBadRequest());

        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPieces() throws Exception {
        // Initialize the database
        pieceRepository.saveAndFlush(piece);

        // Get all the pieceList
        restPieceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(piece.getId().intValue())))
            .andExpect(jsonPath("$.[*].serial").value(hasItem(DEFAULT_SERIAL)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC)))
            .andExpect(jsonPath("$.[*].manu").value(hasItem(DEFAULT_MANU)))
            .andExpect(jsonPath("$.[*].notesp").value(hasItem(DEFAULT_NOTESP)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPiecesWithEagerRelationshipsIsEnabled() throws Exception {
        when(pieceRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPieceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(pieceRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPiecesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(pieceRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPieceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(pieceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPiece() throws Exception {
        // Initialize the database
        pieceRepository.saveAndFlush(piece);

        // Get the piece
        restPieceMockMvc
            .perform(get(ENTITY_API_URL_ID, piece.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(piece.getId().intValue()))
            .andExpect(jsonPath("$.serial").value(DEFAULT_SERIAL))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC))
            .andExpect(jsonPath("$.manu").value(DEFAULT_MANU))
            .andExpect(jsonPath("$.notesp").value(DEFAULT_NOTESP));
    }

    @Test
    @Transactional
    void getNonExistingPiece() throws Exception {
        // Get the piece
        restPieceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPiece() throws Exception {
        // Initialize the database
        pieceRepository.saveAndFlush(piece);

        int databaseSizeBeforeUpdate = pieceRepository.findAll().size();

        // Update the piece
        Piece updatedPiece = pieceRepository.findById(piece.getId()).get();
        // Disconnect from session so that the updates on updatedPiece are not directly saved in db
        em.detach(updatedPiece);
        updatedPiece.serial(UPDATED_SERIAL).model(UPDATED_MODEL).desc(UPDATED_DESC).manu(UPDATED_MANU).notesp(UPDATED_NOTESP);

        restPieceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPiece.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPiece))
            )
            .andExpect(status().isOk());

        // Validate the Piece in the database
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeUpdate);
        Piece testPiece = pieceList.get(pieceList.size() - 1);
        assertThat(testPiece.getSerial()).isEqualTo(UPDATED_SERIAL);
        assertThat(testPiece.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testPiece.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testPiece.getManu()).isEqualTo(UPDATED_MANU);
        assertThat(testPiece.getNotesp()).isEqualTo(UPDATED_NOTESP);
    }

    @Test
    @Transactional
    void putNonExistingPiece() throws Exception {
        int databaseSizeBeforeUpdate = pieceRepository.findAll().size();
        piece.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPieceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, piece.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(piece))
            )
            .andExpect(status().isBadRequest());

        // Validate the Piece in the database
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPiece() throws Exception {
        int databaseSizeBeforeUpdate = pieceRepository.findAll().size();
        piece.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPieceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(piece))
            )
            .andExpect(status().isBadRequest());

        // Validate the Piece in the database
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPiece() throws Exception {
        int databaseSizeBeforeUpdate = pieceRepository.findAll().size();
        piece.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPieceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(piece)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Piece in the database
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePieceWithPatch() throws Exception {
        // Initialize the database
        pieceRepository.saveAndFlush(piece);

        int databaseSizeBeforeUpdate = pieceRepository.findAll().size();

        // Update the piece using partial update
        Piece partialUpdatedPiece = new Piece();
        partialUpdatedPiece.setId(piece.getId());

        partialUpdatedPiece.model(UPDATED_MODEL).desc(UPDATED_DESC).notesp(UPDATED_NOTESP);

        restPieceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPiece.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPiece))
            )
            .andExpect(status().isOk());

        // Validate the Piece in the database
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeUpdate);
        Piece testPiece = pieceList.get(pieceList.size() - 1);
        assertThat(testPiece.getSerial()).isEqualTo(DEFAULT_SERIAL);
        assertThat(testPiece.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testPiece.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testPiece.getManu()).isEqualTo(DEFAULT_MANU);
        assertThat(testPiece.getNotesp()).isEqualTo(UPDATED_NOTESP);
    }

    @Test
    @Transactional
    void fullUpdatePieceWithPatch() throws Exception {
        // Initialize the database
        pieceRepository.saveAndFlush(piece);

        int databaseSizeBeforeUpdate = pieceRepository.findAll().size();

        // Update the piece using partial update
        Piece partialUpdatedPiece = new Piece();
        partialUpdatedPiece.setId(piece.getId());

        partialUpdatedPiece.serial(UPDATED_SERIAL).model(UPDATED_MODEL).desc(UPDATED_DESC).manu(UPDATED_MANU).notesp(UPDATED_NOTESP);

        restPieceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPiece.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPiece))
            )
            .andExpect(status().isOk());

        // Validate the Piece in the database
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeUpdate);
        Piece testPiece = pieceList.get(pieceList.size() - 1);
        assertThat(testPiece.getSerial()).isEqualTo(UPDATED_SERIAL);
        assertThat(testPiece.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testPiece.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testPiece.getManu()).isEqualTo(UPDATED_MANU);
        assertThat(testPiece.getNotesp()).isEqualTo(UPDATED_NOTESP);
    }

    @Test
    @Transactional
    void patchNonExistingPiece() throws Exception {
        int databaseSizeBeforeUpdate = pieceRepository.findAll().size();
        piece.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPieceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, piece.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(piece))
            )
            .andExpect(status().isBadRequest());

        // Validate the Piece in the database
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPiece() throws Exception {
        int databaseSizeBeforeUpdate = pieceRepository.findAll().size();
        piece.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPieceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(piece))
            )
            .andExpect(status().isBadRequest());

        // Validate the Piece in the database
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPiece() throws Exception {
        int databaseSizeBeforeUpdate = pieceRepository.findAll().size();
        piece.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPieceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(piece)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Piece in the database
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePiece() throws Exception {
        // Initialize the database
        pieceRepository.saveAndFlush(piece);

        int databaseSizeBeforeDelete = pieceRepository.findAll().size();

        // Delete the piece
        restPieceMockMvc
            .perform(delete(ENTITY_API_URL_ID, piece.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Piece> pieceList = pieceRepository.findAll();
        assertThat(pieceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
