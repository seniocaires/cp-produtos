package managedbean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.sporeon.framework.excecao.ValidacaoException;

import dominio.Pagina;
import dominio.Produto;
import enumeracao.TipoLog;
import enumeracao.TipoPagina;
import service.PaginaServiceLocal;
import service.ProdutoServiceLocal;

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
   * EJB dos serviços de produto.
   * @author Senio Caires
   */
  @EJB
  private ProdutoServiceLocal produtoService;

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
      Thread.sleep((60 * 1000) * 5);
      //      Thread.sleep((5 * 1000) * 1);
    } catch (InterruptedException e) {
      adicionarLog(TipoLog.ERROR, e.getMessage());
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
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

        for (Element elementMarcaPaginaListagem : buscarMarcas(documentPaginaListagemMarca)) {

          atualizarPagina(elementMarcaPaginaListagem.getElementsByTag("a").get(0).attr("href"), 1);
          Runtime.getRuntime().gc();
        }

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

        atualizarConteudo();

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
   * Atualiza as marcas e os produtos.
   * @author Senio Caires
   */
  public void atualizarConteudo() {

    Document documentMarca;

    for (Pagina pagina : paginaService.buscarAtivas(TipoPagina.MARCA)) {

      adicionarLog(TipoLog.INFO, "Acessando pagina da marca: " + pagina.getLink());

      try {
        documentMarca = Jsoup.parse(pagina.getHtml(), "ISO-8859-1");
      } catch (Exception e) {
        try {
          adicionarLog(TipoLog.WARNING, "Erro ao obter dados basicos da marca. Desativando pagina e seguindo para proxima pagina ativa.");
          Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
          pagina.setAtiva(Boolean.FALSE);
          paginaService.salvar(pagina);
        } catch (ValidacaoException ve) {
          adicionarLog(TipoLog.ERROR, "Erro ao desativar pagina.");
          Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        continue;
      }

      try {
        pagina.setAtiva(Boolean.FALSE);
        paginaService.salvar(pagina);
      } catch (ValidacaoException e) {
        adicionarLog(TipoLog.ERROR, "Erro ao desativar pagina.");
        adicionarLog(TipoLog.ERROR, e.getMessage());
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      }

      salvarProdutos(documentMarca, pagina);

      Runtime.getRuntime().gc();
    }
  }

  /**
   * Atualiza os produtos.
   * @author Senio Caires
   * @param documentMarca {@link Document}
   * @param pagina {@link Pagina}
   */
  private void salvarProdutos(Document documentMarca, Pagina pagina) {

    Element gradeProdutos;
    Elements produtosHtml;
    Produto produto;

    try {

      gradeProdutos = documentMarca.getElementById("tbl-produtos");

      produtosHtml = gradeProdutos.getElementsByTag("li");

    } catch (Exception e) {
      adicionarLog(TipoLog.ERROR, "Erro ao procurar produtos.");
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      return;
    }

    for (Element produtoHtml : produtosHtml) {

      try {

        produto = produtoService.buscarPorLink(getLinkProduto(produtoHtml));
        if (produto == null || produto.getId() == null) {
          produto = new Produto(getLinkProduto(produtoHtml));
        }

        produto.setLink(getLinkProduto(produtoHtml));
        produto.setNome(getNomeProduto(produtoHtml));
        produto.setCodigoBarras(getCodigoBarrasProduto(produtoHtml));
        produto.setAtivo(Boolean.TRUE);
        produto.setDataAtualizacao(pagina.getDataAtualizacao());
        adicionarLog(TipoLog.INFO, "Produto adicionado: " + produto.getNome() + " " + produto.getCodigoBarras() + " " + produto.getLink());
        try {
          produtoService.salvar(produto);
        } catch (ValidacaoException e1) {
          adicionarLog(TipoLog.ERROR, "Erro ao salvar produto.");
          Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e1.getMessage(), e1);
        }
      } catch (Exception e) {
        adicionarLog(TipoLog.WARNING, "Erro ao buscar produto. Buscando proximo produto.");
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        produto = null;
        continue;
      }
    }
  }

  /**
   * Retorna o link do produto.
   * @param produtoHtml
   * @return {@link String}
   */
  private String getLinkProduto(Element produtoHtml) {

    StringBuilder resultado = new StringBuilder();
    try {
      resultado.append("http://cosmos.bluesoft.com.br");
      resultado.append(URLDecoder.decode(produtoHtml.getElementsByClass("picture").get(0).getElementsByTag("a").get(0).attr("href").trim(), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      adicionarLog(TipoLog.ERROR, "Erro no decode da url");
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Erro ao recuperar link do produto. " + produtoHtml.html() + e.getMessage(), e);
      resultado.append("");
    }

    return resultado.toString().toLowerCase();
  }

  /**
   * Retorna o nome do produto.
   * @param produtoHtml
   * @return {@link String}
   */
  private String getNomeProduto(Element produtoHtml) {

    StringBuilder resultado = new StringBuilder();
    try {
      resultado.append(produtoHtml.getElementsByClass("description").get(0).getElementsByTag("a").get(0).html().trim());
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Erro ao recuperar nome do produto. " + produtoHtml.html() + e.getMessage(), e);
      resultado.append("");
    }

    return resultado.toString();
  }

  /**
   * Retorna o código de barras do produto.
   * @param produtoHtml
   * @return {@link String}
   */
  private String getCodigoBarrasProduto(Element produtoHtml) {

    StringBuilder resultado = new StringBuilder();
    try {
      resultado.append(produtoHtml.getElementsByClass("barcode").get(0).getElementsByTag("a").get(0).html().trim());
    } catch (Exception e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Erro ao recuperar codigo de barras do produto. " + produtoHtml.html() + e.getMessage(), e);
      resultado.append("");
    }

    return resultado.toString();
  }

  /**
   * Atualiza a página.
   * @author Senio Caires
   * @param linkSemPaginacao - {@link String}
   * @param numeroPagina - Primitive {@link Integer}
   */
  private void atualizarPagina(String linkSemPaginacao, int numeroPagina) {

    Pagina paginaMarca;
    Document documentPaginaMarca;

    try {

      /* Recuperando página ou criando uma nova. */
      paginaMarca = paginaService.buscarPorLink(URLDecoder.decode("http://cosmos.bluesoft.com.br" + linkSemPaginacao + "/produtos?page=" + numeroPagina, "UTF-8"));
      if (paginaMarca == null || paginaMarca.getId() == null) {
        paginaMarca = new Pagina(URLDecoder.decode("http://cosmos.bluesoft.com.br" + linkSemPaginacao + "/produtos?page=" + numeroPagina, "UTF-8"));
      }

      /* Acessando página */
      adicionarLog(TipoLog.INFO, "Acessando pagina da marca: " + paginaMarca.getLink());
      documentPaginaMarca = Jsoup.connect(paginaMarca.getLink()).timeout(6000 * 1000).userAgent("Chrome").get();

      /* Atualizando conteúdo da página */
      paginaMarca.setAtiva(true);
      paginaMarca.setTipo(TipoPagina.MARCA);
      paginaMarca.setDataAtualizacao(new Date());
      paginaMarca.setNumero(numeroPagina);
      paginaMarca.setHtml(getHtmlComprimido(paginaMarca.getLink()));

      try {
        paginaService.salvar(paginaMarca);
      } catch (ValidacaoException ve) {
        adicionarLog(TipoLog.ERROR, "Erro ao persistir pagina: " + paginaMarca.getLink());
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ve.getMessage(), ve);
      }

      if (existePaginaSeguinte(documentPaginaMarca)) {
        atualizarPagina(linkSemPaginacao, ++numeroPagina);
      }

    } catch (UnsupportedEncodingException e) {
      adicionarLog(TipoLog.ERROR, "Erro no encoding da url da pagina." + "http://cosmos.bluesoft.com.br" + linkSemPaginacao + "/produtos?page=" + numeroPagina);
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
    } catch (HttpStatusException hse) {
      adicionarLog(TipoLog.WARNING, "Erro ao acessar pagina da marca." + "http://cosmos.bluesoft.com.br" + linkSemPaginacao + "/produtos?page=" + numeroPagina);
      Logger.getLogger(this.getClass().getName()).log(Level.WARNING, hse.getMessage(), hse);
    } catch (IOException e) {
      adicionarLog(TipoLog.ERROR, "Erro ao carregar pagina." + "http://cosmos.bluesoft.com.br" + linkSemPaginacao + "/produtos?page=" + numeroPagina);
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
    } catch (Exception e) {
      adicionarLog(TipoLog.ERROR, "Erro ao buscar pagina." + "http://cosmos.bluesoft.com.br" + linkSemPaginacao + "/produtos?page=" + numeroPagina);
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
    }
  }

  /**
   * Retorna a lista de marcas da página passada por parâmetro.
   * @author Senio Caires
   * @param pagina - {@link Document}
   * @return Elements
   */
  public Elements buscarMarcas(Document pagina) {

    Elements retorno;

    try {

      Element gradeMarcas = pagina.getElementsByClass("list-numbered").get(0);

      retorno = gradeMarcas.getElementsByTag("li");

    } catch (Exception e) {
      adicionarLog(TipoLog.ERROR, "Erro ao buscar marca da pagina de listagem.");
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      retorno = new Elements();
    }

    return retorno;
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
   * Retorna o html da url passada por parâmetro.
   * @author Senio Caires
   * @param url - {@link String}
   * @return {@link String}
   */
  private String getHtmlComprimido(String url) {

    StringBuilder retorno = new StringBuilder();
    HtmlCompressor compressor = new HtmlCompressor();
    compressor.setCompressCss(true);
    compressor.setCompressJavaScript(false);

    try {
      retorno.append(Jsoup.connect(url).timeout(6000 * 1000).userAgent("Chrome").get().html());
    } catch (IOException e) {
      adicionarLog(TipoLog.ERROR, "Erro ao acessar pagina e comprimir HTML.");
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
    }

    return compressor.compress(retorno.toString());
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
