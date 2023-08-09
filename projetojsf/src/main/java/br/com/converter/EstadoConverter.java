package br.com.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Estados;
import br.com.jpautil.JPAUtil;

//value é como se fosse a referência para tela JSF
//Para aceitar a classe como converter é necessário a anotação abaixo:
@FacesConverter(forClass = Estados.class, value = "estadoConverter")
public class EstadoConverter implements Converter, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//Faces Context, recebe a parte de contexto do JSF
	//UIComponent - componentes do JSF que estão sendo usado - exemplo: h:selectOneMenu
	//Retorna objeto inteiro
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, 
			String codigoEstado) {
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		//Startado a transação
		entityTransaction.begin();
		
		Estados estados = (Estados) entityManager.
				find(Estados.class, Long.parseLong(codigoEstado));
	
		return estados;
	}
	
	//Retorna o código apenas em String
	@Override
	public String getAsString(FacesContext context, UIComponent component, 
			Object estado) {
		
		if(estado == null) {
			
			return null;
		}
		
		// se o estado for uma instância do objeto Estado
				if(estado instanceof Estados) {
					return ((Estados) estado).getId().toString();
				}
				else {
					return estado.toString();
				}
		
		//	return ((Estados) estado).getId().toString();
	}
	
	
}
