package br.com.lucianobrito.enveditor.service;

import br.com.lucianobrito.enveditor.models.EnvModel;
import br.com.lucianobrito.enveditor.models.enuns.Env;
import br.com.lucianobrito.enveditor.utils.EnvUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class EnvFilesService {

    private static FileHandler fileHandler;
    private static EnvFilesService instance;
    private static final String BASH_RC = EnvUtils.USER_HOME + "/.bashrc";
    private static final String INSTRUCTION_IMPORT = "[ -f " + Env.LOCAL.getValue() + " ] && source ~" + Env.LOCAL.getValue();


    private final Logger LOGGER = Logger.getLogger(EnvFilesService.class.getName());

    private String version;
    private List<EnvModel> localEnvs = new ArrayList<>();
    private List<EnvModel> globalEnvs = new ArrayList<>();

    private EnvFilesService() {
        initConfiguration();
    }

    public static synchronized EnvFilesService getInstance() {
        if (instance == null) {
            instance = new EnvFilesService();
            fileHandler = EnvUtils.getFileHander();
            fileHandler.setFormatter(new SimpleFormatter());
            instance.LOGGER.addHandler(fileHandler);
        }

        return instance;
    }

    public List<String> readEnvFile(Env env) {

        List<String> envs = new ArrayList<>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(env.getValue()));
            String line;
            while ((line = bf.readLine()) != null) {
                clearLine(env, line, envs);
            }

            bf.close();
        } catch (IOException e) {
            LOGGER.warning(() -> "Erro ao ler o arquivo: " + env.getValue());
            LOGGER.severe("Motivo: " + e.getMessage());
        }

        return envs;
    }

    private void clearLine(Env env, String line, List<String> envs) {
        if (line != null) {
            line = line.replaceAll("^export ", "");
            envs.add(line);
        }

        switch (env) {
            case LOCAL:
                this.localEnvs.add(new EnvModel(getEnvKey(line), getEnvValue(line), getEnvKey(line)));
                break;
            case GLOBAL:
                this.globalEnvs.add(new EnvModel(getEnvKey(line), getEnvValue(line), getEnvKey(line)));
                break;
        }
    }

    private void initConfiguration() {
        createLocalEnvFile();
        configLocalBashrc();
    }

    private void createLocalEnvFile() {
        try {
            File file = new File(Env.LOCAL.getValue());
            if (!file.exists()) {
                boolean resultCreateFile = file.createNewFile();
                if (!resultCreateFile) {
                    throw new RuntimeException("Erro ao criar o arquivo " + Env.LOCAL.getValue());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void configLocalBashrc() {
        try {
            boolean hasInstruction = false;
            BufferedReader bf = new BufferedReader(new FileReader(BASH_RC));
            String line;
            while ((line = bf.readLine()) != null) {
                if (INSTRUCTION_IMPORT.equals(line)) {
                    hasInstruction = true;
                }
            }
            bf.close();

            if (!hasInstruction) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(BASH_RC, true));
                bw.newLine();
                bw.append(INSTRUCTION_IMPORT);
                bw.newLine();
                bw.flush();
                bw.close();
            }
        } catch (IOException e) {
            LOGGER.warning(() -> "Erro ao ler ou escrever em arquivo: " + BASH_RC);
            LOGGER.severe("Motivo: " + e.getMessage());
        }
    }

    public void salvar(Env env, List<EnvModel> newEnvModels) {
        String pathEnvPersist = env.getValue();
        try {
            File file = new File(pathEnvPersist);
            persistFile(file, newEnvModels);

            if (Env.GLOBAL.equals(env)) {
                executeCommand("export", String.format("$(xargs < %s)", Env.GLOBAL.getValue()));
            }

        } catch (IOException e) {
            LOGGER.severe("Erro ao persistir arquivo: " + pathEnvPersist);
            try {
                File file = new File(EnvUtils.ENV_EDITOR_HOME);
                file.deleteOnExit();
                boolean resultCreateNewFile = file.createNewFile();
                if (!resultCreateNewFile) {
                    throw new RuntimeException("Erro ao persistir arquivo: " + pathEnvPersist);
                }
                persistFile(file, newEnvModels);
            } catch (IOException ex) {
                LOGGER.severe("Erro ao tentar reverter a persistência no arquivo: " + pathEnvPersist);
                LOGGER.severe("Motivo: " + e.getMessage());
            }
        }
    }

    private boolean executeCommand(String... cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            return process.exitValue() == 0;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void persistFile(File file, List<EnvModel> list) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
        for (EnvModel envModel : list) {
            bw.append(envModel.getChave() + "=" + envModel.getValor());
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    public String getEnvKey(String env) {
        return env.replaceAll("=.*$", "").toUpperCase();
    }

    public String getEnvValue(String env) {
        return env.replaceAll("^.*=", "");
    }
}
