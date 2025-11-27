package br.com.zerium.gerenciador.util;

public class Validador {

    // Nome do produto — apenas letras e espaços, mínimo 2 caracteres
    public static boolean nomeValido(String nome) {
        return nome != null && nome.matches("^[A-Za-zÀ-ú\\s]{2,}$");
    }

    // Preço — número positivo maior que zero
    public static boolean precoValido(String precoTexto) {
        try {
            double preco = Double.parseDouble(precoTexto.replace(",", "."));
            return preco > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Quantidade — inteiro positivo maior que zero
    public static boolean quantidadeValida(String qtdTexto) {
        try {
            int qtd = Integer.parseInt(qtdTexto.trim());
            return qtd > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Descrição ou observação — opcional, máximo 255 caracteres
    public static boolean descricaoValida(String texto) {
        return texto == null || texto.trim().length() <= 255;
    }
}
