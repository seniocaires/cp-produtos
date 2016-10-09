package dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sporeon.baseutil.ConversaoUtil;

import enumeracao.TipoPagina;

/**
 * Classe de domínio.<br/>
 * Página.
 * @author Senio Caires
 */
@Entity
@Table(name = "pagina", schema = "cp-produto")
public class Pagina {

  /**
   * Identificador.
   * @author Senio Caires
   */
  private Long id;

  /**
   * Link.
   * @author Senio Caires
   */
  private String link;

  /**
   * Número.
   * @author Senio Caires
   */
  private Integer numero;

  /**
   * Data da última atualização.
   * @author Senio Caires
   */
  private Date dataAtualizacao;

  /**
   * Tipo.
   * @author Senio Caires
   */
  private TipoPagina tipo;

  /**
   * Status.
   * @author Senio Caires
   */
  private Boolean ativa;

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
   * Retorna o número.
   * @author Senio Caires
   * @return {@link Integer}
   */
  public Integer getNumero() {
    return numero;
  }

  /**
   * Altera o número.
   * @author Senio Caires
   * @param numeroParametro - {@link Integer}
   */
  public void setNumero(Integer numeroParametro) {
    this.numero = numeroParametro;
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
   * Retorna o tipo.
   * @author Senio Caires
   * @return {@link TipoPagina}
   */
  @Enumerated(EnumType.STRING)
  public TipoPagina getTipo() {
    return tipo;
  }

  /**
   * Altera o tipo.
   * @author Senio Caires
   * @param tipoParametro - {@link TipoPagina}
   */
  public void setTipo(TipoPagina tipoParametro) {
    this.tipo = tipoParametro;
  }

  /**
   * Retorna o status.
   * @author Senio Caires
   * @return {@link Boolean}
   */
  public Boolean getAtiva() {

    if (ativa == null) {
      ativa = Boolean.TRUE;
    }

    return ativa;
  }

  /**
   * Altera o status.
   * @author Senio Caires
   * @param ativaParametro - {@link Boolean}
   */
  public void setAtiva(Boolean ativaParametro) {
    this.ativa = ativaParametro;
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
    Pagina other = (Pagina) obj;
    if (link == null) {
      if (other.link != null)
        return false;
    } else if (!link.equals(other.link))
      return false;
    return true;
  }
}
