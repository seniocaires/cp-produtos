package service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import com.sporeon.baseutil.ConversaoUtil;
import com.sporeon.framework.excecao.ValidacaoException;

import dominio.Pagina;
import enumeracao.TipoPagina;

/**
 * Classe com os serviços de página.
 * @author Senio Caires
 */
@Stateful(name = "PaginaService")
@Local(value = PaginaServiceLocal.class)
public class PaginaService implements PaginaServiceLocal {

  /**
   * Entity Manager.
   * @author Senio Caires
   */
  @PersistenceContext(type = PersistenceContextType.EXTENDED)
  private EntityManager entityManager;

  /**
   * Salva.
   * @author Senio Caires
   * @param pagina - {@link Pagina}
   * @throws ValidacaoException - {@link ValidacaoException}
   */
  public void salvar(Pagina pagina) throws ValidacaoException {

    if (pagina.getId() == null) {
      entityManager.persist(pagina);
    } else {
      entityManager.merge(pagina);
    }
  }

  /**
   * Remove.
   * @author Senio Caires
   * @param pagina - {@link Pagina}
   * @throws ValidacaoException - {@link ValidacaoException}
   */
  public void remover(Pagina pagina) throws ValidacaoException {

    Pagina paginaParaSerRemovida = entityManager.getReference(Pagina.class, pagina.getId());
    entityManager.remove(paginaParaSerRemovida);
  }

  /**
   * Retorna a última página de listagem de marca acessada ou uma nova página caso não exista.
   * @author Senio Caires
   * @return {@link Pagina}
   */
  public Pagina buscarPaginaListagemMarca() {

    Pagina retorno;

    try {

      Query query = entityManager.createQuery("SELECT pagina FROM Pagina pagina WHERE pagina.tipo = :tipo");
      query.setParameter("tipo", TipoPagina.LISTA_MARCA);

      retorno = (Pagina) query.getSingleResult();

    } catch (NoResultException nre) {
      retorno = new Pagina();
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      retorno = new Pagina();
    }

    return retorno;
  }

  /**
   * Retorna a página do link passado por parâmetro.
   * @author Senio Caires
   * @param link - {@link String}
   * @return {@link String}
   */
  public Pagina buscarPorLink(String link) {

    Pagina retorno;
    TypedQuery<Pagina> query = null;
    EntityType<Pagina> metamodelPagina;
    CriteriaBuilder criteriaBuilder;
    CriteriaQuery<Pagina> criteriaQuery;
    Root<Pagina> root;
    Predicate condicaoLink;

    try {

      metamodelPagina = entityManager.getMetamodel().entity(Pagina.class);
      criteriaBuilder = entityManager.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(Pagina.class);
      root = criteriaQuery.from(Pagina.class);
      condicaoLink = criteriaBuilder.equal(criteriaBuilder.lower(root.get(metamodelPagina.getDeclaredSingularAttribute("link", String.class))), ConversaoUtil.nuloParaVazio(link).toLowerCase());
      criteriaQuery.select(root).where(condicaoLink);
      query = entityManager.createQuery(criteriaQuery);

      retorno = (Pagina) query.getSingleResult();

    } catch (NoResultException nre) {
      retorno = null;
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      retorno = null;
    }

    return retorno;
  }

  /**
   * Retorna as páginas ativas.
   * @author Senio Caires
   * @param tipo {@link TipoPagina}
   * @return {@link List}<{@linkPagina}>
   */
  public List<Pagina> buscarAtivas(TipoPagina tipo) {

    TypedQuery<Pagina> query = null;

    try {

      CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Pagina> criteriaQuery = criteriaBuilder.createQuery(Pagina.class);
      Root<Pagina> root = criteriaQuery.from(Pagina.class);
      Predicate condicaoTipo = criteriaBuilder.equal(root.get("tipo"), tipo);
      Predicate condicaoAtiva = criteriaBuilder.equal(root.get("ativa"), Boolean.TRUE);
      criteriaQuery.select(root).where(condicaoTipo, condicaoAtiva);
      query = entityManager.createQuery(criteriaQuery);

      return query.getResultList();
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      return new ArrayList<Pagina>();
    }
  }
}
