package com.apsorders.orderstr.repository;

import com.apsorders.orderstr.domain.Piece;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PieceRepositoryWithBagRelationships {
    Optional<Piece> fetchBagRelationships(Optional<Piece> piece);

    List<Piece> fetchBagRelationships(List<Piece> pieces);

    Page<Piece> fetchBagRelationships(Page<Piece> pieces);
}
