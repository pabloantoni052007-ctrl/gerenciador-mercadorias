package br.com.zerium.gerenciador.app;

import br.com.zerium.gerenciador.view.TelaGerenciamento;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Define o Look and Feel para Nimbus, que é moderno e multiplataforma
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se o Nimbus não estiver disponível, usa o padrão do sistema
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Executa a criação da tela na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            TelaGerenciamento tela = new TelaGerenciamento();
            tela.setVisible(true);
        });
    }
}
