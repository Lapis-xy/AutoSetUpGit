package com.example;

// Import standard di sistema e GUI (Nessuna libreria esterna richiesta)
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GitAutomationGUI extends JFrame {

    private JTextField txtRepoUrl;
    private JTextField txtNewRepoName;
    private JCheckBox chkPrivate;
    private JPasswordField txtToken;
    private JTextField txtCommitMessage;
    private JTextArea txtTerminalLog;
    private JButton btnExecute;
    private JLabel lblStatusIndicator;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    // File di memorizzazione del Token sul Desktop Linux
    private static final String DESKTOP_TOKEN_FILE = "/home/informatica/Desktop/autogit_token.txt";

    // Palette Colori Console 100% Dark
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

        setTitle("autogitupSetup - Native Core Engine");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(10, 10));

        // --- HEADER (Titolo Console) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BG_INPUT));
        
        JLabel lblTitle = new JLabel(" [SYSTEM@AUTOMATION-BUS]:~ autogitupSetup --native-mode");
        lblTitle.setFont(FONT_MONO_BOLD);
        lblTitle.setForeground(TEXT_CYAN);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblStatusIndicator = new JLabel("READY ");
        lblStatusIndicator.setFont(FONT_MONO_BOLD);
        lblStatusIndicator.setForeground(TEXT_GREEN);
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblStatusIndicator, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- PANNELLO CENTRALE ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setBackground(BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(BG_PANEL);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(createConsoleBorder(" CONFIG_PARAMETERS "));

        JLabel lblRepo = createConsoleLabel("REPOSITORY REMOTE URL (Se già esistente):");
        txtRepoUrl = createConsoleTextField("");
        
        JLabel lblNewRepo = createConsoleLabel("OPPURE CREA NUOVA REPO (Via Modulo Nativo):");
        txtNewRepoName = createConsoleTextField("");
        
        chkPrivate = new JCheckBox("Repository Privata");
        chkPrivate.setFont(FONT_MONO);
        chkPrivate.setBackground(BG_PANEL);
        chkPrivate.setForeground(TEXT_WHITE);
        chkPrivate.setFocusPainted(false);
        chkPrivate.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblToken = createConsoleLabel("PERSONAL ACCESS TOKEN (PAT) [Richiesto per il Push locale]:");
        txtToken = new JPasswordField();
        styleSecureField(txtToken);

        JLabel lblCommit = createConsoleLabel("FIRST COMMIT MESSAGE:");
        txtCommitMessage = createConsoleTextField("Commit Iniziale");

        btnExecute = new JButton("EXECUTE AUTOMATION ENGINE");
        btnExecute.setFont(FONT_MONO_BOLD);
        btnExecute.setBackground(BG_INPUT);
        btnExecute.setForeground(TEXT_GREEN);
        btnExecute.setFocusPainted(false);
        btnExecute.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExecute.setBorder(BorderFactory.createLineBorder(TEXT_GREEN, 1));
        btnExecute.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExecute.addActionListener(this::handleGitAutomation);

        JTextArea txtInfoBox = new JTextArea(
            "Target Engine: Native Linux Curl Bypass\n" +
            "Branch Policy: default -> [main]\n" +
            "Session Sync: Sandbox indipendente (No Playwright)\n" +
            "Status: Pronto ad eludere i filtri di rete."
        );
        txtInfoBox.setFont(FONT_MONO);
        txtInfoBox.setForeground(TEXT_MUTED);
        txtInfoBox.setBackground(BG_DARK);
        txtInfoBox.setEditable(false);
        txtInfoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BG_INPUT, 1),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        txtInfoBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(lblRepo);
        leftPanel.add(Box.createVerticalStrut(3));
        leftPanel.add(txtRepoUrl);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(lblNewRepo);
        leftPanel.add(Box.createVerticalStrut(3));
        leftPanel.add(txtNewRepoName);
        leftPanel.add(Box.createVerticalStrut(3));
        leftPanel.add(chkPrivate);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(lblToken);
        leftPanel.add(Box.createVerticalStrut(3));
        leftPanel.add(txtToken);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(lblCommit);
        leftPanel.add(Box.createVerticalStrut(3));
        txtCommitMessage = createConsoleTextField("Commit Iniziale");
        leftPanel.add(txtCommitMessage);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(btnExecute);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(txtInfoBox);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BG_PANEL);
        rightPanel.setBorder(createConsoleBorder(" LIVE_TERMINAL_LOG "));

        txtTerminalLog = new JTextArea();
        txtTerminalLog.setEditable(false);
        txtTerminalLog.setBackground(BG_DARK);
        txtTerminalLog.setForeground(TEXT_GREEN);
        txtTerminalLog.setFont(FONT_MONO);
        txtTerminalLog.setCaretColor(TEXT_GREEN);
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

        loadTokenFromDesktop();
        logSystem("Console Native Core caricata. Pronta all'uso.");
    }
    private void handleGitAutomation(ActionEvent e) {
        String repoUrl = txtRepoUrl.getText().trim();
        String newRepoName = txtNewRepoName.getText().trim();
        String token = new String(txtToken.getPassword()).trim();
        String commitMsg = txtCommitMessage.getText().trim();
        boolean isPrivate = chkPrivate.isSelected();

        if (repoUrl.isEmpty() && newRepoName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ERRORE: Inserisci un URL remoto o un nome per la nuova repo.", "System Alert", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (commitMsg.isEmpty()) {
            commitMsg = "Commit Iniziale";
        }

        if (!token.isEmpty()) {
            saveTokenToDesktop(token);
        }

        btnExecute.setEnabled(false);
        btnExecute.setBorder(BorderFactory.createLineBorder(TEXT_MUTED, 1));
        btnExecute.setForeground(TEXT_MUTED);
        lblStatusIndicator.setText("PROCESSING ");
        lblStatusIndicator.setForeground(TEXT_CYAN);
        
        txtTerminalLog.setText(""); 
        String finalCommitMsg = commitMsg;

        new Thread(() -> {
            try {
                String targetRepoUrl = repoUrl;

                // Se l'utente vuole creare una nuova repository, attiva il modulo nativo Linux
                if (targetRepoUrl.isEmpty() && !newRepoName.isEmpty()) {
                    logSystem("[DETECTOR]: Richiesta generazione remota intercettata.");
                    targetRepoUrl = createGitHubRepository(newRepoName, isPrivate);
                    if (targetRepoUrl == null) {
                        throw new RuntimeException("Creazione tramite modulo nativo fallita.");
                    }
                }

                String finalPushUrl = targetRepoUrl;
                if (!token.isEmpty() && targetRepoUrl.startsWith("https://")) {
                    finalPushUrl = targetRepoUrl.replace("https://", "https://" + token + "@");
                }

                logSystem("Verifica moduli Git locali...");
                File gitFolder = new File(".git");
                if (!gitFolder.exists()) {
                    logSystem("Inizializzazione modulo .git locale...");
                    runCommand("git init");
                }

                setupGitignore();
                checkAndCreateReadme();

                logSystem("Aggiornamento area di staging (git add)...");
                runCommand("git add .");

                logSystem("Generazione blocco commit...");
                runCommand("git commit -m \"" + finalCommitMsg + "\"");

                logSystem("Riallineamento branch principale -> [main]");
                runCommand("git branch -M main");

                logSystem("Binding endpoint remoto...");
                executeQuietly("git remote remove origin");
                runCommand("git remote add origin " + targetRepoUrl);

                logSystem("Iniezione pacchetti verso il server remoto...");
                runCommand("git push -u " + finalPushUrl + " main");

                logSystem("Sincronizzazione completata con successo!");
                
                SwingUtilities.invokeLater(() -> {
                    lblStatusIndicator.setText("SUCCESS ");
                    lblStatusIndicator.setForeground(TEXT_GREEN);
                });
                
                JOptionPane.showMessageDialog(this, "Deploy eseguito con successo!", "Console Core Info", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                logSystem("ERRORE CRITICO: Sequenza interrotta.");
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
    private String createGitHubRepository(String repoName, boolean isPrivate) {
        try {
            logSystem("[SYSTEM-CORE]: Avvio modulo nativo Linux di bypass...");
            
            String token = new String(txtToken.getPassword()).trim();
            if (token.isEmpty()) {
                logSystem("[API-ERROR]: Personal Access Token mancante nella GUI.");
                return null;
            }

            String privateValue = isPrivate ? "true" : "false";
            String jsonPayload = "{\"name\":\"" + repoName + "\",\"private\":" + privateValue + "}";
            
            // Comando curl nativo senza dipendenze esterne
            String[] osCommand = {
                "curl",
                "-i", 
                "-s", 
                "-X", "POST",
                "-H", "Authorization: token " + token,
                "-H", "Accept: application/vnd.github.v3+json",
                "-H", "User-Agent: Mozilla/5.0 (X11; Linux x86_64)", 
                "-H", "Content-Type: application/json",
                "-d", jsonPayload,
                "https://github.com"
            };

            logSystem("[API-ENGINE]: Iniezione pacchetto cifrato verso l'endpoint...");
            Process process = new ProcessBuilder(osCommand).redirectErrorStream(true).start();
            
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
            }
            process.waitFor();
            String rawOutput = response.toString();

            // Estrazione sicura del clone_url tramite regex
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"clone_url\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Matcher matcher = pattern.matcher(rawOutput);

            if (matcher.find()) {
                String cloneUrl = matcher.group(1).trim();
                logSystem("[API-SUCCESS]: Repository remota intercettata con successo!");
                return cloneUrl;
            }

            // Analisi dell'intestazione per stanare i blocchi di rete
            if (rawOutput.contains("HTTP/1.1 401") || rawOutput.contains("Bad credentials")) {
                logSystem("[API-ERROR]: Autenticazione fallita. Controlla il Token PAT.");
            } else if (rawOutput.contains("HTTP/1.1 422") || rawOutput.contains("already exists")) {
                logSystem("[API-ERROR]: Nome già in uso. La repo '" + repoName + "' esiste già su GitHub.");
            } else {
                logSystem("[API-ERROR]: Intercettazione di rete rilevata.");
                String[] lines = rawOutput.split("\n");
                if (lines.length > 0) {
                    logConsoleRaw("  [STATO SERVER]: " + lines[0]);
                }
            }

        } catch (Exception e) {
            logSystem("[SYSTEM-CRASH]: Errore nell'esecuzione dell'engine: " + e.getMessage());
        }
        return null;
    }

    private void saveTokenToDesktop(String token) {
        try (FileWriter writer = new FileWriter(DESKTOP_TOKEN_FILE)) {
            writer.write(token);
            logSystem("Token aggiornato e salvato sul Desktop: autogit_token.txt");
        } catch (IOException e) {
            logSystem("[WRITE-ERROR] Impossibile scrivere il file del token sul Desktop.");
        }
    }

    private void loadTokenFromDesktop() {
        File tokenFile = new File(DESKTOP_TOKEN_FILE);
        if (tokenFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(tokenFile))) {
                String savedToken = reader.readLine();
                if (savedToken != null && !savedToken.trim().isEmpty()) {
                    txtToken.setText(savedToken.trim());
                    logSystem("Token configurato caricato dal file autogit_token.txt sul Desktop.");
                }
            } catch (IOException e) {
                logSystem("[READ-ERROR] Errore durante la lettura del file token sul Desktop.");
            }
        }
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
        SwingUtilities.invokeLater(() -> {
            GitAutomationGUI gui = new GitAutomationGUI();
            gui.setVisible(true);
        });
    }
}
