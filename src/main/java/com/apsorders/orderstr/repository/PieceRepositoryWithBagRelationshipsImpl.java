package com.apsorders.orderstr.repository;

import com.apsorders.orderstr.domain.Piece;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class PieceRepositoryWithBagRelationshipsImpl implements PieceRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Piece> fetchBagRelationships(Optional<Piece> piece) {
        return piece.map(this::fetchOrdersBelongeds);
    }

    @Override
    public Page<Piece> fetchBagRelationships(Page<Piece> pieces) {
        return new PageImpl<>(fetchBagRelationships(pieces.getContent()), pieces.getPageable(), pieces.getTotalElements());
    }

    @Override
    public List<Piece> fetchBagRelationships(List<Piece> pieces) {
        return Optional.of(pieces).map(this::fetchOrdersBelongeds).orElse(Collections.emptyList());
    }

    Piece fetchOrdersBelongeds(Piece result) {
        return entityManager
            .createQuery("select piece from Piece piece left join fetch piece.ordersBelongeds where piece is :piece", Piece.class)
            .setParameter("piece", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Piece> fetchOrdersBelongeds(List<Piece> pieces) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, pieces.size()).forEach(index -> order.put(pieces.get(index).getId(), index));
        List<Piece> result = entityManager
            .createQuery("select distinct piece from Piece piece left join fetch piece.ordersBelongeds where piece in :pieces", Piece.class)
            .setParameter("pieces", pieces)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
