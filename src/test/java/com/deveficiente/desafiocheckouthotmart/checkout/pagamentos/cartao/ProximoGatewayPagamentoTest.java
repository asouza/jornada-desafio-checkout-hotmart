package com.deveficiente.desafiocheckouthotmart.checkout.pagamentos.cartao;

import com.deveficiente.desafiocheckouthotmart.checkout.Compra;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.CartaoGateway1Client;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway1cartao.NovoPagamentoGatewayCartao1Request;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway2cartao.CartaoGateway2Client;
import com.deveficiente.desafiocheckouthotmart.clientesremotos.gateway3cartao.CartaoGateway3Client;
import com.deveficiente.desafiocheckouthotmart.featureflag.FeatureFlagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ProximoGatewayPagamentoTest {

    private CartaoGateway1Client gateway1ClientMock;
    private CartaoGateway2Client gateway2ClientMock;
    private CartaoGateway3Client gateway3ClientMock;
    private FeatureFlagService featureFlagServiceMock;
    private Compra compraMock;

    @BeforeEach
    void setUp() {
        gateway1ClientMock = Mockito.mock(CartaoGateway1Client.class);
        gateway2ClientMock = Mockito.mock(CartaoGateway2Client.class);
        gateway3ClientMock = Mockito.mock(CartaoGateway3Client.class);
        featureFlagServiceMock = Mockito.mock(FeatureFlagService.class);
        compraMock = Mockito.mock(Compra.class);

        // Configuração padrão para os gateways retornarem uma resposta válida
        when(gateway1ClientMock.executa(any(NovoPagamentoGatewayCartao1Request.class))).thenReturn("id-gateway1");
        when(gateway2ClientMock.executa(any())).thenReturn("id-gateway2");
        when(gateway3ClientMock.executa(any())).thenReturn("id-gateway3");
    }

    @Test
    @DisplayName("Deve selecionar o gateway1 quando ele é o único habilitado")
    void deveUsarGateway1QuandoSomenteEleEstaHabilitado() {
        // Arrange
        Function<Compra,String> mockFuncaoGateway1 = compra -> "id-gateway1";
        //alguma coisa acontece na compilacao entre a linha de cima e essa para eu poder passar a funcao como argumento no Optional
        when(featureFlagServiceMock.optionalizeFeature(Mockito.eq("gateway1"), any())).thenReturn(Optional.of(mockFuncaoGateway1));
        when(featureFlagServiceMock.optionalizeFeature(Mockito.eq("gateway2"), any())).thenReturn(Optional.empty());
        when(featureFlagServiceMock.optionalizeFeature(Mockito.eq("gateway3"), any())).thenReturn(Optional.empty());

        ProximoGatewayPagamento proximoGatewayPagamento = new ProximoGatewayPagamento(
                gateway1ClientMock, gateway2ClientMock, gateway3ClientMock, featureFlagServiceMock);

        // Act
        Supplier<String> gatewaySupplier = proximoGatewayPagamento.proximoGateway(compraMock);
        String idTransacao = gatewaySupplier.get();

        // Assert
        assertEquals("id-gateway1", idTransacao);
    }

    @Test
    @DisplayName("Deve selecionar o gateway2 quando gateway1 está desabilitado")
    void deveUsarGateway2QuandoGateway1EstaDesabilitado() {
        // Arrange
        when(featureFlagServiceMock.optionalizeFeature(Mockito.eq("gateway1"), any())).thenReturn(Optional.empty());
        Function<Compra,String> mockFuncaoGateway2 = compra -> "id-gateway2";
        when(featureFlagServiceMock.optionalizeFeature(Mockito.eq("gateway2"), any())).thenReturn(Optional.of(mockFuncaoGateway2));
        when(featureFlagServiceMock.optionalizeFeature(Mockito.eq("gateway3"), any())).thenReturn(Optional.empty());

        ProximoGatewayPagamento proximoGatewayPagamento = new ProximoGatewayPagamento(
                gateway1ClientMock, gateway2ClientMock, gateway3ClientMock, featureFlagServiceMock);

        // Act
        Supplier<String> gatewaySupplier = proximoGatewayPagamento.proximoGateway(compraMock);
        String idTransacao = gatewaySupplier.get();

        // Assert
        assertEquals("id-gateway2", idTransacao);
    }

    @Test
    @DisplayName("Deve selecionar o gateway3 quando gateway1 e gateway2 estão desabilitados")
    void deveUsarGateway3QuandoGateway1E2EstaoDesabilitados() {
        // Arrange
        when(featureFlagServiceMock.optionalizeFeature(Mockito.eq("gateway1"), any())).thenReturn(Optional.empty());
        when(featureFlagServiceMock.optionalizeFeature(Mockito.eq("gateway2"), any())).thenReturn(Optional.empty());
        Function<Compra,String> mockFuncaoGateway3 = compra -> "id-gateway3";
        when(featureFlagServiceMock.optionalizeFeature(Mockito.eq("gateway3"), any())).thenReturn(Optional.of(mockFuncaoGateway3));

        ProximoGatewayPagamento proximoGatewayPagamento = new ProximoGatewayPagamento(
                gateway1ClientMock, gateway2ClientMock, gateway3ClientMock, featureFlagServiceMock);

        // Act
        Supplier<String> gatewaySupplier = proximoGatewayPagamento.proximoGateway(compraMock);
        String idTransacao = gatewaySupplier.get();

        // Assert
        assertEquals("id-gateway3", idTransacao);
    }

    @Test
    @DisplayName("Deve usar o round robin quando todos os gateways estão habilitados")
    void deveUsarRoundRobinQuandoTodosGatewaysEstaoHabilitados() {
        // Arrange
        when(featureFlagServiceMock.optionalizeFeature(anyString(), any())).thenAnswer(invocation -> {
            String gateway = invocation.getArgument(0);
            Function<Compra,String> mockFuncaoGateway = compra -> "id-"+gateway;
            return Optional.of(mockFuncaoGateway);
        });

        ProximoGatewayPagamento proximoGatewayPagamento = new ProximoGatewayPagamento(
                gateway1ClientMock, gateway2ClientMock, gateway3ClientMock, featureFlagServiceMock);

        // Act - Primeira chamada deve usar gateway1
        Supplier<String> gatewaySupplier1 = proximoGatewayPagamento.proximoGateway(compraMock);
        String idTransacao1 = gatewaySupplier1.get();

        // Segunda chamada deve usar gateway2
        Supplier<String> gatewaySupplier2 = proximoGatewayPagamento.proximoGateway(compraMock);
        String idTransacao2 = gatewaySupplier2.get();

        // Terceira chamada deve usar gateway3
        Supplier<String> gatewaySupplier3 = proximoGatewayPagamento.proximoGateway(compraMock);
        String idTransacao3 = gatewaySupplier3.get();

        // Quarta chamada deve voltar para gateway1
        Supplier<String> gatewaySupplier4 = proximoGatewayPagamento.proximoGateway(compraMock);
        String idTransacao4 = gatewaySupplier4.get();

        // Assert
        assertEquals("id-gateway1", idTransacao1);
        assertEquals("id-gateway2", idTransacao2);
        assertEquals("id-gateway3", idTransacao3);
        assertEquals("id-gateway1", idTransacao4);
    }

    @Test
    @DisplayName("Deve lançar exceção quando todos os gateways estão desabilitados")
    void deveLancarExcecaoQuandoTodosGatewaysEstaoDesabilitados() {
        // Arrange
        when(featureFlagServiceMock.optionalizeFeature(anyString(), any())).thenReturn(Optional.empty());

        ProximoGatewayPagamento proximoGatewayPagamento = new ProximoGatewayPagamento(
                gateway1ClientMock, gateway2ClientMock, gateway3ClientMock, featureFlagServiceMock);

        // Act & Assert
        Supplier<String> gatewaySupplier = proximoGatewayPagamento.proximoGateway(compraMock);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, gatewaySupplier::get);
        assertTrue(exception.getMessage().contains("Aparentemente ficamos sem opcoes"));
    }
}