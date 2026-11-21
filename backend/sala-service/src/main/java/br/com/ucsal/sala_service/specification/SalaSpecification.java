package br.com.ucsal.sala_service.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.com.ucsal.sala_service.enums.EstadoSala;
import br.com.ucsal.sala_service.enums.TipoSala;
import br.com.ucsal.sala_service.model.Sala;
import jakarta.persistence.criteria.Predicate;

public final class SalaSpecification {

    private SalaSpecification() {
    }

    public static Specification<Sala> filtrar(TipoSala tipo, Integer capacidadeMinima, EstadoSala estado) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipoSala"), tipo));
            }
            if (capacidadeMinima != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("capacidade"), capacidadeMinima));
            }
            if (estado != null) {
                predicates.add(cb.equal(root.get("estadoSala"), estado));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
