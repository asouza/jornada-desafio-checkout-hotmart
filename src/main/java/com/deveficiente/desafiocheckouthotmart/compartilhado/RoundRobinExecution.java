package com.deveficiente.desafiocheckouthotmart.compartilhado;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe para possibilitar a execução de funções seguindo o modelo 
 * de round robin, inspirado no princípio de Constant Work Pattern da 
 * Amazon
 * @author albertoluizsouza
 *
 * @param <T>
 * @param <R>
 */
public class RoundRobinExecution<T, R> {
    private final List<Supplier<Optional<Function<T, R>>>> functions;
    private int currentIndex = 0;
    private final ReentrantLock lock = new ReentrantLock();
	private String contexto;
	
	private static final Logger log = LoggerFactory
			.getLogger(RoundRobinExecution.class);


    public RoundRobinExecution(String contexto,List<Supplier<Optional<Function<T, R>>>> functions) {
        this.contexto = contexto;
        
		if (functions == null || functions.isEmpty()) {
            throw new IllegalArgumentException("Lista de funções não pode ser nula ou vazia.");
        }
        this.functions = functions;
    }

    public Supplier<Optional<Function<T, R>>> getNextFunction() {
        lock.lock();
        try {
        	//aqui podia ser debug
        	Log5WBuilder
        		.metodo()
        		.oQueEstaAcontecendo("Escolhendo próxima funcao a ser executada no round robin")
        		.adicionaInformacao("contexto", contexto)
        		.adicionaInformacao("indice", currentIndex+"")
        		.info(log);
        	
            Supplier<Optional<Function<T, R>>> function = functions.get(currentIndex);
            currentIndex = (currentIndex + 1) % functions.size();
            return function;
        } finally {
            lock.unlock();
        }
    }

    public int getNumberOfOptions() {
        return this.functions.size();
    }
}
