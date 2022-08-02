package com.apsorders.orderstr.web.rest;

import static com.apsorders.orderstr.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.apsorders.orderstr.IntegrationTest;
import com.apsorders.orderstr.domain.Order;
import com.apsorders.orderstr.domain.Piece;
import com.apsorders.orderstr.domain.enumeration.StatusO;
import com.apsorders.orderstr.repository.OrderRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link OrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderResourceIT {

    private static final String DEFAULT_DRIVER = "AAAAAAAAAA";
    private static final String UPDATED_DRIVER = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_PICK_UP_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_PICK_UP_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_REPAIR_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_REPAIR_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DELIVERY_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DELIVERY_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_R_ORDER_NUM = "AAAAAAAAAA";
    private static final String UPDATED_R_ORDER_NUM = "BBBBBBBBBB";

    private static final String DEFAULT_INV_ORDER_NUM = "AAAAAAAAAA";
    private static final String UPDATED_INV_ORDER_NUM = "BBBBBBBBBB";

    private static final StatusO DEFAULT_STATUS_O = StatusO.PICKED_UP;
    private static final StatusO UPDATED_STATUS_O = StatusO.TBR;

    private static final String DEFAULT_NOTESO = "AAAAAAAAAA";
    private static final String UPDATED_NOTESO = "BBBBBBBBBB";

    private static final byte[] DEFAULT_SCAN = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_SCAN = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_SCAN_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_SCAN_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderMockMvc;

    private Order order;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createEntity(EntityManager em) {
        Order order = new Order()
            .driver(DEFAULT_DRIVER)
            .pickUpDate(DEFAULT_PICK_UP_DATE)
            .repairDate(DEFAULT_REPAIR_DATE)
            .deliveryDate(DEFAULT_DELIVERY_DATE)
            .rOrderNum(DEFAULT_R_ORDER_NUM)
            .invOrderNum(DEFAULT_INV_ORDER_NUM)
            .statusO(DEFAULT_STATUS_O)
            .noteso(DEFAULT_NOTESO)
            .scan(DEFAULT_SCAN)
            .scanContentType(DEFAULT_SCAN_CONTENT_TYPE);
        // Add required entity
        Piece piece;
        if (TestUtil.findAll(em, Piece.class).isEmpty()) {
            piece = PieceResourceIT.createEntity(em);
            em.persist(piece);
            em.flush();
        } else {
            piece = TestUtil.findAll(em, Piece.class).get(0);
        }
        order.getPiecesIns().add(piece);
        return order;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createUpdatedEntity(EntityManager em) {
        Order order = new Order()
            .driver(UPDATED_DRIVER)
            .pickUpDate(UPDATED_PICK_UP_DATE)
            .repairDate(UPDATED_REPAIR_DATE)
            .deliveryDate(UPDATED_DELIVERY_DATE)
            .rOrderNum(UPDATED_R_ORDER_NUM)
            .invOrderNum(UPDATED_INV_ORDER_NUM)
            .statusO(UPDATED_STATUS_O)
            .noteso(UPDATED_NOTESO)
            .scan(UPDATED_SCAN)
            .scanContentType(UPDATED_SCAN_CONTENT_TYPE);
        // Add required entity
        Piece piece;
        if (TestUtil.findAll(em, Piece.class).isEmpty()) {
            piece = PieceResourceIT.createUpdatedEntity(em);
            em.persist(piece);
            em.flush();
        } else {
            piece = TestUtil.findAll(em, Piece.class).get(0);
        }
        order.getPiecesIns().add(piece);
        return order;
    }

    @BeforeEach
    public void initTest() {
        order = createEntity(em);
    }

    @Test
    @Transactional
    void createOrder() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();
        // Create the Order
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getDriver()).isEqualTo(DEFAULT_DRIVER);
        assertThat(testOrder.getPickUpDate()).isEqualTo(DEFAULT_PICK_UP_DATE);
        assertThat(testOrder.getRepairDate()).isEqualTo(DEFAULT_REPAIR_DATE);
        assertThat(testOrder.getDeliveryDate()).isEqualTo(DEFAULT_DELIVERY_DATE);
        assertThat(testOrder.getrOrderNum()).isEqualTo(DEFAULT_R_ORDER_NUM);
        assertThat(testOrder.getInvOrderNum()).isEqualTo(DEFAULT_INV_ORDER_NUM);
        assertThat(testOrder.getStatusO()).isEqualTo(DEFAULT_STATUS_O);
        assertThat(testOrder.getNoteso()).isEqualTo(DEFAULT_NOTESO);
        assertThat(testOrder.getScan()).isEqualTo(DEFAULT_SCAN);
        assertThat(testOrder.getScanContentType()).isEqualTo(DEFAULT_SCAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createOrderWithExistingId() throws Exception {
        // Create the Order with an existing ID
        order.setId(1L);

        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDriverIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setDriver(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPickUpDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setPickUpDate(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkrOrderNumIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setrOrderNum(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].driver").value(hasItem(DEFAULT_DRIVER)))
            .andExpect(jsonPath("$.[*].pickUpDate").value(hasItem(sameInstant(DEFAULT_PICK_UP_DATE))))
            .andExpect(jsonPath("$.[*].repairDate").value(hasItem(sameInstant(DEFAULT_REPAIR_DATE))))
            .andExpect(jsonPath("$.[*].deliveryDate").value(hasItem(sameInstant(DEFAULT_DELIVERY_DATE))))
            .andExpect(jsonPath("$.[*].rOrderNum").value(hasItem(DEFAULT_R_ORDER_NUM)))
            .andExpect(jsonPath("$.[*].invOrderNum").value(hasItem(DEFAULT_INV_ORDER_NUM)))
            .andExpect(jsonPath("$.[*].statusO").value(hasItem(DEFAULT_STATUS_O.toString())))
            .andExpect(jsonPath("$.[*].noteso").value(hasItem(DEFAULT_NOTESO)))
            .andExpect(jsonPath("$.[*].scanContentType").value(hasItem(DEFAULT_SCAN_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].scan").value(hasItem(Base64Utils.encodeToString(DEFAULT_SCAN))));
    }

    @Test
    @Transactional
    void getOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId().intValue()))
            .andExpect(jsonPath("$.driver").value(DEFAULT_DRIVER))
            .andExpect(jsonPath("$.pickUpDate").value(sameInstant(DEFAULT_PICK_UP_DATE)))
            .andExpect(jsonPath("$.repairDate").value(sameInstant(DEFAULT_REPAIR_DATE)))
            .andExpect(jsonPath("$.deliveryDate").value(sameInstant(DEFAULT_DELIVERY_DATE)))
            .andExpect(jsonPath("$.rOrderNum").value(DEFAULT_R_ORDER_NUM))
            .andExpect(jsonPath("$.invOrderNum").value(DEFAULT_INV_ORDER_NUM))
            .andExpect(jsonPath("$.statusO").value(DEFAULT_STATUS_O.toString()))
            .andExpect(jsonPath("$.noteso").value(DEFAULT_NOTESO))
            .andExpect(jsonPath("$.scanContentType").value(DEFAULT_SCAN_CONTENT_TYPE))
            .andExpect(jsonPath("$.scan").value(Base64Utils.encodeToString(DEFAULT_SCAN)));
    }

    @Test
    @Transactional
    void getNonExistingOrder() throws Exception {
        // Get the order
        restOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order
        Order updatedOrder = orderRepository.findById(order.getId()).get();
        // Disconnect from session so that the updates on updatedOrder are not directly saved in db
        em.detach(updatedOrder);
        updatedOrder
            .driver(UPDATED_DRIVER)
            .pickUpDate(UPDATED_PICK_UP_DATE)
            .repairDate(UPDATED_REPAIR_DATE)
            .deliveryDate(UPDATED_DELIVERY_DATE)
            .rOrderNum(UPDATED_R_ORDER_NUM)
            .invOrderNum(UPDATED_INV_ORDER_NUM)
            .statusO(UPDATED_STATUS_O)
            .noteso(UPDATED_NOTESO)
            .scan(UPDATED_SCAN)
            .scanContentType(UPDATED_SCAN_CONTENT_TYPE);

        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getDriver()).isEqualTo(UPDATED_DRIVER);
        assertThat(testOrder.getPickUpDate()).isEqualTo(UPDATED_PICK_UP_DATE);
        assertThat(testOrder.getRepairDate()).isEqualTo(UPDATED_REPAIR_DATE);
        assertThat(testOrder.getDeliveryDate()).isEqualTo(UPDATED_DELIVERY_DATE);
        assertThat(testOrder.getrOrderNum()).isEqualTo(UPDATED_R_ORDER_NUM);
        assertThat(testOrder.getInvOrderNum()).isEqualTo(UPDATED_INV_ORDER_NUM);
        assertThat(testOrder.getStatusO()).isEqualTo(UPDATED_STATUS_O);
        assertThat(testOrder.getNoteso()).isEqualTo(UPDATED_NOTESO);
        assertThat(testOrder.getScan()).isEqualTo(UPDATED_SCAN);
        assertThat(testOrder.getScanContentType()).isEqualTo(UPDATED_SCAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, order.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder
            .driver(UPDATED_DRIVER)
            .repairDate(UPDATED_REPAIR_DATE)
            .deliveryDate(UPDATED_DELIVERY_DATE)
            .rOrderNum(UPDATED_R_ORDER_NUM)
            .invOrderNum(UPDATED_INV_ORDER_NUM)
            .statusO(UPDATED_STATUS_O)
            .noteso(UPDATED_NOTESO)
            .scan(UPDATED_SCAN)
            .scanContentType(UPDATED_SCAN_CONTENT_TYPE);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getDriver()).isEqualTo(UPDATED_DRIVER);
        assertThat(testOrder.getPickUpDate()).isEqualTo(DEFAULT_PICK_UP_DATE);
        assertThat(testOrder.getRepairDate()).isEqualTo(UPDATED_REPAIR_DATE);
        assertThat(testOrder.getDeliveryDate()).isEqualTo(UPDATED_DELIVERY_DATE);
        assertThat(testOrder.getrOrderNum()).isEqualTo(UPDATED_R_ORDER_NUM);
        assertThat(testOrder.getInvOrderNum()).isEqualTo(UPDATED_INV_ORDER_NUM);
        assertThat(testOrder.getStatusO()).isEqualTo(UPDATED_STATUS_O);
        assertThat(testOrder.getNoteso()).isEqualTo(UPDATED_NOTESO);
        assertThat(testOrder.getScan()).isEqualTo(UPDATED_SCAN);
        assertThat(testOrder.getScanContentType()).isEqualTo(UPDATED_SCAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder
            .driver(UPDATED_DRIVER)
            .pickUpDate(UPDATED_PICK_UP_DATE)
            .repairDate(UPDATED_REPAIR_DATE)
            .deliveryDate(UPDATED_DELIVERY_DATE)
            .rOrderNum(UPDATED_R_ORDER_NUM)
            .invOrderNum(UPDATED_INV_ORDER_NUM)
            .statusO(UPDATED_STATUS_O)
            .noteso(UPDATED_NOTESO)
            .scan(UPDATED_SCAN)
            .scanContentType(UPDATED_SCAN_CONTENT_TYPE);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getDriver()).isEqualTo(UPDATED_DRIVER);
        assertThat(testOrder.getPickUpDate()).isEqualTo(UPDATED_PICK_UP_DATE);
        assertThat(testOrder.getRepairDate()).isEqualTo(UPDATED_REPAIR_DATE);
        assertThat(testOrder.getDeliveryDate()).isEqualTo(UPDATED_DELIVERY_DATE);
        assertThat(testOrder.getrOrderNum()).isEqualTo(UPDATED_R_ORDER_NUM);
        assertThat(testOrder.getInvOrderNum()).isEqualTo(UPDATED_INV_ORDER_NUM);
        assertThat(testOrder.getStatusO()).isEqualTo(UPDATED_STATUS_O);
        assertThat(testOrder.getNoteso()).isEqualTo(UPDATED_NOTESO);
        assertThat(testOrder.getScan()).isEqualTo(UPDATED_SCAN);
        assertThat(testOrder.getScanContentType()).isEqualTo(UPDATED_SCAN_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, order.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(order))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Delete the order
        restOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, order.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
