package service;

import com.sporeon.framework.excecao.ValidacaoException;

import dominio.Produto;

/**
 * Interface para os serviços de Produto.
 * @author Senio Caires
 */
public interface ProdutoServiceLocal {

  /**
   * Salva.
   * @author Senio Caires
   * @param produto - {@link Produto}
   * @throws ValidacaoException - {@link ValidacaoException}
   */
  void salvar(Produto produto) throws ValidacaoException;

  /**
   * Retorna o produto do link passado por parâmetro.
   * @author Senio Caires
   * @param link - {@link String}
   * @return {@link String}
   */
  Produto buscarPorLink(String link);
}
