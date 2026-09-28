package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AutoGitPro {

    // Codici ANSI per colorare il testo nel terminale
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";

    public static void main(String[] args) {
        // RIGA CORRETTA:
        Scanner scanner = new Scanner(System.in);
        
        System.out.println(CYAN + BOLD + "============================================================");
        System.out.println(" 🚀 ASSISTENTE AUTOMATICO GIT (JAVA) - PRIMO COMMIT");
        System.out.println("============================================================" + RESET);

        checkGitConfig();

        // 1. Richiesta e validazione URL della Repository
        String repoUrl = "";
        while (true) {
            System.out.print("\n" + BOLD + "🔗 Inserisci il link della repository remota: " + RESET);
            repoUrl = scanner.nextLine().trim();

            if (repoUrl.isEmpty()) {
                System.out.println(RED + "L'URL non può essere vuoto." + RESET);
                continue;
            }

            if (!validateUrl(repoUrl)) {
                System.out.print(YELLOW + "⚠️ L'URL non sembra un link Git standard. Vuoi usarlo comunque? (s/n): " + RESET);
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (!confirm.equals("s")) {
                    continue;
                }
            }
            break;
        }

        // 2. Richiesta del messaggio di commit
        System.out.print("\n" + BOLD + "💬 Inserisci il nome del primo commit " + RESET + "(Default: 'feat: initial commit'): ");
        String commitMessage = scanner.nextLine().trim();
        if (commitMessage.isEmpty()) {
            commitMessage = "feat: initial commit";
        }

        // 3. Inizializzazione Git
        File gitFolder = new File(".git");
        if (!gitFolder.exists()) {
            System.out.println("\n" + CYAN + "⚙️ Inizializzazione della repository Git locale..." + RESET);
            runCommand("git init", "Impossibile inizializzare Git. Verifica che sia installato nel sistema.");
        } else {
            System.out.println("\n" + YELLOW + "📂 Repository Git già esistente nella cartella." + RESET);
        }

        // 4. Configurazione .gitignore e cartella vuota
        setupGitignore();
        checkAndCreateReadme();

        // 5. Area di staging (git add .)
        System.out.println(CYAN + "📦 Aggiunta dei file all'area di staging..." + RESET);
        runCommand("git add .", "Errore durante l'aggiunta dei file (git add).");

        // 6. Esecuzione del Commit
        System.out.println(CYAN + "✍️ Creazione del commit: \"" + commitMessage + "\"..." + RESET);
        runCommand("git commit -m \"" + commitMessage + "\"", "Errore durante il commit. Verifica di avere modifiche da salvare.");

        // 7. Impostazione branch 'main'
        runCommand("git branch -M main", "Impossibile rinominare il branch in 'main'.");

        // 8. Associazione URL remoto (Rimuove il vecchio origin se presente)
        System.out.println(CYAN + "🌐 Associazione dell'URL remoto..." + RESET);
        executeQuietly("git remote remove origin");
        runCommand("git remote add origin " + repoUrl, "Impossibile collegare l'URL remoto.");

        // 9. Push finale
        System.out.println("\n" + YELLOW + "🚀 Caricamento dei file sul server (git push)..." + RESET);
        System.out.println(BOLD + "Nota:" + RESET + " Se è la prima volta, Git potrebbe richiedere l'autenticazione via browser o token.");
        
        runCommand("git push -u origin main", "Errore durante il push. Controlla la connessione o i permessi della repository.");

        System.out.println("\n" + GREEN + BOLD + "✨ OPERAZIONE COMPLETATA CON SUCCESSO! ✨" + RESET);
        System.out.println(GREEN + "La tua repository locale è ora sincronizzata online sul branch 'main'." + RESET);
        System.out.println(CYAN + "============================================================" + RESET + "\n");
        
        scanner.close();
    }

    private static void runCommand(String command, String errorMessage) {
        try {
            String[] osCommand = System.getProperty("os.name").toLowerCase().contains("win") 
                ? new String[]{"cmd.exe", "/c", command} 
                : new String[]{"/bin/sh", "-c", command};

            Process process = new ProcessBuilder(osCommand).inheritIO().start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("\n" + RED + BOLD + "❌ ERRORE: " + errorMessage + RESET);
                System.exit(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("\n" + RED + BOLD + "❌ Eccezione: " + e.getMessage() + RESET);
            System.exit(1);
        }
    }

    private static void executeQuietly(String command) {
        try {
            String[] osCommand = System.getProperty("os.name").toLowerCase().contains("win") 
                ? new String[]{"cmd.exe", "/c", command} 
                : new String[]{"/bin/sh", "-c", command};
            Process process = new ProcessBuilder(osCommand).start();
            process.waitFor();
        } catch (Exception ignored) {}
    }

    private static boolean validateUrl(String url) {
        String regex = "^(https://|git@)(github|gitlab|bitbucket|azure)\\.(com|org|visualstudio)/.*$";
        return Pattern.matches(regex, url);
    }

    private static void setupGitignore() {
        File gitignore = new File(".gitignore");
        if (!gitignore.exists()) {
            System.out.println(CYAN + "📝 Creazione di un file .gitignore predefinito..." + RESET);
            try (FileWriter writer = new FileWriter(gitignore)) {
                writer.write("# File di sistema\n.DS_Store\nThumbs.db\ndesktop.ini\n\n");
                writer.write("# Compilati e dipendenze Java\n*.class\n*.jar\n*.war\n*.ear\ntarget/\n.gradle/\nbuild/\n\n");
                writer.write("# IDEs\n.idea/\n*.iml\n.classpath\n.project\n.settings/\n.vscode/\n\n");
                writer.write("# Configi e log\n*.log\n.env\n");
            } catch (IOException e) {
                System.out.println(YELLOW + "⚠️ Impossibile creare il file .gitignore." + RESET);
            }
        }
    }

    private static void checkAndCreateReadme() {
        File currentDir = new File(".");
        String[] files = currentDir.list();
        if (files != null && files.length <= 2) { 
            boolean soloGitConfig = true;
            for (String file : files) {
                if (!file.equals(".git") && !file.equals(".gitignore")) {
                    soloGitConfig = false;
                    break;
                }
            }
            if (soloGitConfig) {
                System.out.println(CYAN + "📄 La cartella è vuota. Genero un file README.md di base..." + RESET);
                try (FileWriter writer = new FileWriter("README.md")) {
                    String folderName = Paths.get(".").toAbsolutePath().getFileName().toString();
                    writer.write("# " + folderName + "\n\nProgetto inizializzato in automatico.");
                } catch (IOException e) {
                    System.out.println(YELLOW + "⚠️ Impossibile creare il file README.md." + RESET);
                }
            }
        }
    }

    private static void checkGitConfig() {
        try {
            Process p1 = new ProcessBuilder("git", "config", "--global", "user.name").start();
            Process p2 = new ProcessBuilder("git", "config", "--global", "user.email").start();
            p1.waitFor(); p2.waitFor();
            if (p1.exitValue() != 0 || p2.exitValue() != 0) {
                System.out.println(YELLOW + "⚠️ Avviso: Verifica che Git sia configurato con nome ed email globali." + RESET);
            }
        } catch (Exception ignored) {}
    }
}
