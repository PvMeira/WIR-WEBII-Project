package com.senac.cl.managed.beans;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.senac.cl.enums.tipoLeituraHistorico;
import com.senac.cl.modelos.Leitura;
import com.senac.cl.modelos.LeituraHistorico;
import com.senac.cl.modelos.LeituraService;
import com.senac.cl.modelos.Livro;
import com.senac.cl.service.LeituraHistoricoService;
import com.senac.cl.service.LivroService;

/**
 * 
 * @author Pedro
 * @since 11/09/2016
 */
@ManagedBean
@ViewScoped
public class LeituraMB {

	private Leitura leitura;

	@ManagedProperty("#{livroSelecionado}")
	private Livro livroSelecionado;

	private List<Livro> listaSugestao;

	@Inject
	private LeituraService leituraService;

	@Inject
	private LivroService livroService;

	@Inject
	private LivroMB livroMB;
	@Inject
	private LeituraHistoricoService leituraHistoricoService;

	private StreamedContent streamedContent;

	public List<LeituraHistorico> listaHistoricoLeituraUsuarioLogado() {

		return this.leituraHistoricoService.listaHistoricoLeituraUsuarioLogado();

	}

	private List<Leitura> leituraUsuarioLogado;

	/**
	 * Conversor acao leitura
	 * 
	 * @param ed
	 * @return
	 */
	public String conversorAcaoEnumleitura(LeituraHistorico ed) {
		if (tipoLeituraHistorico.getTipoLeituraHistoricoEnumPorSigla(ed.getTipoAcao()).getNome()
				.equalsIgnoreCase("CANCELAMENTO")) {
			return "LIDO";
		}
		if (tipoLeituraHistorico.getTipoLeituraHistoricoEnumPorSigla(ed.getTipoAcao()).getNome()
				.equalsIgnoreCase("ALTERACAO")) {
			return "LENDO";
		} else {
			return tipoLeituraHistorico.getTipoLeituraHistoricoEnumPorSigla(ed.getTipoAcao()).getNome();
		}

	}

	public StreamedContent getStreamedContent() {
		if (FacesContext.getCurrentInstance().getRenderResponse()) {
			return new DefaultStreamedContent();
		} else {
			return streamedContent;
		}
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	/**
	 * 
	 * @param ed
	 */
	public void salvar() {
		this.leituraService.salvar(this.livroSelecionado);
	}

	/**
	 * 
	 * @param ed
	 */
	public void pararLeitura(Leitura ed) {
		this.leituraService.pararLeitura(ed);
	}

	/**
	 * Atualiza a observação da leitura atual
	 */
	public void atualizarObservacao() {
		this.leituraService.atualizar(this.leitura);
		this.limparCampos();
	}

	/**
	 * Popula a entidade para a atualização da observação
	 * 
	 * @param ed
	 */
	public void populaLeituraParaObservacao(Leitura ed) {
		this.leitura = ed;
	}

	/**
	 * limpa campos
	 */
	public void limparCampos() {
		this.leitura = null;
	}

	public List<Livro> listarLivrosAutoComplete(String particula) {
		List<Livro> lista = this.livroService.listarLivrosAutoComplete(particula);
		return lista;
	}

	/**
	 * 
	 * @return
	 */
	public List<Leitura> listaTodasLeituras() {
		return this.leituraService.listaTodoOsRegistros();
	}

	/**
	 * Lista todas as leituras da pessoa logada
	 * 
	 * @return
	 */
	public List<Leitura> listaTodasLeiturasPessoaLogada() {
		if (this.leituraUsuarioLogado != null) {
			return this.leituraUsuarioLogado;
		} else {
			this.populaListaLeiturasPessoaLogada();
			return this.leituraUsuarioLogado;
		}
	}

	private void populaListaLeiturasPessoaLogada() {
		this.leituraUsuarioLogado = this.leituraService.listaTodasLeiturasPessoaLogada();
	}

	/**
	 * @return the leituraService
	 */
	public LeituraService getLeituraService() {
		return leituraService;
	}

	/**
	 * @param leituraService
	 *            the leituraService to set
	 */
	public void setLeituraService(LeituraService leituraService) {
		this.leituraService = leituraService;
	}

	/**
	 * @return the leitura
	 */
	public Leitura getLeitura() {
		return leitura;
	}

	/**
	 * @param leitura
	 *            the leitura to set
	 */
	public void setLeitura(Leitura leitura) {
		this.leitura = leitura;
	}

	/**
	 * @return the livroSelecionado
	 */
	public Livro getLivroSelecionado() {
		return livroSelecionado;
	}

	/**
	 * @param livroSelecionado
	 *            the livroSelecionado to set
	 */
	public void setLivroSelecionado(Livro livroSelecionado) {
		this.livroSelecionado = livroSelecionado;
	}

	/**
	 * @return the livroService
	 */
	public LivroService getLivroService() {
		return livroService;
	}

	/**
	 * @param livroService
	 *            the livroService to set
	 */
	public void setLivroService(LivroService livroService) {
		this.livroService = livroService;
	}

	/**
	 * @return the listaSugestao
	 */
	public List<Livro> getListaSugestao() {
		return listaSugestao;
	}

	/**
	 * @param listaSugestao
	 *            the listaSugestao to set
	 */
	public void setListaSugestao(List<Livro> listaSugestao) {
		this.listaSugestao = listaSugestao;
	}

	/**
	 * @return the livroMB
	 */
	public LivroMB getLivroMB() {
		return livroMB;
	}

	/**
	 * @param livroMB
	 *            the livroMB to set
	 */
	public void setLivroMB(LivroMB livroMB) {
		this.livroMB = livroMB;
	}

	/**
	 * @return the leituraUsuarioLogado
	 */
	public List<Leitura> getLeituraUsuarioLogado() {
		return leituraUsuarioLogado;
	}

	/**
	 * @param leituraUsuarioLogado
	 *            the leituraUsuarioLogado to set
	 */
	public void setLeituraUsuarioLogado(List<Leitura> leituraUsuarioLogado) {
		this.leituraUsuarioLogado = leituraUsuarioLogado;
	}

}
