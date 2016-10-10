package dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sporeon.baseutil.ConversaoUtil;

/**
 * Classe de domínio.<br/>
 * Produto.
 * @author Senio Caires
 */
@Entity
@Table(name = "produto")
public class Produto {

  /**
   * Identificador.
   * @author Senio Caires
   */
  private Long id;

  /**
   * Nome.
   * @author Senio Caires
   */
  private String nome;

  /**
   * Link.
   * @author Senio Caires
   */
  private String link;

  /**
   * Código de barras.
   * @author Senio Caires
   */
  private String codigoBarras;

  /**
   * Data da última atualização.
   * @author Senio Caires
   */
  private Date dataAtualizacao;

  /**
   * Status.
   * @author Senio Caires
   */
  private Boolean ativo;

  /**
   * Contrutor.
   * @author Senio Caires
   */
  public Produto() {
    // default
  }

  /**
   * Contrutor.
   * @author Senio Caires
   * @param linkParametro {@link String}
   */
  public Produto(String linkParametro) {
    this.link = linkParametro;
  }

  /**
   * Retorna o id.
   * @author Senio Caires
   * @return {@link Long}
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long getId() {
    return id;
  }

  /**
   * Altera o id.
   * @author Senio Caires
   * @param idParametro - {@link Long}
   */
  public void setId(Long idParametro) {
    this.id = idParametro;
  }

  /**
   * Retorna o link.
   * @author Senio Caires
   * @return {@link String}
   */
  @Column(length = 800, unique = true)
  public String getLink() {
    return ConversaoUtil.nuloParaVazio(link);
  }

  /**
   * Altera o link.
   * @param linkParametro - {@link String}
   */
  public void setLink(String linkParametro) {
    this.link = linkParametro;
  }

  /**
   * Retorna o nome.
   * @author Senio Caires
   * @return {@link String}
   */
  @Column(length = 800)
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
  @Column(length = 25)
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
   * Retorna a data da última atualização.
   * @author Senio Caires
   * @return {@link Date}
   */
  @Temporal(TemporalType.DATE)
  public Date getDataAtualizacao() {
    return dataAtualizacao;
  }

  /**
   * Altera a data da última atualização.
   * @author Senio Caires
   * @param dataAtualizacaoParametro - {@link Date}
   */
  public void setDataAtualizacao(Date dataAtualizacaoParametro) {
    this.dataAtualizacao = dataAtualizacaoParametro;
  }

  /**
   * Retorna o status.
   * @author Senio Caires
   * @return {@link Boolean}
   */
  public Boolean getAtivo() {

    if (ativo == null) {
      ativo = Boolean.TRUE;
    }

    return ativo;
  }

  /**
   * Altera o status.
   * @author Senio Caires
   * @param ativoParametro - {@link Boolean}
   */
  public void setAtivo(Boolean ativoParametro) {
    this.ativo = ativoParametro;
  }

  /**
   * toString.
   * @author Senio Caires
   * @return {@link String}
   */
  public String toString() {
    return getLink();
  }

  /**
   * HashCode.
   * @author Senio Caires
   * @return Primitive {@link Integer}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((link == null) ? 0 : link.hashCode());
    return result;
  }

  /**
   * Equals.
   * @author Senio Caires
   * @return Primitive {@link Boolean}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Produto other = (Produto) obj;
    if (link == null) {
      if (other.link != null)
        return false;
    } else if (!link.equals(other.link))
      return false;
    return true;
  }
}
