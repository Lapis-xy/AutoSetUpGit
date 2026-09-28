package com.example;

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

    // Percorso specifico impostato sul tuo Desktop Linux
    private static final String DESKTOP_TOKEN_FILE = "/home/informatica/Desktop/autogit_token.txt";

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
        setSize(900, 620);
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

        // --- 2. PANNELLO CENTRALE ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setBackground(BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(BG_PANEL);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(createConsoleBorder(" CONFIG_PARAMETERS "));

        JLabel lblRepo = createConsoleLabel("REPOSITORY REMOTE URL (Se già esistente):");
        txtRepoUrl = createConsoleTextField("");
        
        JLabel lblNewRepo = createConsoleLabel("OPPURE CREA NUOVA REPO SU GITHUB:");
        txtNewRepoName = createConsoleTextField("");
        
        chkPrivate = new JCheckBox("Repository Privata (Richiede Token)");
        chkPrivate.setFont(FONT_MONO);
        chkPrivate.setBackground(BG_PANEL);
        chkPrivate.setForeground(TEXT_WHITE);
        chkPrivate.setFocusPainted(false);
        chkPrivate.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblToken = createConsoleLabel("PERSONAL ACCESS TOKEN (PAT) [GitHub API Required]:");
        txtToken = new JPasswordField();
        styleSecureField(txtToken);

        JLabel lblCommit = createConsoleLabel("FIRST COMMIT MESSAGE:");
        txtCommitMessage = createConsoleTextField("Commit Iniziale");

        btnExecute = new JButton("EXECUTE INITIALIZE & PUSH");
        btnExecute.setFont(FONT_MONO_BOLD);
        btnExecute.setBackground(BG_INPUT);
        btnExecute.setForeground(TEXT_GREEN);
        btnExecute.setFocusPainted(false);
        btnExecute.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExecute.setBorder(BorderFactory.createLineBorder(TEXT_GREEN, 1));
        btnExecute.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExecute.addActionListener(this::handleGitAutomation);

        JTextArea txtInfoBox = new JTextArea(
            "Target Engine: Git Core v4.2\n" +
            "Branch Policy: default -> [main]\n" +
            "Vault System: PAT auto-cached in Desktop/autogit_token.txt\n" +
            "Status: System engine ready."
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
        logSystem("Console Full Dark caricata. Modulo di archiviazione Desktop pronto.");
    }
    private void handleGitAutomation(ActionEvent e) {
        String repoUrl = txtRepoUrl.getText().trim();
        String newRepoName = txtNewRepoName.getText().trim();
        String token = new String(txtToken.getPassword()).trim();
        String commitMsg = txtCommitMessage.getText().trim();
        boolean isPrivate = chkPrivate.isSelected();

        if (repoUrl.isEmpty() && newRepoName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ERRORE: Inserisci un URL remoto o un nome per creare una nuova repository.", "System Alert", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (token.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ERRORE: Il Token di GitHub è obbligatorio per l'autenticazione API.", "System Alert", JOptionPane.ERROR_MESSAGE);
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

                // CASO 1: Creazione nuova repository
                if (targetRepoUrl.isEmpty() && !newRepoName.isEmpty()) {
                    logSystem("[API-GITHUB] Richiesta creazione remota attiva: " + newRepoName);
                    targetRepoUrl = createGitHubRepository(newRepoName, isPrivate, token);
                    if (targetRepoUrl == null) {
                        throw new RuntimeException("Creazione remota fallita.");
                    }
                    logSystem("[API-GITHUB] Repository online generata!");
                } 
                // CASO 2: URL esistente -> Sincronizza la privacy su GitHub prima del push locale
                else if (!targetRepoUrl.isEmpty()) {
                    logSystem("[API-GITHUB] Sincronizzazione visibilità per repository esistente...");
                    updateRepositoryVisibility(targetRepoUrl, isPrivate, token);
                }

                String finalPushUrl = targetRepoUrl;
                if (!token.isEmpty() && targetRepoUrl.startsWith("https://")) {
                    finalPushUrl = targetRepoUrl.replace("https://", "https://" + token + "@");
                }

                logSystem("Verifica moduli locali...");
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
    private void updateRepositoryVisibility(String repoUrl, boolean isPrivate, String token) {
        try {
            String cleanUrl = repoUrl.replace("https://github.com", "").replace(".git", "");
            String[] parts = cleanUrl.split("/");
            if (parts.length < 2) {
                logSystem("[API-ERROR]: Impossibile decodificare l'URL della repository per modificare la privacy.");
                return;
            }
            String owner = parts[0];
            String repoName = parts[1];

            String privateValue = isPrivate ? "true" : "false";
            String jsonPayload = "{\"private\":" + privateValue + "}";

            // Comando PATCH ufficiale API GitHub
            String[] osCommand = {
                "curl", "-i", "-s", "-X", "PATCH",
                "-H", "Authorization: token " + token,
                "-H", "Accept: application/vnd.github.v3+json",
                "-H", "User-Agent: Mozilla/5.0 (X11; Linux x86_64)",
                "-H", "Content-Type: application/json",
                "-d", jsonPayload,
                "https://github.com" + owner + "/" + repoName
            };

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

            if (rawOutput.contains("HTTP/1.1 200") || rawOutput.contains("\"private\": " + privateValue)) {
                logSystem("[API-SUCCESS]: Visibilità aggiornata a [" + (isPrivate ? "PRIVATA" : "PUBBLICA") + "] su GitHub!");
            } else {
                logSystem("[API-ERROR]: Mancata sincronizzazione privacy. Verifica permessi Token.");
                String[] lines = rawOutput.split("\n");
                if (lines.length > 0) {
                    logConsoleRaw("  [DETTAGLIO-SERVER]: " + java.util.Arrays.toString(lines));
                }
            }
        } catch (Exception e) {
            logSystem("[API-EXCEPTION]: Errore durante il cambio privacy: " + e.getMessage());
        }
    }

    private String createGitHubRepository(String repoName, boolean isPrivate, String token) {
        try {
            logSystem("[AGGIRO-RETE]: Generazione repository tramite modulo di sistema nativo...");
            
            String privateValue = isPrivate ? "true" : "false";
            String jsonPayload = "{\"name\":\"" + repoName + "\",\"private\":" + privateValue + "}";
            
            String[] osCommand = {
                "curl", "-i", "-s",
                "-H", "Authorization: token " + token, 
                "-H", "Accept: application/vnd.github.v3+json",
                "-H", "User-Agent: Mozilla/5.0 (X11; Linux x86_64)",
                "-H", "Content-Type: application/json",
                "-d", jsonPayload, 
                "https://github.com"
            };

            Process process = new ProcessBuilder(osCommand).redirectErrorStream(true).start();
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
            }
            process.waitFor();
            String jsonResponse = response.toString().trim();

            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"clone_url\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Matcher matcher = pattern.matcher(jsonResponse);

            if (matcher.find()) {
                String cloneUrl = matcher.group(1).trim();
                logSystem("[API-GITHUB] URL clonato intercettato con successo!");
                return cloneUrl;
            } 
            
            if (jsonResponse.contains("Bad credentials")) {
                logSystem("[API-ERROR]: Token non valido o scaduto.");
            } else if (jsonResponse.contains("already exists")) {
                logSystem("[API-ERROR]: Errore! Una repository con questo nome esiste già.");
            } else if (jsonResponse.contains("Requires authentication") || jsonResponse.contains("Not Found")) {
                logSystem("[API-ERROR]: Accesso negato. Controlla se il Token ha i permessi 'repo'.");
            } else {
                logSystem("[API-ERROR]: Risposta imprevista da GitHub.");
                String[] lines = jsonResponse.split("\n");
                if (lines.length > 0) {
                    logConsoleRaw("  [DEBUG-SERVER]: " + java.util.Arrays.toString(lines)); 
                }
            }
        } catch (Exception e) {
            logSystem("[API-EXCEPTION]: Errore critico di bypass: " + e.getMessage());
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
private JTextField createConsoleTextField(String defaultText) {JTextField textField = new JTextField(defaultText);textField.setFont(FONT_MONO);textField.setBackground(BG_INPUT);textField.setForeground(TEXT_GREEN);textField.setCaretColor(TEXT_GREEN);textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BG_DARK, 1),BorderFactory.createEmptyBorder(5, 5, 5, 5)));textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));textField.setAlignmentX(Component.LEFT_ALIGNMENT);return textField;}private void styleSecureField(JPasswordField passwordField) {passwordField.setFont(FONT_MONO);passwordField.setBackground(BG_INPUT);passwordField.setForeground(TEXT_GREEN);passwordField.setCaretColor(TEXT_GREEN);passwordField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BG_DARK, 1),BorderFactory.createEmptyBorder(5, 5, 5, 5)));passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);}private Border createConsoleBorder(String title) {Border line = BorderFactory.createLineBorder(BG_INPUT, 1);TitledBorder titled = BorderFactory.createTitledBorder(line, title);titled.setTitleFont(FONT_MONO_BOLD);titled.setTitleColor(TEXT_CYAN);return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),titled);}public static void main(String[] args) {SwingUtilities.invokeLater(() -> new GitAutomationGUI().setVisible(true));

}}