package br.com.zerium.gerenciador.view;

import br.com.zerium.gerenciador.dao.ProdutoDAO;
import br.com.zerium.gerenciador.model.Produto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TelaGerenciamento extends JFrame {

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;
    private JButton botaoListar;
    private JButton botaoSalvar;
    private JButton botaoDeletar;
    private JTextField campoNome;
    private JTextField campoDescricao;
    private JTextField campoPreco;
    private JTextField campoQuantidade;
    private ProdutoDAO produtoDAO;

    private final Color COR_BOTAO_SALVAR = new Color(46, 204, 113);   // Verde Esmeralda
    private final Color COR_BOTAO_DELETAR = new Color(231, 76, 60);  // Vermelho Alizarin
    private final Color COR_BOTAO_PADRAO = new Color(52, 152, 219);  // Azul Peter River

    public TelaGerenciamento() {
        produtoDAO = new ProdutoDAO();
        configurarJanela();
        inicializarComponentes();
        adicionarListeners();
        atualizarTabela();
    }

    private void configurarJanela() {
        setTitle("Sistema de Gerenciamento de Mercadorias");
        setSize(850, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
    }

    private void inicializarComponentes() {
        // --- PAINEL DA TABELA (CENTRO) ---
        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Descrição", "Preço", "Qtd."}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(modeloTabela);
        tabelaProdutos.setFillsViewportHeight(true); // Faz a tabela usar toda a altura disponível
        tabelaProdutos.setRowHeight(25); // Aumenta a altura das linhas
        tabelaProdutos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabelaProdutos.getTableHeader().setBackground(new Color(70, 130, 180)); // SteelBlue
        tabelaProdutos.getTableHeader().setForeground(Color.WHITE);
        tabelaProdutos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        // --- PAINEL DE FORMULÁRIO (SUL) ---
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Dados do Produto", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: Nome
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1; painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.9; campoNome = new JTextField(20); painelFormulario.add(campoNome, gbc);

        // Linha 1: Descrição
        gbc.gridx = 0; gbc.gridy = 1; painelFormulario.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; campoDescricao = new JTextField(); painelFormulario.add(campoDescricao, gbc);

        // Linha 2: Preço e Quantidade
        gbc.gridx = 0; gbc.gridy = 2; painelFormulario.add(new JLabel("Preço (ex: 99.90):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; campoPreco = new JTextField(); painelFormulario.add(campoPreco, gbc);

        gbc.gridx = 0; gbc.gridy = 3; painelFormulario.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; campoQuantidade = new JTextField(); painelFormulario.add(campoQuantidade, gbc);

        // --- PAINEL DE BOTÕES ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        botaoListar = new JButton("Atualizar Lista");
        personalizarBotao(botaoListar, COR_BOTAO_PADRAO);
        painelBotoes.add(botaoListar);

        botaoSalvar = new JButton("Salvar Novo Produto");
        personalizarBotao(botaoSalvar, COR_BOTAO_SALVAR);
        painelBotoes.add(botaoSalvar);

        botaoDeletar = new JButton("Deletar Selecionado");
        personalizarBotao(botaoDeletar, COR_BOTAO_DELETAR);
        painelBotoes.add(botaoDeletar);

        // Painel Sul completo
        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelFormulario, BorderLayout.CENTER);
        painelSul.add(painelBotoes, BorderLayout.SOUTH);

        add(painelSul, BorderLayout.SOUTH);
    }

    private void personalizarBotao(JButton botao, Color corFundo) {
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setBackground(corFundo);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        // O UIManager do Nimbus cuida da opacidade, mas setBorder ajuda na consistência
        botao.setBorder(new EmptyBorder(10, 20, 10, 20));
        adicionarEfeitoHover(botao, corFundo);
    }

    private void adicionarEfeitoHover(JButton botao, Color corOriginal) {
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(corOriginal.brighter()); // Clareia a cor
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(corOriginal); // Volta à cor original
            }
        });
    }

    private void adicionarListeners() {
        botaoListar.addActionListener(e -> atualizarTabela());
        botaoSalvar.addActionListener(e -> adicionarProduto());
        botaoDeletar.addActionListener(e -> deletarProduto());
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        List<Produto> produtos = produtoDAO.listarProdutos();
        for (Produto p : produtos) {
            modeloTabela.addRow(new Object[]{p.getId(), p.getNome(), p.getDescricao(), p.getPreco(), p.getQuantidade()});
        }
    }

    private void adicionarProduto() {
        // ... (código dos métodos adicionar, deletar e limpar campos permanece o mesmo da resposta anterior)
        String nome = campoNome.getText();
        String descricao = campoDescricao.getText();
        String precoStr = campoPreco.getText();
        String qtdStr = campoQuantidade.getText();

        if (nome.trim().isEmpty() || precoStr.trim().isEmpty() || qtdStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, Preço e Quantidade são obrigatórios!", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double preco = Double.parseDouble(precoStr.replace(",", "."));
            int quantidade = Integer.parseInt(qtdStr);

            Produto novoProduto = new Produto(nome, descricao, preco, quantidade);
            produtoDAO.adicionarProduto(novoProduto);

            atualizarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Produto salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço e Quantidade devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarProduto() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto na tabela para deletar.", "Nenhum Produto Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProduto = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja deletar o produto '" + modeloTabela.getValueAt(linhaSelecionada, 1) + "'?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            produtoDAO.removerProduto(idProduto);
            atualizarTabela();
            JOptionPane.showMessageDialog(this, "Produto deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void limparCampos() {
        campoNome.setText("");
        campoDescricao.setText("");
        campoPreco.setText("");
        campoQuantidade.setText("");
        campoNome.requestFocusInWindow(); // Coloca o foco no campo nome
    }
}