package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class GitAutomationGUI extends JFrame {

    private JTextField txtRepoUrl;
    private JTextField txtCommitMessage;
    private JTextArea txtLog;
    private JButton btnExecute;

    public GitAutomationGUI() {
        // Imposta il look and feel nativo per massima compatibilità (GTK su Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Configurazione della Finestra Principale
        setTitle("🚀 Git Auto-Commit & Push Assistant");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra lo schermo
        setLayout(new BorderLayout(10, 10));

        // Pannello Principale con Padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Sezione URL Repository
        JLabel lblRepo = new JLabel("🔗 Link Repository Remota (es. GitHub/GitLab):");
        lblRepo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRepo.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtRepoUrl = new JTextField();
        txtRepoUrl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtRepoUrl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sezione Messaggio di Commit
        JLabel lblCommit = new JLabel("💬 Messaggio del Primo Commit:");
        lblCommit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCommit.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtCommitMessage = new JTextField("feat: Commit iniziale");
        txtCommitMessage.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtCommitMessage.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Bottone di Esecuzione
        btnExecute = new JButton("Avvia Automazione (Commit & Push)");
        btnExecute.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExecute.setBackground(new Color(46, 139, 87)); 
        btnExecute.setForeground(Color.BLACK);
        btnExecute.setFocusPainted(false);
        btnExecute.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExecute.addActionListener(this::handleGitAutomation);

        // Sezione Log / Output
        JLabel lblLog = new JLabel("📋 Stato delle Operazioni (Console):");
        lblLog.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLog.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setBackground(Color.BLACK);
        txtLog.setForeground(Color.GREEN);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtLog);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemblaggio del Layout
        mainPanel.add(lblRepo);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(txtRepoUrl);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(lblCommit);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(txtCommitMessage);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(btnExecute);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(lblLog);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(scrollPane);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void handleGitAutomation(ActionEvent e) {
        String repoUrl = txtRepoUrl.getText().trim();
        String commitMsg = txtCommitMessage.getText().trim();

        if (repoUrl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci un URL valido per la repository!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (commitMsg.isEmpty()) {
            commitMsg = "feat: initial commit";
        }

        btnExecute.setEnabled(false);
        txtLog.setText(""); 

        String finalCommitMsg = commitMsg;
        
        new Thread(() -> {
            try {
                log("[INFO] Avvio procedura Git automatica...\n");

                File gitFolder = new File(".git");
                if (!gitFolder.exists()) {
                    log("[GIT] Inizializzazione nuova repository...\n");
                    runCommand("git init");
                } else {
                    log("[GIT] Cartella .git già esistente.\n");
                }

                setupGitignore();
                checkAndCreateReadme();

                log("[GIT] Aggiunta file all'area di staging...\n");
                runCommand("git add .");

                log("[GIT] Creazione commit: \"" + finalCommitMsg + "\"\n");
                runCommand("git commit -m \"" + finalCommitMsg + "\"");

                log("[GIT] Impostazione branch 'main'...\n");
                runCommand("git branch -M main");

                log("[GIT] Configurazione server remoto...\n");
                executeQuietly("git remote remove origin");
                runCommand("git remote add origin " + repoUrl);

                log("[GIT] Invio dei file sul server (Push). Attendere...\n");
                runCommand("git push -u origin main");

                log("\n✨ OPERAZIONE COMPLETATA CON SUCCESSO! ✨\n");
                JOptionPane.showMessageDialog(this, "Repository caricata con successo su branch 'main'!", "Successo", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                log("\n❌ ERRORE: Operazione interrotta. Verifica la console.");
                JOptionPane.showMessageDialog(this, "Si è verificato un errore.\nAssicurati che Git sia installato e configurato.", "Errore", JOptionPane.ERROR_MESSAGE);
            } finally {
                SwingUtilities.invokeLater(() -> btnExecute.setEnabled(true));
            }
        }).start();
    }

    private void runCommand(String command) throws Exception {
        String[] osCommand = System.getProperty("os.name").toLowerCase().contains("win") 
            ? new String[]{"cmd.exe", "/c", command} 
            : new String[]{"/bin/sh", "-c", command};

        Process process = new ProcessBuilder(osCommand).redirectErrorStream(true).start();
        
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log("  > " + line + "\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 && !command.contains("git commit")) { 
            throw new RuntimeException("Comando fallito con codice: " + exitCode);
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

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append(message);
            txtLog.setCaretPosition(txtLog.getDocument().getLength()); 
        });
    }

    private void setupGitignore() {
        File gitignore = new File(".gitignore");
        if (!gitignore.exists()) {
            try (FileWriter writer = new FileWriter(gitignore)) {
                writer.write("# File di sistema\n.DS_Store\nThumbs.db\n\n# Java e IDEs\n*.class\n*.jar\ntarget/\n.idea/\n*.iml\n.vscode/\n\n# Credenziali\n.env\n");
                log("[INFO] Generato file .gitignore di sicurezza.\n");
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
                    writer.write("# " + folderName + "\n\nRepository creata automaticamente via interfaccia grafica Java.");
                    log("[INFO] Cartella vuota rilevata. Generato README.md iniziale.\n");
                } catch (IOException ignored) {}
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GitAutomationGUI().setVisible(true));
    }
}
