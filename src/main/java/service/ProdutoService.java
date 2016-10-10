package service;

import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import com.sporeon.baseutil.ConversaoUtil;
import com.sporeon.framework.excecao.ValidacaoException;

import dominio.Produto;

/**
 * Classe com os serviços de produto.
 * @author Senio Caires
 */
@Stateful(name = "ProdutoService")
@Local(value = ProdutoServiceLocal.class)
public class ProdutoService implements ProdutoServiceLocal {

  /**
   * Entity Manager.
   * @author Senio Caires
   */
  @PersistenceContext(type = PersistenceContextType.EXTENDED)
  private EntityManager entityManager;

  /**
   * Salva.
   * @author Senio Caires
   * @param produto - {@link Produto}
   * @throws ValidacaoException - {@link ValidacaoException}
   */
  public void salvar(Produto produto) throws ValidacaoException {

    if (produto.getId() == null) {
      entityManager.persist(produto);
    } else {
      entityManager.merge(produto);
    }
  }

  /**
   * Retorna o produto do link passado por parâmetro.
   * @author Senio Caires
   * @param link - {@link String}
   * @return {@link String}
   */
  public Produto buscarPorLink(String link) {

    Produto retorno;
    TypedQuery<Produto> query = null;
    EntityType<Produto> metamodelProduto;
    CriteriaBuilder criteriaBuilder;
    CriteriaQuery<Produto> criteriaQuery;
    Root<Produto> root;
    Predicate condicaoLink;

    try {

      metamodelProduto = entityManager.getMetamodel().entity(Produto.class);
      criteriaBuilder = entityManager.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(Produto.class);
      root = criteriaQuery.from(Produto.class);
      condicaoLink = criteriaBuilder.equal(criteriaBuilder.lower(root.get(metamodelProduto.getDeclaredSingularAttribute("link", String.class))), ConversaoUtil.nuloParaVazio(link).toLowerCase());
      criteriaQuery.select(root).where(condicaoLink);
      query = entityManager.createQuery(criteriaQuery);

      retorno = (Produto) query.getSingleResult();

    } catch (NoResultException nre) {
      retorno = null;
    } catch (Exception e) {
      e.printStackTrace();
      retorno = null;
    }

    return retorno;
  }
}
