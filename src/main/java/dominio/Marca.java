package dominio;

import com.sporeon.baseutil.ConversaoUtil;

/**
 * Classe de domínio.<br/>
 * Marca dos produtos.
 * @author Senio Caires
 */
public class Marca {

  /**
   * Nome.
   * @author Senio Caires
   */
  private String nome;

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
   * @param nomeParametro - {@link String}
   */
  public void setNome(String nomeParametro) {
    this.nome = nomeParametro;
  }
}
