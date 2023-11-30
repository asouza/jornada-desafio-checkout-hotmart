package com.deveficiente.desafiocheckouthotmart.compartilhado;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cada classe tem um determinado número de pontos em função da métrica estabelecida dentro dos
 * princípios do CDD. Essa annotation ajuda a esclarecer.
 *
 * @author albertoluizsouza
 */
@Retention(RetentionPolicy.SOURCE)
@Target({
  ElementType.TYPE,
  ElementType.METHOD,
  ElementType.CONSTRUCTOR,
  ElementType.FIELD,
  ElementType.TYPE_USE,
  ElementType.TYPE_PARAMETER,
  ElementType.LOCAL_VARIABLE
})
public @interface ICP {

  /**
   * @return Número de pontos de complexidade intrínseca da classe(Intrinsic Complex Point). O valor
   *     default é 1
   */
  double value() default 1.0;

  String description() default "";
}
