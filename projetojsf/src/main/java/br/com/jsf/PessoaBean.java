 package br.com.jsf;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

import br.com.dao.DaoGeneric;
import br.com.entidades.Cidades;
import br.com.entidades.Estados;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;
import br.com.repository.IDAoPessoaImpl;
import br.com.repository.IDaoPessoa;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean  {

	private Pessoa pessoa = new Pessoa();
		
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	private IDaoPessoa iDaoPessoa = new IDAoPessoaImpl();
	
	private List<SelectItem> estados;
	
	private List<SelectItem> cidades;
	
	private Part arquivoFoto;
	
	
	//GETTERS AND SETTERS
	public Pessoa getPessoa() {
		return pessoa;
	}
	
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	public DaoGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}
	
	public void setDaoGeneric(DaoGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	
	public List<Pessoa> getPessoas() {
		return pessoas;
	}
	
	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}
	

	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listaEstados();
		return estados;
	}
	
	public void setEstados(List<SelectItem> estados) {
		this.estados = estados;
	}

	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}
	
	public List<SelectItem> getCidades() {
		return cidades;
	}
	
	
	public void setArquivoFoto(Part arquivoFoto) {
		this.arquivoFoto = arquivoFoto;
	}
	
	public Part getArquivoFoto() {
		return arquivoFoto;
	}
	

	//MÉTODOS
	public String salvar() throws IOException {
		
		//processa a imagem
		//byte[] imagemByte = getByte(arquivoFoto.getInputStream());
		//pessoa.setFotoIconBase64Original(imagemByte);
		
		//tranforma e'm BufferedImage
		//BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemByte));
		
		//Pega o tipo da imagem
		/*
		int type = bufferedImage.getType() == 0? BufferedImage.TYPE_4BYTE_ABGR : bufferedImage.getType();
		
		int largura = 200;
		int altura = 200;
		
		//Criar a miniatura
		BufferedImage resizeImage = new BufferedImage(altura, altura, type);
		Graphics2D g = resizeImage.createGraphics();
		g.drawImage(bufferedImage, 0, 0, largura, altura, null);
		g.dispose();
		
		//escrever novamente a img em tamanho menor
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String extensao = arquivoFoto.getContentType().split("\\/")[1];
		ImageIO.write(resizeImage, extensao, baos);
		
		String miniImagem = "data:" +arquivoFoto.getContentType() + ";base64, " +
		DatatypeConverter.printBase64Binary(baos.toByteArray());
		
		//processar a imagem
		pessoa.setFotoIconBase64(miniImagem);
		pessoa.setExtensao(extensao);
		*/
		pessoa = daoGeneric.merge(pessoa);
		carregarPessoas();
		mostrarmsg("cadastrado com sucesso");
		return "";
	}
	
	private void mostrarmsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
		
	}

	public String novo() {
		//antes do novo
		pessoa = new Pessoa();
		return "";
	}
	
	public String limpar() {
		//antes de limpar
		pessoa = new Pessoa();
		return "";
	}
	
	public String remove() {
		daoGeneric.deletePorId(pessoa);
		pessoa = new Pessoa();
		carregarPessoas();
		mostrarmsg("Removido com Sucesso");
		return "";
	}
	
	
	public String logar() {
		
		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
	
		if(pessoaUser != null) {
			
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser);
			addMessage("messages");
			
			return "primeiraPagina.jsf";
		}
		
		
		return "index.jsf";
	}
	
	private String addMessage(String msg) {
		msg = ""; 
		
		return msg;
		
	}

	public String deslogar() {
		
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().remove("usuarioLogado");
			
			HttpServletRequest httpServletRequest = (HttpServletRequest)context.getCurrentInstance().
					getExternalContext().getRequest();
			httpServletRequest.getSession().invalidate();
			
			
			return "index.jsf";
	}
	
	
	public boolean permiteAcesso(String acesso) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		
		return pessoaUser.getPerfilUser().equals(acesso);
	}
	
	@PostConstruct
	public void carregarPessoas() {
		pessoas = daoGeneric.getListEntity(Pessoa.class);
		
	}
	
	public void pesquisaCep(AjaxBehaviorEvent event) {
		try {
			URL url = new URL("https://viacep.com.br/ws/"+pessoa.getCep()+"/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			
			while((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}
			
			Pessoa gsonAux = new Gson().fromJson(jsonCep.toString(), Pessoa.class);
			
			pessoa.setCep(gsonAux.getCep());
			pessoa.setLogradouro(gsonAux.getLogradouro());
			pessoa.setComplemento(gsonAux.getComplemento());
			pessoa.setBairro(gsonAux.getBairro());
			pessoa.setLocalidade(gsonAux.getLocalidade());
			pessoa.setUf(gsonAux.getUf());
			
			System.out.println(gsonAux);
		} catch (Exception e) {
			mostrarmsg("Erro ao consultar o CEP");
		}
		
	}
	// o event dispara um source com submitedValue onde possui o código do estado aramazenado nele
	public void carregarCidades(AjaxBehaviorEvent event) {
		
		//Para pegar o Objeto Inteiro do estado na tela JSF
		//Para conseguir pegar o objeto inteiro que foi selecionado no comboBox
		//todo componente JSF tem uma classe que representa ele	- HtmlSelectOneMenu
		Estados estado = (Estados) ((HtmlSelectOneMenu) event.getSource()).getValue();
		
		//CóDIGO recuperando pelo o id do Estado
	//	String codigoEstado = (String) event.getComponent().
		//		getAttributes().get("submittedValue");
		
		//Agora não precia mais dessa condição, pois o estado não é mais pelo id
	//	if(codigoEstado != null) {

			//Alterando o estado para um objeto inteiro, realizando a alteração acima
			//não é preciso fazer a consulta no BD pelo JPAUtil
			//Estados estado = JPAUtil.getEntityManager().find(Estados.class, Long.parseLong(codigoEstado));
			
			
			// Dessa forma já é possível obter o objeto do estados no JSF 
		 	if(estado != null) {
				pessoa.setEstados(estado);
				
				List<Cidades> cidades = JPAUtil.getEntityManager()
						.createQuery("from Cidades where estados.id = "
						+ estado.getId()).getResultList();
						//Código Antigo
						//+ codigoEstado).getResultList();

				List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
				
				for (Cidades cidade : cidades) {
					selectItemsCidade.add(new  SelectItem(cidade, cidade.getNome()));
				}
				setCidades(selectItemsCidade);
				
			}
		
		
//		}
		
		
		
		/*
			if(estado != null) {
				pessoa.setEstados(estado);
				
				List<Cidades> cidades = JPAUtil.getEntityManager()
						.createQuery("from Cidades where estados.id = "
						+estado.getId()).getResultList();
				
				List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
				
				for (Cidades cidade : cidades) {
					selectItemsCidade.add(new  SelectItem(cidade, cidade.getNome()));
				}
				
				setCidades(selectItemsCidade);
				
			}
			*/
		
	}
	
	public void editar() {
		if(pessoa.getCidades() != null) {
			Estados estado = pessoa.getCidades().getEstados();
			pessoa.setEstados(estado);
			
			List<Cidades> cidades = JPAUtil.getEntityManager()
					.createQuery("from Cidades where estados.id = "
					+estado.getId()).getResultList();
			
			List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
			
			for (Cidades cidade : cidades) {
				selectItemsCidade.add(new  SelectItem(cidade, cidade.getNome()));
			}
			
			setCidades(selectItemsCidade);
		}
	}
	
	
	
	
	
	//metodo que converte um inputStream em array de bytes
	private byte[] getByte (InputStream is) throws IOException {
		
		int len;
		int size = 1024;
		byte[] buf = null;
		if (is instanceof ByteArrayInputStream) {
			size = is.available();
			buf = new byte[size];
			len = is.read(buf, 0, size);
		}else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[size];
			
			while ((len = is.read(buf, 0, size )) != -1) {
				bos.write(buf, 0, len);				
			}
			
			buf = bos.toByteArray();
		}
		
		return buf;
	}
	
	public void download() throws IOException {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String fileDownlodId = params.get("fileDownloadId");
		
		Pessoa pessoa = daoGeneric.consultar(Pessoa.class, fileDownlodId);
		
		
		System.out.println(pessoa);
		
		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
				.getExternalContext().getResponse();
		
		response.addHeader("Content-Disposition", "attachment; filename=download."+ pessoa.getExtensao());
		response.setContentType("application/octet-stream");
		response.setContentLength(pessoa.getFotoIconBase64Original().length);
		response.getOutputStream().write(pessoa.getFotoIconBase64Original());
		response.getOutputStream().flush();
		FacesContext.getCurrentInstance().responseComplete();
		
	
	}
	
}
