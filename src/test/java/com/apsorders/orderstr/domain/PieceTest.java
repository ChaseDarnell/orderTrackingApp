package com.apsorders.orderstr.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.apsorders.orderstr.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PieceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Piece.class);
        Piece piece1 = new Piece();
        piece1.setId(1L);
        Piece piece2 = new Piece();
        piece2.setId(piece1.getId());
        assertThat(piece1).isEqualTo(piece2);
        piece2.setId(2L);
        assertThat(piece1).isNotEqualTo(piece2);
        piece1.setId(null);
        assertThat(piece1).isNotEqualTo(piece2);
    }
}
