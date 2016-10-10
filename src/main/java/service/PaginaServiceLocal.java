package service;

import java.util.List;

import com.sporeon.framework.excecao.ValidacaoException;

import dominio.Pagina;
import enumeracao.TipoPagina;

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

  /**
   * Retorna as páginas ativas.
   * @author Senio Caires
   * @param tipo {@link TipoPagina}
   * @return {@link List}<{@linkPagina}>
   */
  List<Pagina> buscarAtivas(TipoPagina tipo);
}
