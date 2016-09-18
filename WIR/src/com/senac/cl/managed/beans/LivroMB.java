package com.senac.cl.managed.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RateEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.senac.cl.modelos.Leitura;
import com.senac.cl.modelos.LeituraService;
import com.senac.cl.modelos.Livro;
import com.senac.cl.service.LivroService;
import com.senac.cl.utilitarios.Data;
import com.senac.cl.utilitarios.SistemaDeMensagens;

/**
 * 
 * @author Pedro
 * @since 11/09/2016
 */
@ManagedBean
@ViewScoped
public class LivroMB {

	private Livro livro;
	private Livro livroParaTransferir;
	private List<Livro> livrosPessoaLogada;
	private List<Livro> livrosSelecionados;

	@Inject
	private LivroService livroService;

	@Inject
	private LeituraService leituraService;

	@Inject
	Data data;

	private final static String LIDO = "Livro j� Lido";
	private final static String LENDO = "Lendo";
	private final static String JA_FOI_LIDO = "J� Foi Lido";
	private final static String NAO_LIDO = "Livro n�o Lido";
	private boolean tornarpublico;

	public LivroMB() {
	}

	/**
	 * Salva o livro
	 */
	public void salvar() {
		livroService.salvar(this.getLivro(), tornarpublico);
		SistemaDeMensagens.notificaINFORMACAO("Parab�ns!", "Cadastro salvo com sucesso!");
		limpar();
	}

	/**
	 * Salva uma nova leitura apartir do livro seleciona inline
	 */
	public void iniciarLeituraLivroInline(Livro ed) {
		this.leituraService.salvar(ed);
	}

	/**
	 * Transfere o livro que foi selecionado e e muda a booleana publica para
	 * TRUE
	 */
	public void transferir() {
		this.livroService.atualizarATransferenciaParaPublico(this.livroParaTransferir);
	}

	/**
	 * Copia os livros selecionados para a conta do usuario logado
	 */
	public void copiarLivrosPublicosParaContaUsuario(Livro ed) {
		this.livroService.copiaLivroPublicoParaContaUsuarioLogado(ed);
	}

	/**
	 * Copia os livros selecionados para a conta do usuario logado em lote
	 */
	public void copiarLivrosPublicosParaContaUsuarioEmLote() {
		List<Livro> lista = this.livrosSelecionados;
		for (Livro livro : lista) {
			this.copiarLivrosPublicosParaContaUsuario(livro);
		}
	}

	/**
	 * Lista livros do autocomplete que esteja com publico = false
	 * 
	 * @param particula
	 * @return
	 */
	public List<Livro> listarLivrosAutoCompleteTransferir(String particula) {
		List<Livro> lista = this.livroService.listarLivrosAutoCompleteTransferir(particula);
		return lista;
	}

	/**
	 * Deleta o livro selecionado
	 * 
	 * @param livro
	 */
	public void deletar(Livro livro) {
		livroService.deletar(livro);
		SistemaDeMensagens.notificaINFORMACAO("Parab�ns!", "Cadastro deletado ");
		limpar();
	}

	/**
	 * Deletar Sem as informa��es de sucessso para o metodo de delete em lote
	 * 
	 * @param livro
	 */
	public void deletarSeminforma��o(Livro livro) {
		livroService.deletar(livro);
	}

	/**
	 * Carrega o arquivo upado para a entidade
	 * 
	 * @param event
	 */
	public void carregarArquivo(FileUploadEvent event) {
		// Converte o arquivo do evento em um array de bytes
		// para ser armazenado no banco
		byte[] conteudo = event.getFile().getContents();
		this.livro.setArquivo(conteudo);
	}

	/**
	 * Retorna o Arquivo para downlaod do pdf
	 * 
	 * @param livro
	 * @return
	 * @throws IOException
	 */
	public StreamedContent FileDownloadView(Livro livro) throws IOException {
		// Converte os bytes do livro para um arquivo
		ByteArrayInputStream bis = new ByteArrayInputStream(livro.getArquivo());
		StreamedContent file;
		// disponibiliza o formato e o tipo de arquivo para o download
		file = new DefaultStreamedContent(bis, "application/pdf", livro.getTitulo() + ".pdf");
		// retorna o arquivo
		return file;
	}

	/**
	 * Metodo para Retornar um .ZIP com todos os PDF selecionados
	 * 
	 * @return
	 * @throws IOException
	 */
	public DefaultStreamedContent geradorZip() throws IOException {
		ByteArrayOutputStream arq = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(arq);
		List<Livro> listaLivrosSelecionados = this.livrosSelecionados;
		for (Livro livro : listaLivrosSelecionados) {
			byte[] buffer = new byte[99999];
			StreamedContent file = FileDownloadView(livro);
			ZipEntry entry = new ZipEntry(file.getName());
			zip.putNextEntry(entry);
			int len;
			while ((len = file.getStream().read(buffer)) > 0) {
				zip.write(buffer, 0, len);
			}
			file.getStream().close();
			zip.closeEntry();
		}
		zip.close();
		String d = this.data.getDataFormatoPTbrInteira(Calendar.getInstance());
		return new DefaultStreamedContent(new ByteArrayInputStream(arq.toByteArray()), "application/zip",
				"LivrosSelecionados" + d + ".zip");
	}

	/**
	 * Verifica o status do livro, caso seja true retorna n�o lido se n�o
	 * retorna ja lido
	 * 
	 * @param livro
	 * @return
	 */
	public String VerificaStatusLivro(Livro livro) {
		if (livro.isLivroAtivo() == false && livro.isJaFoiLido() == false) {
			Leitura lei = this.buscaLivroLeitura(livro);
			if (lei != null) {
				return LENDO;
			} else {
				return LIDO;
			}
		}
		if (livro.isJaFoiLido() == true) {
			return JA_FOI_LIDO;
		} else {
			return NAO_LIDO;
		}
	}

	/**
	 * busca se existe uma leitura para o livro em questao
	 * 
	 * @param ed
	 * @return
	 */
	private Leitura buscaLivroLeitura(Livro ed) {
		return this.leituraService.buscaLeituraPeloId(ed);

	}

	/**
	 * popula a string de observa��o que sera usada na modal de observa��o
	 */
	public void populaObservacaoParaModal(Livro livro) {
		this.livro = livro;
	}

	/**
	 * metodo para atualizar o resumo que foi alterado
	 */
	public void atualizaResumoLivro() {
		livroService.atualizar(this.livro);
	}

	/**
	 * metodo para fazer o delete em lote atraves do selection do dataTable
	 */
	public void deletarEmLote() {
		int contador = 0;
		List<Livro> lista = this.livrosSelecionados;
		for (Livro livro : lista) {
			deletarSeminforma��o(livro);
			contador++;
		}
		SistemaDeMensagens.notificaINFORMACAO("Livros deletados ", "Com sucesso, numero de livros :" + contador);
	}

	/**
	 * Metodo para atualizar a pontua��o do livro apartir o evento ajax na 1
	 * camada
	 * 
	 * @param rateEvent
	 */
	public void updatePontuacaoLivro(RateEvent rateEvent) {
		Integer pontuacao = 0;
		// Pega o evento do componente do prime e passa para um int
		pontuacao = ((Integer) rateEvent.getRating()).intValue();
		this.livro.setPontuacao(pontuacao);
		livroService.atualizar(this.livro);
	}

	/**
	 * Retorna a hint do label que contem o email e username para contato
	 * 
	 * @param ed
	 * @return
	 */
	public String getHintColaborador(Livro ed) {
		String username = ed.getDono().getUsername();
		String email = ed.getDono().getMail();
		String hint = username.concat(" | ").concat(email);

		return hint;
	}

	/**
	 * Listar todos os livros do banco
	 * 
	 * @return
	 */
	public List<Livro> listarTodosLivros() {
		return livroService.listarTodosRegistros();
	}

	/**
	 * seta um novo livro
	 */
	public void limpar() {
		setLivro(new Livro());
		setLivroParaTransferir(null);
	}

	/**
	 * Lista todos os livros do usuario logado
	 */
	public List<Livro> listaLivrosPessoaLogada() {
		return this.livroService.listarTodosLivrosDoUsuario();
	}

	/**
	 * Lista todos os livros que est�o marcados como P�blicos
	 * 
	 * @return
	 */
	public List<Livro> listaLivrosPublicos() {
		return this.livroService.listaTodosLivrosPublicos();
	}

	// -----------get set
	/**
	 * @return the livro
	 */
	public Livro getLivro() {
		if (this.livro == null) {
			limpar();
		}
		return livro;
	}

	/**
	 * @param livro
	 *            the livro to set
	 */
	public void setLivro(Livro livro) {
		this.livro = livro;
	}

	/**
	 * @return the livrosPessoaLogada
	 */
	public List<Livro> getLivrosPessoaLogada() {
		return livrosPessoaLogada;
	}

	/**
	 * @param livrosPessoaLogada
	 *            the livrosPessoaLogada to set
	 */
	public void setLivrosPessoaLogada(List<Livro> livrosPessoaLogada) {
		this.livrosPessoaLogada = livrosPessoaLogada;
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
	 * @return the livrosSelecionados
	 */
	public List<Livro> getLivrosSelecionados() {
		return livrosSelecionados;
	}

	/**
	 * @param livrosSelecionados
	 *            the livrosSelecionados to set
	 */
	public void setLivrosSelecionados(List<Livro> livrosSelecionados) {
		this.livrosSelecionados = livrosSelecionados;
	}

	/**
	 * @return the tornarpublico
	 */
	public boolean isTornarpublico() {
		return tornarpublico;
	}

	/**
	 * @param tornarpublico
	 *            the tornarpublico to set
	 */
	public void setTornarpublico(boolean tornarpublico) {
		this.tornarpublico = tornarpublico;
	}

	/**
	 * @return the livroParaTransferir
	 */
	public Livro getLivroParaTransferir() {
		return livroParaTransferir;
	}

	/**
	 * @param livroParaTransferir
	 *            the livroParaTransferir to set
	 */
	public void setLivroParaTransferir(Livro livroParaTransferir) {
		this.livroParaTransferir = livroParaTransferir;
	}

}
