package managedbean;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sporeon.framework.excecao.ValidacaoException;

import dominio.Pagina;
import enumeracao.TipoLog;
import enumeracao.TipoPagina;
import service.PaginaServiceLocal;

/**
 * Managedbean do crawler.
 * @author Senio Caires
 */
@ManagedBean
@ApplicationScoped
public class Crawler {

  /**
   * Link da página das marcas.
   * @author Senio Caires
   */
  private static final String LINK_PAGINA_LISTA_MARCAS = "http://cosmos.bluesoft.com.br/marcas?page=";

  /**
   * Log.
   * @author Senio Caires
   */
  private StringBuilder log;

  /**
   * Thread do crawler.
   * @author Senio Caires
   */
  private Thread crawler;

  /**
   * Timer para limpar o log.
   * @author Senio Caires
   */
  private Timer cleaner;

  /**
   * EJB dos serviços de página.
   * @author Senio Caires
   */
  @EJB
  private PaginaServiceLocal paginaService;

  /**
   * Iniciar crawler e cleaner do log.
   * @author Senio Caires
   */
  public void iniciar() {

    iniciarCrawler();
    iniciarCleaner();
  }

  /**
   * Iniciar o crawler.
   * @author Senio Caires
   */
  private void iniciarCrawler() {

    crawler = new Thread(new Runnable() {
      public void run() {
        adicionarLog(TipoLog.INFO, "Iniciando crawler.");
        buscarPaginas();
      }
    });
    crawler.setName("Thread.Crawler.navegar");
    crawler.start();
  }

  /**
   * Iniciar o cleaner.
   * @author Senio Caires
   */
  private void iniciarCleaner() {

    cleaner = new Timer();
    cleaner.scheduleAtFixedRate(new TimerTask() {
      public void run() {
        adicionarLog(TipoLog.INFO, "Iniciando cleaner.");
        flushLog();
      }
    }, 0, (30 * 1000) * 1);
  }

  /**
   * Parar o crawler e cleaner do log.
   * @author Senio Caires
   */
  public void parar() {

    pararCrawler();
    pararCleaner();
  }

  /**
   * Parar o crawler.
   * @author Senio Caires
   */
  @SuppressWarnings("deprecation")
  private void pararCrawler() {

    crawler.stop();
    crawler.interrupt();
    crawler = null;
  }

  /**
   * Parar o cleaner.
   * @author Senio Caires
   */
  private void pararCleaner() {

    cleaner.cancel();
    cleaner = null;
  }

  /**
   * Induzir o sleep do crawler para evitar DDOS.
   * @author Senio Caires
   */
  private void induzirSleepCrawler() {
    try {
      adicionarLog(TipoLog.INFO, "Induzindo sleep do crawler.");
//      Thread.sleep((60 * 1000) * 5);
      Thread.sleep((5 * 1000) * 1);
    } catch (InterruptedException e) {
      adicionarLog(TipoLog.ERROR, e.getMessage());
    }
  }

  /**
   * Informa se o crawler está rodando.
   * @author Senio Caires
   * @return Primitive {@link Boolean}
   */
  public boolean isCrawlerRodando() {
    return crawler != null && crawler.isAlive();
  }

  /**
   * Informa se o cleaner está rodando.
   * @author Senio Caires
   * @return Primitive {@link Boolean}
   */
  public boolean isCleanerRodando() {
    return cleaner != null;
  }

  /**
   * Rodar o crawler.
   * @author Senio Caires
   */
  public void buscarPaginas() {

    boolean existePaginaListagemSeguinte = false;
    Pagina ultimaPaginaListagemMarcaAcessada = buscarUltimaPaginaListagemMarcaAcessada();
    Document documentPaginaListagemMarca;

    do {

      try {

        adicionarLog(TipoLog.INFO, "Acessando pagina de listagem: " + ultimaPaginaListagemMarcaAcessada.getLink());
        documentPaginaListagemMarca = Jsoup.connect(ultimaPaginaListagemMarcaAcessada.getLink()).timeout(6000 * 1000).userAgent("Chrome").get();

        //        for (Element elementProdutoPaginaListagem : buscarProdutos(documentPaginaListagemMarca)) {
        //
        //          atualizarPagina(elementProdutoPaginaListagem.getElementsByTag("a").get(0).attr("href"), 1);
        //          Runtime.getRuntime().gc();
        //        }

        /* Atualizando última página de listagem de marca */;
        ultimaPaginaListagemMarcaAcessada.setNumero(ultimaPaginaListagemMarcaAcessada.getNumero() + 1);
        ultimaPaginaListagemMarcaAcessada.setLink(LINK_PAGINA_LISTA_MARCAS + ultimaPaginaListagemMarcaAcessada.getNumero());
        ultimaPaginaListagemMarcaAcessada.setTipo(TipoPagina.LISTA_MARCA);
        ultimaPaginaListagemMarcaAcessada.setDataAtualizacao(new Date());
        ultimaPaginaListagemMarcaAcessada.setAtiva(Boolean.TRUE);

        existePaginaListagemSeguinte = existePaginaSeguinte(documentPaginaListagemMarca);

        if (existePaginaListagemSeguinte) {
          try {
            paginaService.salvar(ultimaPaginaListagemMarcaAcessada);
          } catch (ValidacaoException e) {
            adicionarLog(TipoLog.ERROR, "Erro ao salvar pagina de listagem de marca.");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
          }
        } else {
          try {
            adicionarLog(TipoLog.INFO, "Nao existe proxima pagina de listagem de marca.");
            paginaService.remover(ultimaPaginaListagemMarcaAcessada);
          } catch (ValidacaoException e) {
            adicionarLog(TipoLog.ERROR, "Erro ao remover pagina de listagem de marca.");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
          }
        }

        //        atualizarConteudo();

        Runtime.getRuntime().gc();
        induzirSleepCrawler();

      } catch (IOException e) {
        adicionarLog(TipoLog.ERROR, "Erro ao carregar pagina de listagem de marca.");
        existePaginaListagemSeguinte = false;
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      } catch (Exception e) {
        adicionarLog(TipoLog.ERROR, "Erro desconhecido na pagina de listagem de marca.");
        existePaginaListagemSeguinte = false;
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      }

    } while (existePaginaListagemSeguinte);
  }

  /**
   * Informa se existe próxima página.
   * @author Senio Caires
   * @param pagina - {@link Pagina}
   * @return Primitive {@link Boolean}
   */
  public boolean existePaginaSeguinte(Document pagina) {

    try {
      Element btNext = pagina.select("li[class=next next_page").get(0);
      if ("next next_page".equals(btNext.attr("class").trim())) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Retorna a última página de listagem de marca que foi acessada.
   * @author Senio Caires
   * @return {@link Pagina}
   */
  private Pagina buscarUltimaPaginaListagemMarcaAcessada() {

    Pagina retorno;

    retorno = paginaService.buscarPaginaListagemMarca();

    if (retorno.getId() == null) {
      retorno.setNumero(1);
      retorno.setLink(LINK_PAGINA_LISTA_MARCAS + 1);
    }

    return retorno;
  }

  /**
   * Limpa o log.
   * @author Senio Caires
   */
  public void flushLog() {
    getLog().delete(0, getLog().length());
  }

  /**
   * Adicionar mensagem ao log.
   * @author Senio Caires
   * @param tipo - {@link TipoLog}
   * @param log - {@link String}
   */
  private void adicionarLog(TipoLog tipo, String log) {

    getLog().insert(0, "</div>");
    getLog().insert(0, log);
    if (TipoLog.SUCCESS.equals(tipo)) {
      getLog().insert(0, "<div class='alert alert-success'>");
    } else if (TipoLog.INFO.equals(tipo)) {
      getLog().insert(0, "<div class='alert alert-info'>");
    } else if (TipoLog.WARNING.equals(tipo)) {
      getLog().insert(0, "<div class='alert alert-warning'>");
    } else if (TipoLog.ERROR.equals(tipo)) {
      getLog().insert(0, "<div class='alert alert-danger'>");
    }
  }

  /**
   * Método stub.
   * @author Senio Caires
   */
  public void stub() {
    // Stub
  }

  /**
   * Retorna o log.
   * @author Senio Caires
   * @return {@link String}
   */
  public String getLogString() {
    return getLog().toString();
  }

  /**
   * Retorna o log.
   * @author Senio Caires
   * @return {@link StringBuilder}
   */
  public StringBuilder getLog() {

    if (log == null) {
      log = new StringBuilder();
    }

    return log;
  }

  /**
   * Altera o log.
   * @author Senio Caires
   * @param log - {@link StringBuilder}
   */
  public void setLog(StringBuilder log) {
    this.log = log;
  }
}
