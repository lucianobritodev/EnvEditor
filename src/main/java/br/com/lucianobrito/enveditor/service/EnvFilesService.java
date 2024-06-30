package br.com.lucianobrito.enveditor.service;

import br.com.lucianobrito.enveditor.models.EnvModel;
import br.com.lucianobrito.enveditor.service.enuns.Env;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnvFilesService {
    private static EnvFilesService instance;

    private List<EnvModel> localEnvs;
    private List<EnvModel> globalEnvs;

    private EnvFilesService() {
    }

    public static synchronized EnvFilesService getInstance() {
        if (instance == null) {
            instance = new EnvFilesService();
        }
        return instance;
    }

    public List<String> readEnvFile(Env env) {

        List<String> envs = new ArrayList<>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(env.getValue()));
            String line = bf.readLine();
            while (line != null) {
                readLocalEnvs(env, line, envs);
                line = bf.readLine();
            }

            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return envs;
    }

    private void readLocalEnvs(Env env, String line, List<String> envs) {
        switch (env) {
            case LOCAL:
                if (line.startsWith("export ")) {
                    envs.add(line.replaceAll("^export ", ""));
                }
                break;
            case GLOBAL:
                envs.add(line.replaceAll("^export ", ""));
                break;
        }
    }
}
