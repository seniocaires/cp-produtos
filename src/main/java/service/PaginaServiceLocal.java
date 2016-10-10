package service;

import com.sporeon.framework.excecao.ValidacaoException;

import dominio.Pagina;

/**
 * Interface para os serviços de Pagina.
 * @author Senio Caires
 */
public interface PaginaServiceLocal {

  /**
   * Salva.
   * @author Senio Caires
   * @param pagina - {@link Pagina}
   * @throws ValidacaoException - {@link ValidacaoException}
   */
  void salvar(Pagina pagina) throws ValidacaoException;

  /**
   * Remove.
   * @author Senio Caires
   * @param pagina - {@link Pagina}
   * @throws ValidacaoException - {@link ValidacaoException}
   */
  void remover(Pagina pagina) throws ValidacaoException;

  /**
   * Retorna a última página de listagem de marca acessada ou uma nova página caso não exista.
   * @author Senio Caires
   * @return {@link Pagina}
   */
  Pagina buscarPaginaListagemMarca();

  /**
   * Retorna a página do link passado por parâmetro.
   * @author Senio Caires
   * @param link - {@link String}
   * @return {@link String}
   */
  Pagina buscarPorLink(String link);
}
