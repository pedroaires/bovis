package bovisApp.firstApp.specification;

import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Pesagem;
import bovisApp.firstApp.model.Transacao;
import bovisApp.firstApp.model.TransacaoBoi;
import bovisApp.firstApp.model.enumeration.EstadoBoi;
import bovisApp.firstApp.model.enumeration.TransacaoOperacao;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Join;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BoiSpecification {
    public static Specification<Boi> hasRaca(Long racaId){
        return (root, query, criteriaBuilder) -> {
            if (racaId == null){
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            else{
                return criteriaBuilder.equal(root.get("raca").get("id"), racaId);
            }
        };
    }

    public static Specification<Boi> hasNumero(Integer numeroBoi){
        return (root, query, criteriaBuilder) -> {
            if (numeroBoi == null){
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            else{
                return criteriaBuilder.equal(root.get("numero"), numeroBoi);
            }
        };
    }

    public static Specification<Boi> hasEstado(EstadoBoi estadoBoi){
        return (root, query, criteriaBuilder) -> {
            if (estadoBoi == null){
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            else{
                return criteriaBuilder.equal(root.get("estadoBoi"), estadoBoi);
            }
        };
    }

    public static Specification<Boi> hasPesagemBetween(BigDecimal pesoMin, BigDecimal pesoMax){
        return (root, query, criteriaBuilder) -> {
            if (pesoMin == null && pesoMax == null){
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            query.distinct(true);

            Join<Boi, Pesagem> pesagemJoin = root.join("pesagens", JoinType.INNER);
            Predicate predicate = criteriaBuilder.isTrue(criteriaBuilder.literal((true)));
            if(pesoMin != null){
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(pesagemJoin.get("peso"), pesoMin)
                );
            }
            if (pesoMax != null){
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(pesagemJoin.get("peso"), pesoMax)
                );
            }
            return predicate;
        };
    }
    public static Specification<Boi> hasPesagemDateBetween(LocalDate dataInicio, LocalDate dataFim){
        return (root, query, criteriaBuilder) -> {
            if (dataInicio == null && dataFim == null){
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            query.distinct(true);
            Join<Boi, Pesagem> pesagemJoin = root.join("pesagens", JoinType.INNER);
            Predicate predicate = criteriaBuilder.isTrue(criteriaBuilder.literal((true)));
            if(dataInicio != null){
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(pesagemJoin.get("data"), dataInicio)
                );
            }
            if (dataFim != null){
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(pesagemJoin.get("data"), dataFim)
                );
            }
            return predicate;
        };
    }

    public static Specification<Boi> hasTransacaoDataBetween(LocalDate dataInicio, LocalDate dataFim, TransacaoOperacao operacao){
        return (root, query, criteriaBuilder) -> {
            if (dataInicio == null && dataFim == null){
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            query.distinct(true);
            Join<Boi, TransacaoBoi> transacaoBoiJoin = root.join("transacaoBois", JoinType.INNER);
            Join<TransacaoBoi, Transacao> transacaoJoin = transacaoBoiJoin.join("transacao", JoinType.INNER);
            Predicate predicate = criteriaBuilder.isTrue(criteriaBuilder.literal((true)));
            if(dataInicio != null){
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(transacaoJoin.get("data"), dataInicio)
                );
            }
            if (dataFim != null){
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(transacaoJoin.get("data"), dataFim)
                );
            }
            if (operacao != null){
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(transacaoJoin.get("operacao"), operacao)
                );
            }
            return predicate;
        };
    }

    public static Specification<Boi> fetchPesagens(){
        return (root, query, criteriaBuilder) -> {
            if (query.getResultType() != Long.class) {
                root.fetch("pesagens", JoinType.LEFT);
            }
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        };
    }

}
