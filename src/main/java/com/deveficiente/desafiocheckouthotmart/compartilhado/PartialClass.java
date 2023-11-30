package com.deveficiente.desafiocheckouthotmart.compartilhado;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Quando um código é refatorado em função de uma métrica + limite construído em cima do cdd, novas
 * unidades são criadas para distribuir a dificuldade de entendimento. Nem sempre essas unidades tem
 * um nome sugestivo, de vez em quando elas são apenas partes do código maior. Algo que lembra muito
 * as partial classes do C#.
 *
 * <p>Um outro cenário é quando você tem uma classe que só existe para ser usada naquele ponto
 * específico. Um exemplo é o {@link ComparadorTreinamentosDashboard}
 *
 * <p>Essa annotation pega essa inspiração e deve ser usada para explicar que uma classe na verdade
 * é parte de outra.
 *
 * @author albertoluizsouza
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface PartialClass {

  /**
   * @return Classe principal que representa a raiz da adequação aos limites estabelecidos em função
   *     do CDD
   */
  Class<?> value();
}
