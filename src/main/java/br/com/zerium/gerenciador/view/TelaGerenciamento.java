package br.com.zerium.gerenciador.view;

import br.com.zerium.gerenciador.dao.MovimentacaoDAO;
import br.com.zerium.gerenciador.dao.ProdutoDAO;
import br.com.zerium.gerenciador.model.ItemPedido;
import br.com.zerium.gerenciador.model.Movimentacao;
import br.com.zerium.gerenciador.model.Pedido;
import br.com.zerium.gerenciador.model.Produto;
import br.com.zerium.gerenciador.util.Validador;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Arrays;

public class TelaGerenciamento extends JFrame {

    private final ProdutoDAO produtoDAO;
    private final MovimentacaoDAO movimentacaoDAO;
    private Integer produtoSelecionadoId = null;

    // Componentes da Aba de Estoque
    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabelaProdutos;
    private JButton botaoSalvarEstoque, botaoDeletarEstoque, botaoLimparForm;
    private JTextField campoNome, campoDescricao, campoPreco, campoQuantidade;

    // Componentes da Aba de Vendas
    private JTable tabelaVendas;
    private DefaultTableModel modeloTabelaVendas;
    private JComboBox<Produto> comboProdutosVenda;
    private JSpinner spinnerQuantidadeVenda;
    private JTextField campoObservacaoVenda;
    private JTextField campoPrecoVenda;

    private Pedido pedidoAtual = new Pedido();
    private JTable tabelaItensPedido;
    private DefaultTableModel modeloTabelaItensPedido;
    private JButton botaoAdicionarItem;
    private JButton botaoFinalizarVenda;



    // Componentes da Aba de Relatório
    private JTable tabelaRelatorio;
    private DefaultTableModel modeloTabelaRelatorio;

    // Cores
    private final Color COR_VERDE = new Color(39, 174, 96);
    private final Color COR_VERMELHO = new Color(192, 57, 43);
    private final Color COR_AZUL = new Color(41, 128, 185);
    private final Color COR_CINZA = new Color(127, 140, 141);

    public TelaGerenciamento() {
        this.produtoDAO = new ProdutoDAO();
        this.movimentacaoDAO = new MovimentacaoDAO();

        configurarJanela();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("Gerenciar Estoque", criarPainelEstoque());
        tabbedPane.addTab("Registrar Venda", criarPainelVendas());
        tabbedPane.addTab("Relatórios", criarPainelRelatorios());

        add(tabbedPane, BorderLayout.CENTER);
        atualizarDados();

    }


    private void configurarJanela() {
        setTitle("Sistema de Gerenciamento de Mercadorias");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private JPanel criarPainelEstoque() {
        JPanel painelEstoque = new JPanel(new BorderLayout(15, 15));
        painelEstoque.setBorder(new EmptyBorder(15, 15, 15, 15));

        modeloTabelaProdutos = new DefaultTableModel(new Object[]{"ID", "Nome", "Descrição", "Preço", "Qtd."}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaProdutos = new JTable(modeloTabelaProdutos);
        configurarTabela(tabelaProdutos);
        painelEstoque.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        JPanel painelFormulario = criarFormularioEstoque();
        painelEstoque.add(painelFormulario, BorderLayout.SOUTH);
        return painelEstoque;
    }

    private JPanel criarFormularioEstoque() {
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Dados do Produto",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; campoNome = new JTextField(20); painelFormulario.add(campoNome, gbc);
        gbc.gridx = 0; gbc.gridy = 1; painelFormulario.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; campoDescricao = new JTextField(); painelFormulario.add(campoDescricao, gbc);
        gbc.gridx = 0; gbc.gridy = 2; painelFormulario.add(new JLabel("Preço (ex: 99.90):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; campoPreco = new JTextField(); painelFormulario.add(campoPreco, gbc);
        gbc.gridx = 0; gbc.gridy = 3; painelFormulario.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; campoQuantidade = new JTextField(); painelFormulario.add(campoQuantidade, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botaoSalvarEstoque = personalizarBotao(new JButton(), "Salvar", COR_VERDE);
        botaoDeletarEstoque = personalizarBotao(new JButton(), "Deletar", COR_VERMELHO);
        botaoLimparForm = personalizarBotao(new JButton(), "Limpar", COR_CINZA);
        painelBotoes.add(botaoLimparForm);
        painelBotoes.add(botaoDeletarEstoque);
        painelBotoes.add(botaoSalvarEstoque);

        gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST; gbc.insets = new Insets(15, 5, 5, 5);
        painelFormulario.add(painelBotoes, gbc);

        adicionarListenersEstoque();
        return painelFormulario;
    }

    private JPanel criarPainelVendas() {
        JPanel painelVendas = new JPanel(new BorderLayout(15, 15));
        painelVendas.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tabela de vendas
        modeloTabelaVendas = new DefaultTableModel(new Object[]{"Data", "Produto Vendido", "Qtd.", "Observação"}, 0);
        tabelaVendas = new JTable(modeloTabelaVendas);
        configurarTabela(tabelaVendas);
        tabelaVendas.setRowHeight(26);
        painelVendas.add(new JScrollPane(tabelaVendas), BorderLayout.CENTER);

        // Formulário de registro de vendas
        JPanel painelFormulario = criarFormularioVenda();

        JScrollPane scrollFormulario = new JScrollPane(painelFormulario);
        scrollFormulario.setBorder(BorderFactory.createEmptyBorder());
        scrollFormulario.setPreferredSize(new Dimension(0, 220));
        painelVendas.add(scrollFormulario, BorderLayout.SOUTH);

        return painelVendas;
    }

    // ======================= ABA RELATÓRIOS =======================
    private JPanel criarPainelRelatorios() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel titulo = new JLabel("Gerar Relatórios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridy = 0;
        painel.add(titulo, gbc);

        JButton botaoValor = new JButton("Relatório de Valor por Produto");
        JButton botaoQuantidade = new JButton("Relatório de Quantidade por Produto");
        JButton botaoMensal = new JButton("Relatório Mensal (Gráfico)");

        // Estilização padrão dos botões
        personalizarBotao(botaoValor, "Relatório de Valor por Produto", COR_AZUL);
        personalizarBotao(botaoQuantidade, "Relatório de Quantidade por Produto", COR_VERDE);
        personalizarBotao(botaoMensal, "Relatório Mensal (Gráfico)", COR_VERMELHO);

        gbc.gridy = 1;
        painel.add(botaoValor, gbc);

        gbc.gridy = 2;
        painel.add(botaoQuantidade, gbc);

        gbc.gridy = 3;
        painel.add(botaoMensal, gbc);

        // Ações dos botões
        botaoValor.addActionListener(e -> gerarRelatorioValor());
        botaoQuantidade.addActionListener(e -> gerarRelatorioQuantidade());
        botaoMensal.addActionListener(e -> abrirRelatorioMensal());

        return painel;
    }

    private void abrirRelatorioMensal() {
        JFrame frame = new JFrame("Relatório de Faturamento Mensal");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(criarPainelRelatorioMensal());
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private void gerarRelatorioQuantidade() {
        try {
            File pasta = new File("relatorios");
            if (!pasta.exists()) pasta.mkdirs();

            File arquivo = new File(pasta, "relatorio_quantidade_produto.txt");

            try (PrintWriter writer = new PrintWriter(arquivo)) {
                writer.println("=== RELATÓRIO DE QUANTIDADE POR PRODUTO ===\n");
                List<Produto> produtos = produtoDAO.listarProdutos();
                for (Produto p : produtos) {
                    writer.printf("ID: %d | Nome: %s | Estoque: %d%n",
                            p.getId(), p.getNome(), p.getQuantidade());
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Relatório gerado com sucesso!\nArquivo salvo em:\n" + arquivo.getAbsolutePath(),
                    "Relatório", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void gerarRelatorioValor() {
        try {
            File pasta = new File("relatorios");
            if (!pasta.exists()) pasta.mkdirs();

            File arquivo = new File(pasta, "relatorio_valor_produto.txt");

            try (PrintWriter writer = new PrintWriter(arquivo)) {
                writer.println("=== RELATÓRIO DE VALOR POR PRODUTO ===\n");
                List<Produto> produtos = produtoDAO.listarProdutos();
                for (Produto p : produtos) {
                    writer.printf("ID: %d | Nome: %s | Preço: R$ %.2f%n",
                            p.getId(), p.getNome(), p.getPreco());
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Relatório gerado com sucesso!\nArquivo salvo em:\n" + arquivo.getAbsolutePath(),
                    "Relatório", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }



    private JPanel criarFormularioVenda() {
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Registrar Nova Venda",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 1 - Produto
        gbc.gridx = 0; gbc.gridy = 0;
        painelFormulario.add(new JLabel("Produto:"), gbc);

        gbc.gridx = 1;
        comboProdutosVenda = new JComboBox<>();
        painelFormulario.add(comboProdutosVenda, gbc);

        // Linha 2 - Quantidade
        gbc.gridx = 0; gbc.gridy = 1;
        painelFormulario.add(new JLabel("Quantidade:"), gbc);

        gbc.gridx = 1;
        spinnerQuantidadeVenda = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        painelFormulario.add(spinnerQuantidadeVenda, gbc);

        // Linha 3 - Preço
        gbc.gridx = 0; gbc.gridy = 2;
        painelFormulario.add(new JLabel("Preço de Venda (R$):"), gbc);

        gbc.gridx = 1;
        campoPrecoVenda = new JTextField();
        painelFormulario.add(campoPrecoVenda, gbc);

        // Linha 4 - Observação
        gbc.gridx = 0; gbc.gridy = 3;
        painelFormulario.add(new JLabel("Observação:"), gbc);

        gbc.gridx = 1;
        campoObservacaoVenda = new JTextField();
        painelFormulario.add(campoObservacaoVenda, gbc);

        // Linha 5 - Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botaoAdicionarItem = personalizarBotao(new JButton(), "Adicionar ao Pedido", COR_AZUL);
        botaoFinalizarVenda = personalizarBotao(new JButton(), "Finalizar Venda", COR_VERDE);
        painelBotoes.add(botaoAdicionarItem);
        painelBotoes.add(botaoFinalizarVenda);

        gbc.gridx = 1; gbc.gridy = 4;
        painelFormulario.add(painelBotoes, gbc);

        // Linha 6 - Tabela de Itens do Pedido
        modeloTabelaItensPedido = new DefaultTableModel(new Object[]{"Produto", "Qtd.", "Preço", "Subtotal"}, 0);
        tabelaItensPedido = new JTable(modeloTabelaItensPedido);
        configurarTabela(tabelaItensPedido);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;
        painelFormulario.add(new JScrollPane(tabelaItensPedido), gbc);

        // Ações
        botaoAdicionarItem.addActionListener(e -> adicionarItemAoPedido());
        botaoFinalizarVenda.addActionListener(e -> finalizarPedido());

        return painelFormulario;
    }

    private void adicionarItemAoPedido() {
        Produto produtoSelecionado = (Produto) comboProdutosVenda.getSelectedItem();
        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto antes de adicionar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantidade = (int) spinnerQuantidadeVenda.getValue();
        if (quantidade <= 0) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double precoVenda;
        try {
            precoVenda = Double.parseDouble(campoPrecoVenda.getText().replace(",", "."));
            if (precoVenda <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um preço válido maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Adiciona ao pedido atual
        pedidoAtual.adicionarItem(produtoSelecionado, quantidade, precoVenda);

        // Atualiza a tabela
        double subtotal = precoVenda * quantidade;
        modeloTabelaItensPedido.addRow(new Object[]{
                produtoSelecionado.getNome(),
                quantidade,
                String.format("R$ %.2f", precoVenda),
                String.format("R$ %.2f", subtotal)
        });

        campoPrecoVenda.setText("");
        spinnerQuantidadeVenda.setValue(1);
    }

    private void finalizarPedido() {
        if (pedidoAtual.getItens().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione ao menos um produto ao pedido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            for (ItemPedido item : pedidoAtual.getItens()) {
                produtoDAO.registrarVenda(item.getProduto().getId(), item.getQuantidade(), item.getPrecoVenda(), "Venda agrupada");
            }

            JOptionPane.showMessageDialog(this,
                    "Pedido finalizado com sucesso!\nFaturamento total: R$ " + String.format("%.2f", pedidoAtual.calcularTotal()),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            modeloTabelaItensPedido.setRowCount(0);
            pedidoAtual = new Pedido();
            atualizarDados();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao finalizar pedido", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JPanel criarPainelRelatorio() {
        JPanel painelRelatorio = new JPanel(new BorderLayout(10, 10));
        painelRelatorio.setBorder(new EmptyBorder(15, 15, 15, 15));

        modeloTabelaRelatorio = new DefaultTableModel(new Object[]{"Data", "Produto", "Tipo", "Qtd. Movida", "Observação"}, 0);
        tabelaRelatorio = new JTable(modeloTabelaRelatorio);
        configurarTabela(tabelaRelatorio);
        painelRelatorio.add(new JScrollPane(tabelaRelatorio), BorderLayout.CENTER);

        JButton botaoAtualizarRelatorio = personalizarBotao(new JButton(), "Atualizar Relatório", COR_AZUL);
        botaoAtualizarRelatorio.addActionListener(e -> carregarDadosRelatorio());

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotao.add(botaoAtualizarRelatorio);

        painelRelatorio.add(painelBotao, BorderLayout.NORTH);
        return painelRelatorio;
    }

    private JPanel criarPainelRelatorioMensal() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));

        MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
        List<Object[]> dados = movimentacaoDAO.listarFaturamentoMensal();


        JPanel painelGrafico = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (dados == null || dados.isEmpty()) {
                    g.drawString("Nenhum dado de venda encontrado.", 100, 100);
                    return;
                }

                // Extrai dados do banco
                int n = dados.size();
                String[] meses = new String[n];
                double[] totalVendas = new double[n];
                int[] pedidos = new int[n];

                for (int i = 0; i < n; i++) {
                    meses[i] = (String) dados.get(i)[0];
                    pedidos[i] = (int) dados.get(i)[1];
                    totalVendas[i] = (double) dados.get(i)[2];
                }

                int largura = getWidth();
                int altura = getHeight();
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Eixos
                g2.setColor(Color.DARK_GRAY);
                g2.drawLine(80, altura - 60, largura - 40, altura - 60);
                g2.drawLine(80, 40, 80, altura - 60);

                // Escalas
                double maxVenda = Arrays.stream(totalVendas).max().orElse(1000);
                int espacamento = (largura - 160) / meses.length;

                // Desenha barras (faturamento)
                for (int i = 0; i < meses.length; i++) {
                    int x = 100 + i * espacamento;
                    int alturaBarra = (int) ((totalVendas[i] / maxVenda) * (altura - 120));
                    int y = altura - 60 - alturaBarra;

                    g2.setColor(new Color(41, 128, 185));
                    g2.fillRect(x, y, 60, alturaBarra);

                    g2.setColor(Color.BLACK);
                    g2.drawString("R$ " + String.format("%.2f", totalVendas[i]), x, y - 5);
                    g2.drawString(meses[i], x + 5, altura - 40);
                }

                // Desenha linha (pedidos)
                g2.setColor(new Color(231, 76, 60));
                int[] xPts = new int[meses.length];
                int[] yPts = new int[meses.length];

                int maxPedidos = Arrays.stream(pedidos).max().orElse(1);

                for (int i = 0; i < meses.length; i++) {
                    int x = 130 + i * espacamento;
                    int y = (int) (altura - 60 - (pedidos[i] * (altura - 120) / (double) maxPedidos));
                    xPts[i] = x;
                    yPts[i] = y;
                    g2.fillOval(x - 4, y - 4, 8, 8);
                    if (i > 0) g2.drawLine(xPts[i - 1], yPts[i - 1], x, y);
                }

                // Legendas
                g2.setColor(new Color(41, 128, 185));
                g2.fillRect(100, 20, 12, 12);
                g2.setColor(Color.BLACK);
                g2.drawString("Faturamento (R$)", 120, 30);

                g2.setColor(new Color(231, 76, 60));
                g2.fillOval(260, 20, 10, 10);
                g2.setColor(Color.BLACK);
                g2.drawString("Pedidos", 280, 30);
            }
        };

        painelGrafico.setPreferredSize(new Dimension(900, 350));
        painel.add(painelGrafico, BorderLayout.CENTER);

        // Lista de resumo abaixo do gráfico
        JTextArea listaMeses = new JTextArea();
        listaMeses.setEditable(false);
        listaMeses.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        if (dados.isEmpty()) {
            listaMeses.setText("Nenhuma venda registrada até o momento.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Object[] linha : dados) {
                String mes = (String) linha[0];
                int pedidos = (int) linha[1];
                double vendas = (double) linha[2];
                sb.append(String.format("%s → Faturamento = R$ %.2f — Pedidos: %d%n", mes, vendas, pedidos));
            }
            listaMeses.setText(sb.toString());
        }

        painel.add(new JScrollPane(listaMeses), BorderLayout.SOUTH);
        return painel;
    }



    private void adicionarListenersEstoque() {
        botaoSalvarEstoque.addActionListener(e -> salvarOuAtualizarProduto());
        botaoDeletarEstoque.addActionListener(e -> deletarProduto());
        botaoLimparForm.addActionListener(e -> limparFormularioEstoque());
        tabelaProdutos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) carregarProdutoParaEdicao();
            }
        });
    }



    private void salvarOuAtualizarProduto() {
        String nome = campoNome.getText();
        String precoStr = campoPreco.getText();
        String qtdStr = campoQuantidade.getText();
        if (nome.trim().isEmpty() || precoStr.trim().isEmpty() || qtdStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, Preço e Quantidade são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }


        if (!Validador.nomeValido(campoNome.getText())) {
            JOptionPane.showMessageDialog(this, "O nome deve conter apenas letras e ter pelo menos 2 caracteres.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Validador.precoValido(campoPreco.getText())) {
            JOptionPane.showMessageDialog(this, "O preço deve ser um número maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Validador.quantidadeValida(campoQuantidade.getText())) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser um número inteiro maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Validador.descricaoValida(campoDescricao.getText())) {
            JOptionPane.showMessageDialog(this, "A descrição é muito longa (máximo 255 caracteres).", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double preco = Double.parseDouble(precoStr.replace(",", "."));
            int quantidade = Integer.parseInt(qtdStr);
            Produto produto = new Produto(nome, campoDescricao.getText(), preco, quantidade);

            if (produtoSelecionadoId == null) {
                produtoDAO.adicionarProduto(produto);
            } else {
                produto.setId(produtoSelecionadoId);
                produtoDAO.atualizarProduto(produto);
            }
            limparFormularioEstoque();
            atualizarDados();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço e Quantidade devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarVenda() {
        Produto produtoSelecionado = (Produto) comboProdutosVenda.getSelectedItem();
        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto antes de registrar a venda.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String qtdTexto = spinnerQuantidadeVenda.getValue().toString();
        if (!Validador.quantidadeValida(qtdTexto)) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser um número maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String precoTexto = campoPrecoVenda.getText().replace(",", ".");
        if (!Validador.precoValido(precoTexto)) {
            JOptionPane.showMessageDialog(this, "O preço de venda deve ser um número maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Validador.descricaoValida(campoObservacaoVenda.getText())) {
            JOptionPane.showMessageDialog(this, "A observação é muito longa (máximo 255 caracteres).", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double precoVenda = Double.parseDouble(precoTexto);
            int quantidade = Integer.parseInt(qtdTexto);

            produtoDAO.registrarVenda(produtoSelecionado.getId(), quantidade, precoVenda, campoObservacaoVenda.getText());
            JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            spinnerQuantidadeVenda.setValue(1);
            campoObservacaoVenda.setText("");
            campoPrecoVenda.setText("");
            atualizarDados();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao Registrar Venda", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deletarProduto() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idProduto = (int) modeloTabelaProdutos.getValueAt(linhaSelecionada, 0);
        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar este produto?", "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacao == JOptionPane.YES_OPTION) {
            produtoDAO.removerProduto(idProduto);
            limparFormularioEstoque();
            atualizarDados();
        }
    }

    private void carregarProdutoParaEdicao() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) return;
        produtoSelecionadoId = (Integer) modeloTabelaProdutos.getValueAt(linha, 0);
        Produto p = produtoDAO.buscarPorId(produtoSelecionadoId);
        if (p != null) {
            campoNome.setText(p.getNome());
            campoDescricao.setText(p.getDescricao());
            campoPreco.setText(String.format("%.2f", p.getPreco()).replace(",", "."));
            campoQuantidade.setText(String.valueOf(p.getQuantidade()));
            botaoSalvarEstoque.setText("Atualizar");
        }
    }

    private void carregarDadosVendas() {
        modeloTabelaVendas.setRowCount(0);
        List<Movimentacao> vendas = movimentacaoDAO.listarVendas();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        for (Movimentacao venda : vendas) {
            modeloTabelaVendas.addRow(new Object[]{
                    sdf.format(venda.getDataMovimentacao()),
                    venda.getNomeProduto(),
                    "-" + venda.getQuantidadeMovida(),
                    venda.getObservacao()
            });
        }
    }

    private void carregarDadosRelatorio() {

        if (modeloTabelaRelatorio == null) return;

        modeloTabelaRelatorio.setRowCount(0);
        List<Movimentacao> movimentacoes = movimentacaoDAO.listarTodas();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        for (Movimentacao mov : movimentacoes) {
            modeloTabelaRelatorio.addRow(new Object[]{
                    sdf.format(mov.getDataMovimentacao()),
                    mov.getNomeProduto(),
                    mov.getTipo(),
                    mov.getTipo().equals("ENTRADA") ? "+" + mov.getQuantidadeMovida() : "-" + mov.getQuantidadeMovida(),
                    mov.getObservacao()
            });
        }
    }


    private void atualizarDados() {
        List<Produto> produtos = produtoDAO.listarProdutos();
        modeloTabelaProdutos.setRowCount(0);
        for (Produto p : produtos) {
            modeloTabelaProdutos.addRow(new Object[]{p.getId(), p.getNome(), p.getDescricao(), p.getPreco(), p.getQuantidade()});
        }

        Produto selecionado = (Produto) comboProdutosVenda.getSelectedItem();
        comboProdutosVenda.removeAllItems();
        for (Produto p : produtos) {
            comboProdutosVenda.addItem(p);
        }
        if (selecionado != null) {
            for (int i = 0; i < comboProdutosVenda.getItemCount(); i++) {
                if (comboProdutosVenda.getItemAt(i).getId() == selecionado.getId()) {
                    comboProdutosVenda.setSelectedIndex(i);
                    break;
                }
            }
        }

        carregarDadosVendas();
        carregarDadosRelatorio();
    }

    private void limparFormularioEstoque() {
        campoNome.setText("");
        campoDescricao.setText("");
        campoPreco.setText("");
        campoQuantidade.setText("");
        produtoSelecionadoId = null;
        botaoSalvarEstoque.setText("Salvar");
        tabelaProdutos.clearSelection();
        campoNome.requestFocusInWindow();
    }

    private void configurarTabela(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setOpaque(false);
        header.setBackground(new Color(69, 73, 74));
        header.setForeground(Color.WHITE);
    }


    private JButton personalizarBotao(JButton botao, String texto, Color cor) {
        botao.setText(texto);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.putClientProperty("JButton.buttonType", "roundRect");
        return botao;
    }
}

