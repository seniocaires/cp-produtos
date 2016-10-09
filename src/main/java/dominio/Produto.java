package dominio;

import com.sporeon.baseutil.ConversaoUtil;

/**
 * Classe de domínio.<br/>
 * Produto.
 * @author Senio Caires
 */
public class Produto {

  /**
   * Nome.
   * @author Senio Caires
   */
  private String nome;

  /**
   * Código de barras.
   * @author Senio Caires
   */
  private String codigoBarras;

  /**
   * Marca.
   * @author Senio Caires
   */
  private Marca marca;

  /**
   * Retorna o nome.
   * @author Senio Caires
   * @return {@link String}
   */
  public String getNome() {
    return ConversaoUtil.nuloParaVazio(nome);
  }

  /**
   * Altera o nome.
   * @author Senio Caires
   * @param nomeParametro - {@link String}
   */
  public void setNome(String nomeParametro) {
    this.nome = nomeParametro;
  }

  /**
   * Retorna o código de barras.
   * @author Senio Caires
   * @return {@link String}
   */
  public String getCodigoBarras() {
    return ConversaoUtil.nuloParaVazio(codigoBarras);
  }

  /**
   * Altera o código de barras.
   * @author Senio Caires
   * @param codigoBarrasParametro - {@link String}
   */
  public void setCodigoBarras(String codigoBarrasParametro) {
    this.codigoBarras = codigoBarrasParametro;
  }

  /**
   * Retorna a marca.
   * @author Senio Caires
   * @return {@link Marca}
   */
  public Marca getMarca() {
    return marca;
  }

  /**
   * Altera a marca.
   * @author Senio Caires
   * @param marcaParametro - {@link Marca}
   */
  public void setMarca(Marca marcaParametro) {
    this.marca = marcaParametro;
  }
}
