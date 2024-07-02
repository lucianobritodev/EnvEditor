package br.com.lucianobrito.enveditor.service;

import br.com.lucianobrito.enveditor.models.EnvModel;
import br.com.lucianobrito.enveditor.service.enuns.Env;
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
    private static final String INSTRUCTION_IMPORT = "[ -f " + EnvUtils.USER_HOME + "/.environment ] && source "
            + EnvUtils.USER_HOME + "/.environment;";

    private final Logger LOGGER = Logger.getLogger(EnvFilesService.class.getName());

    private List<EnvModel> localEnvs = new ArrayList<>();
    private List<EnvModel> globalEnvs = new ArrayList<>();

    private EnvFilesService() {
    }

    public static synchronized EnvFilesService getInstance() throws IOException {
        if (instance == null) {
            instance = new EnvFilesService();
            fileHandler = EnvUtils.getFileHander();
            fileHandler.setEncoding("UTF-8");
            fileHandler.setFormatter(new SimpleFormatter());
            instance.LOGGER.addHandler(fileHandler);
        }

        return instance;
    }

    public List<String> readEnvFile(Env env) {

        List<String> envs = new ArrayList<>();
        try {
            createLocalEnvs(env);

            BufferedReader bf = new BufferedReader(new FileReader(env.getValue()));
            String line = bf.readLine();
            while (line != null) {
                clearLine(env, line, envs);
                line = bf.readLine();
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

    private void createLocalEnvs(Env env) throws IOException {
        if (Env.LOCAL.equals(env)) {
            File file = new File(env.getValue());
            if (!file.exists()) {
                Boolean result = file.createNewFile();
                if (!result) {
                    throw new RuntimeException("Erro ao criar o arquivo " + env.getValue());
                }
            }
        }

        configLocalBashrc();
    }

    private void configLocalBashrc() {
        try {
            boolean hasInstruction = false;
            BufferedReader bf = new BufferedReader(new FileReader(BASH_RC));
            String line = bf.readLine();
            while (line != null) {
                if (INSTRUCTION_IMPORT.equals(line)) {
                    hasInstruction = true;
                }
                line = bf.readLine();
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
            file.deleteOnExit();
            file.createNewFile();
            persistFile(file, newEnvModels);

        } catch (IOException e) {
            LOGGER.severe("Erro ao persistir arquivo: " + pathEnvPersist);
            try {
                File file = new File(EnvUtils.ENV_EDITOR_HOME);
                file.deleteOnExit();
                file.createNewFile();
                persistFile(file, newEnvModels);
            } catch (IOException ex) {
                LOGGER.severe("Erro ao tentar reverter a persistência no arquivo: " + pathEnvPersist);
                LOGGER.severe("Motivo: " + e.getMessage());
            }
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
