package service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

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

}
