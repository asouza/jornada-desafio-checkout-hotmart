package com.deveficiente.desafiocheckouthotmart.checkout;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FormulaCalculoJurosTest {

    private static final int CASAS_DECIMAIS = 4;
    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_EVEN;
    
    @Test
    @DisplayName("Deve calcular corretamente o valor para uma parcela")
    void testUmaParcela() {
        // Given
        BigDecimal valorOriginal = new BigDecimal("100.00");
        BigDecimal taxaAoMes = new BigDecimal("3.5");
        int numeroParcelas = 1;
        
        // When
        BigDecimal resultado = FormulaCalculoJuros.executa(
            valorOriginal, 
            taxaAoMes, 
            numeroParcelas, 
            CASAS_DECIMAIS,
            ARREDONDAMENTO
        );
        
        // Then
        // Em uma parcela, não há juros, então o valor deve ser igual ao valor original
        assertEquals(valorOriginal, resultado, "O valor para uma parcela deve ser igual ao valor original");
    }
    
    @Test
    @DisplayName("Deve calcular corretamente o valor para duas parcelas")
    void testDuasParcelas() {
        // Given
        BigDecimal valorOriginal = new BigDecimal("100.00");
        BigDecimal taxaAoMes = new BigDecimal("3.5");
        int numeroParcelas = 2;
        
        // When
        BigDecimal resultado = FormulaCalculoJuros.executa(
            valorOriginal, 
            taxaAoMes, 
            numeroParcelas, 
            CASAS_DECIMAIS,
            ARREDONDAMENTO
        );
        
        // Then
        // Valor calculado manualmente: 100 * 0.035 * (1.035)² / ((1.035)² - 1) = 51.78...
        BigDecimal valorEsperado = new BigDecimal("52.64");
        
        assertEquals(
            valorEsperado.setScale(5, ARREDONDAMENTO), 
            resultado.setScale(5, ARREDONDAMENTO), 
            "O valor para duas parcelas deve incluir juros corretamente"
        );
    }
    
    /**
     * Provedor de dados para o teste parametrizado
     * Formato: valorOriginal, taxaAoMes, numeroParcelas, valorEsperado
     */
    private static Stream<Arguments> cenariosDeTeste() {
        return Stream.of(
            // Valor, Taxa, Parcelas, Valor Esperado
            Arguments.of(new BigDecimal("100.00"), new BigDecimal("3.5"), 1, new BigDecimal("100.00000")),
            Arguments.of(new BigDecimal("100.00"), new BigDecimal("3.5"), 2, new BigDecimal("52.64")),
            Arguments.of(new BigDecimal("100.00"), new BigDecimal("3.5"), 3, new BigDecimal("35.69")),
            Arguments.of(new BigDecimal("100.00"), new BigDecimal("3.5"), 4, new BigDecimal("27.23")),
            Arguments.of(new BigDecimal("100.00"), new BigDecimal("3.5"), 5, new BigDecimal("22.15")),
            Arguments.of(new BigDecimal("100.00"), new BigDecimal("3.5"), 6, new BigDecimal("18.77")),
            Arguments.of(new BigDecimal("100.00"), new BigDecimal("3.5"), 12, new BigDecimal("10.35")),
            // Diferentes valores e taxas
            Arguments.of(new BigDecimal("500.00"), new BigDecimal("2.0"), 3, new BigDecimal("173.4")),
            Arguments.of(new BigDecimal("1000.00"), new BigDecimal("1.5"), 6, new BigDecimal("175.5")),
            Arguments.of(new BigDecimal("2000.00"), new BigDecimal("5.0"), 12, new BigDecimal("225.6"))
        );
    }
    
    @ParameterizedTest(name = "Valor: {0}, Taxa: {1}%, Parcelas: {2} → Valor parcela: {3}")
    @MethodSource("cenariosDeTeste")
    @DisplayName("Deve calcular corretamente o valor da parcela para diferentes cenários")
    void testMultiplosCenarios(
            BigDecimal valorOriginal, 
            BigDecimal taxaAoMes, 
            int numeroParcelas, 
            BigDecimal valorEsperado) {
        
        // When
        BigDecimal resultado = FormulaCalculoJuros.executa(
            valorOriginal, 
            taxaAoMes, 
            numeroParcelas, 
            CASAS_DECIMAIS,
            ARREDONDAMENTO
        );
        
        // Then
        assertEquals(
            valorEsperado.setScale(5, ARREDONDAMENTO),
            resultado.setScale(5, ARREDONDAMENTO),
            String.format("O cálculo para %d parcelas com taxa de %s%% e valor de %s deve ser correto", 
                          numeroParcelas, taxaAoMes, valorOriginal)
        );
    }
    
    @Test
    @DisplayName("Deve verificar que o valor total pago com juros é maior que o valor original")
    void testValorTotalComJuros() {
        // Given
        BigDecimal valorOriginal = new BigDecimal("100.00");
        BigDecimal taxaAoMes = new BigDecimal("3.5");
        int numeroParcelas = 3;
        
        // When
        BigDecimal valorParcela = FormulaCalculoJuros.executa(
            valorOriginal, 
            taxaAoMes, 
            numeroParcelas, 
            CASAS_DECIMAIS,
            ARREDONDAMENTO
        );
        
        BigDecimal valorTotal = valorParcela.multiply(new BigDecimal(numeroParcelas));
        
        // Then
        // Com juros, o valor total pago deve ser maior que o valor original
        assertEquals(1, valorTotal.compareTo(valorOriginal), 
                "O valor total pago com juros deve ser maior que o valor original");
    }
}