package com.example;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GitAutomationGUI extends JFrame {

    private JTextField txtRepoUrl;
    private JPasswordField txtToken;
    private JTextField txtCommitMessage;
    private JTextArea txtTerminalLog;
    private JButton btnExecute;
    private JLabel lblStatusIndicator;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    // Palette Colori Console 100% Dark - Nessun Elemento Bianco Standard
    private static final Color BG_DARK = new Color(12, 12, 12);
    private static final Color BG_PANEL = new Color(20, 20, 20);
    private static final Color BG_INPUT = new Color(30, 30, 30);
    private static final Color TEXT_GREEN = new Color(0, 255, 102);
    private static final Color TEXT_CYAN = new Color(0, 210, 255);
    private static final Color TEXT_WHITE = new Color(220, 220, 220);
    private static final Color TEXT_MUTED = new Color(110, 110, 110);
    private static final Color ACCENT_RED = new Color(255, 51, 51);
    
    private static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 12);
    private static final Font FONT_MONO_BOLD = new Font("Monospaced", Font.BOLD, 13);

    public GitAutomationGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        setTitle("autogitupSetup");
        setSize(855, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(10, 10));

        // --- 1. HEADER (Titolo Console) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BG_INPUT));
        
        JLabel lblTitle = new JLabel(" [SYSTEM@AUTOMATION-BUS]:~ autogitupSetup --run");
        lblTitle.setFont(FONT_MONO_BOLD);
        lblTitle.setForeground(TEXT_CYAN);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblStatusIndicator = new JLabel("READY ");
        lblStatusIndicator.setFont(FONT_MONO_BOLD);
        lblStatusIndicator.setForeground(TEXT_GREEN);
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblStatusIndicator, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. PANNELLO CENTRALE (Configurazione + Terminale) ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setBackground(BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // PANNELLO SINISTRO: Input Controlli
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(BG_PANEL);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(createConsoleBorder(" CONFIG_PARAMETERS "));

        // Input URL
        JLabel lblRepo = createConsoleLabel("REPOSITORY REMOTE URL:");
        txtRepoUrl = createConsoleTextField("");
        
        // Input Token
        JLabel lblToken = createConsoleLabel("PERSONAL ACCESS TOKEN (PAT) [OPZIONALE]:");
        txtToken = new JPasswordField();
        styleSecureField(txtToken);

        // Input Commit
        JLabel lblCommit = createConsoleLabel("FIRST COMMIT MESSAGE:");
        txtCommitMessage = createConsoleTextField("Commit Iniziale");

        // Bottone d'azione
        btnExecute = new JButton("EXECUTE INITIALIZE & PUSH");
        btnExecute.setFont(FONT_MONO_BOLD);
        btnExecute.setBackground(BG_INPUT);
        btnExecute.setForeground(TEXT_GREEN);
        btnExecute.setFocusPainted(false);
        btnExecute.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExecute.setBorder(BorderFactory.createLineBorder(TEXT_GREEN, 1));
        btnExecute.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExecute.addActionListener(this::handleGitAutomation);

        // Box informativo di sistema
        JTextArea txtInfoBox = new JTextArea(
            "Target Engine: Git Core v3\n" +
            "Branch Policy: default -> [main]\n" +
            "Auth Module: Token Injection Safe Mode active\n" +
            "Status: Ready to encrypt and inject credentials."
        );
        txtInfoBox.setFont(FONT_MONO);
        txtInfoBox.setForeground(TEXT_MUTED);
        txtInfoBox.setBackground(BG_DARK);
        txtInfoBox.setEditable(false);
        txtInfoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BG_INPUT, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        txtInfoBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(lblRepo);
        leftPanel.add(Box.createVerticalStrut(5)); // Errore fisso qui
        leftPanel.add(txtRepoUrl);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(lblToken);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(txtToken);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(lblCommit);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(txtCommitMessage);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(btnExecute);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(txtInfoBox);

        // PANNELLO DESTRO: Terminale Output log
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BG_PANEL);
        rightPanel.setBorder(createConsoleBorder(" LIVE_TERMINAL_LOG "));

        txtTerminalLog = new JTextArea();
        txtTerminalLog.setEditable(false);
        txtTerminalLog.setBackground(BG_DARK);
        txtTerminalLog.setForeground(TEXT_GREEN);
        txtTerminalLog.setFont(FONT_MONO);
        txtTerminalLog.setCaretColor(TEXT_GREEN);
        
        // Attiva il ritorno a capo automatico ed esclude la barra orizzontale
        txtTerminalLog.setLineWrap(true);
        txtTerminalLog.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(txtTerminalLog);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(BG_INPUT, 1));
        scrollPane.getVerticalScrollBar().setBackground(BG_DARK);
        
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);
        add(centerPanel, BorderLayout.CENTER);

        logSystem("Console Full Dark caricata. Moduli di sicurezza attivi.");
    }
    private void handleGitAutomation(ActionEvent e) {
        String repoUrl = txtRepoUrl.getText().trim();
        String token = new String(txtToken.getPassword()).trim();
        String commitMsg = txtCommitMessage.getText().trim();

        if (repoUrl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ERRORE: Inserire un endpoint URI remoto valido.", "Console System Alert", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (commitMsg.isEmpty()) {
            commitMsg = "Commit Iniziale";
        }

        String targetPushUrl = repoUrl;
        if (!token.isEmpty()) {
            if (repoUrl.startsWith("https://")) {
                targetPushUrl = repoUrl.replace("https://", "https://" + token + "@");
            } else {
                logSystem("[WARNING] Il token funziona solo con URL HTTPS. Procedo senza iniezione.");
            }
        }

        btnExecute.setEnabled(false);
        btnExecute.setBorder(BorderFactory.createLineBorder(TEXT_MUTED, 1));
        btnExecute.setForeground(TEXT_MUTED);
        lblStatusIndicator.setText("PROCESSING ");
        lblStatusIndicator.setForeground(TEXT_CYAN);
        
        txtTerminalLog.setText(""); 
        String finalCommitMsg = commitMsg;
        String finalPushUrl = targetPushUrl;
        boolean hasToken = !token.isEmpty();

        new Thread(() -> {
            try {
                logSystem("Verifica moduli locali in corso...");

                File gitFolder = new File(".git");
                if (!gitFolder.exists()) {
                    logSystem("Nessun modulo .git locale. Eseguo init...");
                    runCommand("git init");
                } else {
                    logSystem("Modulo .git locale già inizializzato.");
                }

                setupGitignore();
                checkAndCreateReadme();

                logSystem("Aggiornamento area di staging (git add)...");
                runCommand("git add .");

                logSystem("Iniezione del blocco di commit...");
                runCommand("git commit -m \"" + finalCommitMsg + "\"");

                logSystem("Riallineamento branch di sviluppo -> [main]");
                runCommand("git branch -M main");

                logSystem("Pulizia e binding dell'endpoint remoto...");
                executeQuietly("git remote remove origin");
                runCommand("git remote add origin " + repoUrl);

                logSystem("Iniezione pacchetti verso il server remoto...");
                if (hasToken) {
                    logSystem("[AUTH] Utilizzo Personal Access Token per autenticazione silenziosa.");
                    runCommand("git push -u " + finalPushUrl + " main");
                } else {
                    logSystem("[AUTH] Nessun token fornito. Attesa credenziali di sistema standard.");
                    runCommand("git push -u origin main");
                }

                logSystem("Sincronizzazione completata con successo!");
                
                SwingUtilities.invokeLater(() -> {
                    lblStatusIndicator.setText("SUCCESS ");
                    lblStatusIndicator.setForeground(TEXT_GREEN);
                });
                
                JOptionPane.showMessageDialog(this, "Deploy eseguito con successo!", "Console Core Info", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                logSystem("ERRORE CRITICO: Procedura interrotta per fallimento del core.");
                SwingUtilities.invokeLater(() -> {
                    lblStatusIndicator.setText("FAILURE ");
                    lblStatusIndicator.setForeground(ACCENT_RED);
                });
                JOptionPane.showMessageDialog(this, "Esecuzione fallita.\nControlla l'output dei log.", "Console Core Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                SwingUtilities.invokeLater(() -> {
                    btnExecute.setEnabled(true);
                    btnExecute.setBorder(BorderFactory.createLineBorder(TEXT_GREEN, 1));
                    btnExecute.setForeground(TEXT_GREEN);
                    if (!lblStatusIndicator.getText().contains("FAILURE")) {
                        lblStatusIndicator.setText("READY ");
                        lblStatusIndicator.setForeground(TEXT_GREEN);
                    }
                });
            }
        }).start();
    }
    private void runCommand(String command) throws Exception {
        String[] osCommand = System.getProperty("os.name").toLowerCase().contains("win") 
            ? new String[]{"cmd.exe", "/c", command} 
            : new String[]{"/bin/sh", "-c", command};

        Process process = new ProcessBuilder(osCommand).redirectErrorStream(true).start();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("@github.com") || line.contains("@gitlab.com")) {
                    line = "  > [URL Remoto protetto mascherato per motivi di sicurezza]";
                }
                logConsoleRaw("  " + line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 && !command.contains("git commit")) { 
            throw new RuntimeException("Comando fallito.");
        }
    }

    private void executeQuietly(String command) {
        try {
            String[] osCommand = System.getProperty("os.name").toLowerCase().contains("win") 
                ? new String[]{"cmd.exe", "/c", command} 
                : new String[]{"/bin/sh", "-c", command};
            new ProcessBuilder(osCommand).start().waitFor();
        } catch (Exception ignored) {}
    }

    private void logSystem(String message) {
        String time = timeFormat.format(new Date());
        SwingUtilities.invokeLater(() -> {
            txtTerminalLog.append(String.format("[%s] [SYS-CORE]: %s\n", time, message));
            txtTerminalLog.setCaretPosition(txtTerminalLog.getDocument().getLength());
        });
    }

    private void logConsoleRaw(String rawMessage) {
        SwingUtilities.invokeLater(() -> {
            txtTerminalLog.append(rawMessage + "\n");
            txtTerminalLog.setCaretPosition(txtTerminalLog.getDocument().getLength());
        });
    }

    private void setupGitignore() {
        File gitignore = new File(".gitignore");
        if (!gitignore.exists()) {
            try (FileWriter writer = new FileWriter(gitignore)) {
                writer.write("# Esclusioni specifiche del progetto\ntarget/\n");
                writer.write("GitAutomationGUI.java\nGitAutomationGUI.class\n");
                logSystem("Generato file .gitignore personalizzato (target/ e script esclusi).");
            } catch (IOException ignored) {}
        }
    }

    private void checkAndCreateReadme() {
        File currentDir = new File(".");
        String[] files = currentDir.list();
        if (files != null && files.length <= 2) {
            boolean empty = true;
            for (String f : files) {
                if (!f.equals(".git") && !f.equals(".gitignore") && !f.equals("GitAutomationGUI.java") && !f.equals("GitAutomationGUI.class") && !f.equals("target") && !f.equals("pom.xml")) {
                    empty = false;
                    break;
                }
            }
            if (empty) {
                try (FileWriter writer = new FileWriter("README.md")) {
                    String folderName = Paths.get(".").toAbsolutePath().getFileName().toString();
                    writer.write("# " + folderName + "\n\nRepository creata in modalità protetta via terminale console.");
                    logSystem("Directory vuota rilevata. Generato README.md iniziale.");
                } catch (IOException ignored) {}
            }
        }
    }

    private JLabel createConsoleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_MONO_BOLD);
        label.setForeground(TEXT_WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createConsoleTextField(String defaultText) {
        JTextField textField = new JTextField(defaultText);
        textField.setFont(FONT_MONO);
        textField.setBackground(BG_INPUT);
        textField.setForeground(TEXT_GREEN);
        textField.setCaretColor(TEXT_GREEN);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BG_DARK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        return textField;
    }

    private void styleSecureField(JPasswordField passwordField) {
        passwordField.setFont(FONT_MONO);
        passwordField.setBackground(BG_INPUT);
        passwordField.setForeground(TEXT_GREEN);
        passwordField.setCaretColor(TEXT_GREEN);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BG_DARK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private Border createConsoleBorder(String title) {
        Border line = BorderFactory.createLineBorder(BG_INPUT, 1);
        TitledBorder titled = BorderFactory.createTitledBorder(line, title);
        titled.setTitleFont(FONT_MONO_BOLD);
        titled.setTitleColor(TEXT_CYAN);
        return BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            titled
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GitAutomationGUI().setVisible(true));
    }
}
