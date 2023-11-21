package com.deveficiente.desafiocheckouthotmart.contas;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import com.deveficiente.desafiocheckouthotmart.compartilhado.Resultado;
import com.deveficiente.desafiocheckouthotmart.configuracoes.Configuracao;
import com.deveficiente.desafiocheckouthotmart.produtos.Produto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Conta {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private @NotBlank @Email String email;
    @NotNull
    @ManyToOne
    private Configuracao configuracao;
    @NotNull
    private UUID codigo;
    @OneToMany(mappedBy = "conta",cascade = CascadeType.PERSIST)
	private Set<Produto> produtos = new HashSet<>();
    
    @Deprecated
    public Conta() {
		// TODO Auto-generated constructor stub
	}

    public Conta(@NotBlank @Email String email, Configuracao configuracao) {
        this.email = email;
        this.configuracao = configuracao;
        this.codigo = UUID.randomUUID();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Conta other = (Conta) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        return true;
    }

    /**
     * 
     * @param funcaoCriadoraDeProduto
     * @return novo {@link Produto} adicionado na conta
     */
    public Resultado<RuntimeException, Produto> adicionaProduto(Function<Conta,Produto> funcaoCriadoraDeProduto) {
        Produto novoProduto = funcaoCriadoraDeProduto.apply(this);
		boolean adicionou = this.produtos.add(novoProduto);
		
		
        if(adicionou) {
        	return Resultado.sucessoComInfoAdicional(novoProduto);
        }
        return Resultado.falhaCom(new JaExisteProdutoComMesmoNomeException(novoProduto));
    }

	public UUID getCodigo() {
		
		return codigo;
	}

    

}
