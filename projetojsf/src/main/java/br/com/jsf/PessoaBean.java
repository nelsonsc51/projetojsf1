 package br.com.jsf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

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

	
	
	
	

	public String salvar() {
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
	
	public void carregarCidades(AjaxBehaviorEvent event) {
		
		//Para conseguir pegar o objeto inteiro que foi selecionado no comboBox
		Estados estado = (Estados) ((HtmlSelectOneMenu) event.getSource()).getValue();
		
		
			if(estado != null) {
				pessoa.setEstados(estado);
				
				List<Cidades> cidades = JPAUtil.getEntityManager()
						.createQuery("from Cidades where estados.id = "+estado.getId()).getResultList();
				
				List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
				
				for (Cidades cidade : cidades) {
					selectItemsCidade.add(new  SelectItem(cidade, cidade.getNome()));
				}
				
				setCidades(selectItemsCidade);
				
			}
		
	}
	
	public void editar() {
		if(pessoa.getCidades() != null) {
			Estados estado = pessoa.getCidades().getEstados();
			pessoa.setEstados(estado); 
		}
	}
	
	
	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}
	
	public List<SelectItem> getCidades() {
		return cidades;
	}
	
	

	
	
}
